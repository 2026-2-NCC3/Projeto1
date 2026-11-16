'use strict';

const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const QRCode = require('./vendor/QRCode');
const QRErrorCorrectLevel = require('./vendor/QRCode/QRErrorCorrectLevel');
const integracao = require('./integracao');

const ROOT = __dirname;
const PUBLIC = path.join(ROOT, 'public');
const envPath = path.join(ROOT, '.env');

if (
  process.env.NODE_ENV !== 'test' &&
  fs.existsSync(envPath) &&
  typeof process.loadEnvFile === 'function'
) {
  process.loadEnvFile(envPath);
}

const IS_RENDER = process.env.RENDER === 'true';
const PORT = Number(process.env.PORT || 3000);
const HOST = process.env.HOST || (IS_RENDER ? '0.0.0.0' : '127.0.0.1');
const EMAIL = process.env.ADMIN_EMAIL || '';
const PASSWORD = process.env.ADMIN_PASSWORD || '';
const SECURE_COOKIE = IS_RENDER || process.env.COOKIE_SECURE === 'true';

const LIVE_COURSES_URL =
  process.env.COURSES_API_URL ||
  'https://proxima-etapa-api-ncc3.onrender.com/courses';

const MAX_BODY = 110_000;
const SESSION_TTL = 8 * 60 * 60 * 1000;
const sessions = new Map();
const loginAttempts = new Map();
const dbVersions = new WeakMap();

const uuid = value =>
  typeof value === 'string' &&
  /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(value);

const clean = (value, max = 500) =>
  typeof value === 'string' ? value.trim().slice(0, max) : '';

const now = () => new Date().toISOString();
const newId = () => crypto.randomUUID();

const isDate = value =>
  typeof value === 'string' &&
  !Number.isNaN(Date.parse(value));

function initialData() {
  return {
    revision: 1,
    description: 'Dados do painel administrativo Próxima Etapa.',
    courses: [],
    meetings: [],
    students: [],
    attendances: [],
    announcements: [],
    messages: [],
    lastImport: null
  };
}

function databaseError(message, status = 503) {
  return Object.assign(new Error(message), { status });
}

function databaseConfig() {
  const base = (process.env.SUPABASE_URL || '').trim();
  const key = (process.env.SUPABASE_SECRET_KEY || '').trim();

  if (!base || !key) {
    throw databaseError(
      'Configure SUPABASE_URL e SUPABASE_SECRET_KEY no servidor.'
    );
  }

  let url;

  try {
    url = new URL('/rest/v1/painel_web_dados', base);
  } catch {
    throw databaseError('SUPABASE_URL inválida.');
  }

  if (
    url.protocol !== 'https:' ||
    url.username ||
    url.password
  ) {
    throw databaseError('SUPABASE_URL precisa ser um endereço HTTPS válido.');
  }

  if (!key.startsWith('sb_secret_')) {
    throw databaseError(
      'SUPABASE_SECRET_KEY precisa conter a chave secreta iniciada por sb_secret_.'
    );
  }

  return { url, key };
}

async function databaseRequest(method, filters = {}, body) {
  const { url, key } = databaseConfig();

  for (const [name, value] of Object.entries(filters)) {
    url.searchParams.set(name, value);
  }

  let response;

  try {
    response = await fetch(url, {
      method,
      redirect: 'error',
      headers: {
        apikey: key,
        Accept: 'application/json',
        'Content-Type': 'application/json',
        Prefer: 'return=representation',
        'Accept-Profile': 'public',
        'Content-Profile': 'public'
      },
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: AbortSignal.timeout(15000)
    });
  } catch {
    throw databaseError(
      'Não foi possível confirmar a operação no Supabase. Atualize o painel antes de tentar novamente.'
    );
  }

  if (!response.ok) {
    console.error(`Supabase: HTTP ${response.status}`);

    if (response.status === 409) {
      throw databaseError(
        'Outra operação alterou os dados. Atualize o painel e tente novamente.',
        409
      );
    }

    throw databaseError(
      'Falha ao acessar o banco do painel. Confira a configuração do Supabase.'
    );
  }

  try {
    return await response.json();
  } catch {
    throw databaseError(
      'Resposta inesperada do banco. Atualize o painel antes de tentar novamente.'
    );
  }
}

function validateDatabase(db) {
  const collections = [
    'courses',
    'meetings',
    'students',
    'attendances',
    'announcements',
    'messages'
  ];

  if (
    !db ||
    typeof db !== 'object' ||
    Array.isArray(db) ||
    collections.some(name => !Array.isArray(db[name]))
  ) {
    throw databaseError(
      'Os dados do painel têm um formato inválido. Nenhum dado foi substituído.'
    );
  }

  return db;
}

async function loadDB() {
  const rows = await databaseRequest('GET', {
    id: 'eq.principal',
    select: 'dados,atualizado_em'
  });

  if (!Array.isArray(rows) || rows.length > 1) {
    throw databaseError('Resposta inválida ao consultar o painel.');
  }

  if (rows.length === 0) {
    const db = initialData();
    dbVersions.set(db, null);
    return db;
  }

  const row = rows[0];

  if (!Number.isFinite(Date.parse(row.atualizado_em))) {
    throw databaseError('Versão dos dados inválida.');
  }

  const db = validateDatabase(row.dados);
  dbVersions.set(db, row.atualizado_em);

  return db;
}

async function saveDB(db) {
  validateDatabase(db);

  if (!dbVersions.has(db)) {
    throw databaseError(
      'Carregue os dados antes de tentar salvá-los.'
    );
  }

  const previousVersion = dbVersions.get(db);

  const nextTime = previousVersion === null
    ? Date.now()
    : Math.max(Date.now(), Date.parse(previousVersion) + 1);

  const record = {
    dados: db,
    atualizado_em: new Date(nextTime).toISOString()
  };

  let rows;

  if (previousVersion === null) {
    rows = await databaseRequest('POST', {}, {
      id: 'principal',
      ...record
    });
  } else {
    rows = await databaseRequest(
      'PATCH',
      {
        id: 'eq.principal',
        atualizado_em: `eq.${previousVersion}`
      },
      record
    );
  }

  if (!Array.isArray(rows) || rows.length !== 1) {
    throw databaseError(
      'Outra operação alterou os dados. Atualize o painel e tente novamente.',
      409
    );
  }

  if (!Number.isFinite(Date.parse(rows[0].atualizado_em))) {
    throw databaseError(
      'Não foi possível confirmar a versão salva. Atualize o painel.'
    );
  }

  dbVersions.set(db, rows[0].atualizado_em);
}

function send(res, status, payload, extraHeaders = {}) {
  const text = JSON.stringify(payload);

  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Cache-Control': 'no-store',
    ...extraHeaders
  });

  res.end(text);
}

function fail(res, status, message) {
  send(res, status, { error: message });
}

function headers(res) {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('Referrer-Policy', 'no-referrer');
  res.setHeader('X-Frame-Options', 'DENY');
  res.setHeader('Cache-Control', 'no-store');

  res.setHeader(
    'Content-Security-Policy',
    "default-src 'self'; img-src 'self' data:; font-src 'self'; style-src 'self' 'unsafe-inline'; script-src 'self'; connect-src 'self'; object-src 'none'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'"
  );
}

function sessionCookie(token, maxAge) {
  return (
    `pe_session=${token}; HttpOnly; SameSite=Strict; Path=/; Max-Age=${maxAge}` +
    (SECURE_COOKIE ? '; Secure' : '')
  );
}

function getSession(req) {
  const cookie = req.headers.cookie || '';
  const match = cookie.match(
    /(?:^|;\s*)pe_session=([0-9a-f]{64})(?:;|$)/
  );

  const token = match?.[1];

  if (!token) return null;

  const session = sessions.get(token);

  if (!session || session.expires < Date.now()) {
    if (session) sessions.delete(token);
    return null;
  }

  return {
    token,
    email: session.email
  };
}

function sameText(a, b) {
  const ah = crypto
    .createHash('sha256')
    .update(String(a))
    .digest();

  const bh = crypto
    .createHash('sha256')
    .update(String(b))
    .digest();

  return crypto.timingSafeEqual(ah, bh);
}

async function readBody(req) {
  const chunks = [];
  let count = 0;

  for await (const chunk of req) {
    count += chunk.length;

    if (count > MAX_BODY) {
      throw Object.assign(
        new Error('JSON muito grande.'),
        { status: 413 }
      );
    }

    chunks.push(chunk);
  }

  if (count === 0) return {};

  let body;

  try {
    body = JSON.parse(Buffer.concat(chunks).toString('utf8'));
  } catch {
    throw Object.assign(
      new Error('JSON inválido.'),
      { status: 400 }
    );
  }

  if (!body || typeof body !== 'object' || Array.isArray(body)) {
    throw Object.assign(
      new Error('Envie um objeto JSON válido.'),
      { status: 400 }
    );
  }

  return body;
}

function qrSvg(data) {
  const qr = new QRCode(0, QRErrorCorrectLevel.M);

  qr.addData(data);
  qr.make();

  const quiet = 4;
  const cell = 7;
  const total = qr.getModuleCount();
  const size = (total + quiet * 2) * cell;

  let rects = '';

  for (let row = 0; row < total; row++) {
    for (let col = 0; col < total; col++) {
      if (qr.isDark(row, col)) {
        rects +=
          `<rect x="${(col + quiet) * cell}" ` +
          `y="${(row + quiet) * cell}" ` +
          `width="${cell}" height="${cell}"/>`;
      }
    }
  }

  return (
    `<svg xmlns="http://www.w3.org/2000/svg" ` +
    `width="${size}" height="${size}" ` +
    `viewBox="0 0 ${size} ${size}">` +
    '<rect width="100%" height="100%" fill="#ffffff"/>' +
    `<g fill="#103f39">${rects}</g></svg>`
  );
}

function courseInput(body, existing = {}) {
  const title = clean(body.title, 140);
  const date = clean(body.course_date, 60);

  const total = Number(
    body.total_spots ?? existing.total_spots ?? 30
  );

  const available = Number(
    body.available_spots ?? existing.available_spots ?? total
  );

  if (
    !title ||
    !isDate(date) ||
    !Number.isInteger(total) ||
    total < 1 ||
    !Number.isInteger(available) ||
    available < 0 ||
    available > total
  ) {
    throw Object.assign(
      Error('Informe título, data e vagas válidos.'),
      { status: 400 }
    );
  }

  const points = Number(body.points_awarded) || 0;

  if (!Number.isFinite(points)) {
    throw Object.assign(
      Error('Informe uma pontuação válida.'),
      { status: 400 }
    );
  }

  return {
    ...existing,
    title,
    description: clean(body.description, 2000),
    location: clean(body.location, 160),
    course_date: new Date(date).toISOString(),
    category: clean(body.category, 60) || 'geral',
    total_spots: total,
    available_spots: available,
    points_awarded: Math.max(0, points),
    has_certificate: !!body.has_certificate,
    is_active: body.is_active !== false
  };
}

function meetingInput(body, db, existing = {}) {
  const title = clean(body.title, 150);
  const date = clean(body.date, 60);
  const courseId = clean(body.courseId, 50);

  if (
    !title ||
    !isDate(date) ||
    !db.courses.some(course => course.id === courseId)
  ) {
    throw Object.assign(
      Error('Informe nome, curso e data válidos.'),
      { status: 400 }
    );
  }

  return {
    ...existing,
    title,
    date: new Date(date).toISOString(),
    courseId,
    location: clean(body.location, 120),
    status: ['agendado', 'aberto', 'encerrado'].includes(body.status)
      ? body.status
      : 'agendado'
  };
}

function studentInput(body, db, existing = {}) {
  const name = clean(body.name, 140);
  const email = clean(body.email, 180).toLowerCase();

  if (
    !name ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) ||
    db.students.some(
      student =>
        student.email === email &&
        student.id !== existing.id
    )
  ) {
    throw Object.assign(
      Error('Informe nome e e-mail únicos e válidos.'),
      { status: 400 }
    );
  }

  const enrollments = Array.isArray(body.enrollments)
    ? body.enrollments.filter(
        id => db.courses.some(course => course.id === id)
      )
    : existing.enrollments || [];

  return {
    ...existing,
    name,
    email,
    phone: clean(body.phone, 40),
    status: body.status === 'inativo' ? 'inativo' : 'ativo',
    enrollments: [...new Set(enrollments)]
  };
}

const uuidPart = raw => raw.split('/').filter(Boolean);

async function route(req, res, url) {
  const pathname = url.pathname;
  const method = req.method;
  const session = getSession(req);

  if (pathname === '/api/login' && method === 'POST') {
    if (req.headers['x-panel-request'] !== '1') {
      return fail(res, 403, 'Solicitação não permitida.');
    }

    const ip = req.socket.remoteAddress || 'local';

    const previous = loginAttempts.get(ip) || {
      count: 0,
      since: Date.now()
    };

    if (Date.now() - previous.since > 10 * 60 * 1000) {
      previous.count = 0;
      previous.since = Date.now();
    }

    if (previous.count >= 15) {
      return fail(
        res,
        429,
        'Muitas tentativas. Tente novamente mais tarde.'
      );
    }

    const data = await readBody(req);

    if (
      !EMAIL ||
      !PASSWORD ||
      PASSWORD === 'SUBSTITUA_POR_UMA_SENHA_FORTE'
    ) {
      return fail(
        res,
        503,
        'Configure ADMIN_EMAIL e ADMIN_PASSWORD para acessar o painel.'
      );
    }

    if (
      !sameText(data.email || '', EMAIL) ||
      !sameText(data.password || '', PASSWORD)
    ) {
      previous.count++;
      loginAttempts.set(ip, previous);

      return fail(res, 401, 'E-mail ou senha incorretos.');
    }

    loginAttempts.delete(ip);

    const token = crypto.randomBytes(32).toString('hex');

    sessions.set(token, {
      email: EMAIL,
      expires: Date.now() + SESSION_TTL
    });

    res.setHeader('Set-Cookie', sessionCookie(token, 28800));

    return send(res, 200, {
      authenticated: true,
      email: EMAIL,
      demo: true
    });
  }

  if (pathname === '/api/session' && method === 'GET') {
    return send(res, 200, {
      authenticated: !!session,
      email: session?.email || null,
      demo: true
    });
  }

  if (!session) {
    return fail(res, 401, 'Faça login no painel.');
  }

  if (
    !['GET', 'HEAD'].includes(method) &&
    req.headers['x-panel-request'] !== '1'
  ) {
    return fail(res, 403, 'Solicitação não permitida.');
  }

  if (pathname === '/api/logout' && method === 'POST') {
    sessions.delete(session.token);
    res.setHeader('Set-Cookie', sessionCookie('', 0));

    return send(res, 200, { ok: true });
  }

  if (pathname.startsWith('/api/integracao/')) {
    if (pathname === '/api/integracao/qr' && method === 'GET') {
      const id = url.searchParams.get('id');
      if (!uuid(id)) return fail(res, 400, 'Encontro inválido.');
      await integracao.solicitar(`/admin/encontros/${id}`, 'GET');
      res.writeHead(200, { 'Content-Type': 'image/svg+xml; charset=utf-8', 'Cache-Control': 'no-store' });
      return res.end(qrSvg(`ENCONTRO-${id}`));
    }
    const target = integracao.destino(pathname, method);
    const result = await integracao.solicitar(target, method, ['POST', 'PATCH'].includes(method) ? await readBody(req) : undefined);
    return send(res, result.status, result.data);
  }

  if (pathname === '/api/meta' && method === 'GET') {
    return send(res, 200, {
      mode: 'painel-independente',
      storage: 'supabase',
      productionWrites: false,
      remoteReadOnly: true,
      apiUrl: LIVE_COURSES_URL,
      qrMeetingAppSupported: false
    });
  }

  if (pathname === '/api/qr' && method === 'GET') {
    const type = url.searchParams.get('type');
    const id = url.searchParams.get('id');
    const db = await loadDB();

    let payload;

    if (
      type === 'student' &&
      db.students.some(item => item.id === id)
    ) {
      payload = `ALUNO-${id}`;
    } else if (
      type === 'meeting' &&
      db.meetings.some(item => item.id === id)
    ) {
      payload = `ENCONTRO-${id}`;
    } else {
      return fail(res, 404, 'QR não encontrado.');
    }

    const svg = qrSvg(payload);

    res.writeHead(200, {
      'Content-Type': 'image/svg+xml; charset=utf-8',
      'Cache-Control': 'no-store'
    });

    return res.end(svg);
  }

  if (pathname === '/api/courses/import' && method === 'POST') {
    let response;

    try {
      response = await fetch(LIVE_COURSES_URL, {
        method: 'GET',
        headers: { Accept: 'application/json' },
        signal: AbortSignal.timeout(60000)
      });
    } catch {
      return fail(
        res,
        502,
        'A API de cursos não respondeu em até 60 segundos ou está indisponível. Aguarde um momento e tente novamente. Os dados do painel foram preservados.'
      );
    }

    if (!response.ok) {
      return fail(
        res,
        502,
        'A API de cursos respondeu com erro. Dados do painel preservados.'
      );
    }

    let courses;

    try {
      courses = await response.json();
    } catch {
      return fail(res, 502, 'Resposta inválida da API.');
    }

    if (!Array.isArray(courses)) {
      return fail(
        res,
        502,
        'A API não retornou uma lista de cursos.'
      );
    }

    const db = await loadDB();

    let added = 0;
    let updated = 0;

    for (const raw of courses) {
      if (
        !raw ||
        typeof raw !== 'object' ||
        !uuid(raw.id) ||
        !clean(raw.title, 140) ||
        !isDate(raw.course_date)
      ) {
        continue;
      }

      const existing = db.courses.find(
        course => course.id === raw.id
      );

      const normalized = {
        id: raw.id,
        title: clean(raw.title, 140),
        description: clean(raw.description, 2000),
        location: clean(raw.location, 160),
        course_date: new Date(raw.course_date).toISOString(),
        category: clean(raw.category, 60) || 'geral',
        total_spots:
          Number.isInteger(Number(raw.total_spots)) &&
          Number(raw.total_spots) > 0
            ? Number(raw.total_spots)
            : 30,
        available_spots: Number.isInteger(
          Number(raw.available_spots)
        )
          ? Number(raw.available_spots)
          : 30,
        points_awarded: Number.isFinite(Number(raw.points_awarded))
          ? Math.max(0, Number(raw.points_awarded))
          : 0,
        has_certificate: !!raw.has_certificate,
        is_active: raw.is_active !== false,
        origin: 'api-readonly'
      };

      normalized.available_spots = Math.max(
        0,
        Math.min(
          normalized.total_spots,
          normalized.available_spots
        )
      );

      if (existing) {
        if (existing.origin !== 'local') {
          Object.assign(existing, normalized);
          updated++;
        }
      } else {
        db.courses.push(normalized);
        added++;
      }
    }

    db.lastImport = now();
    await saveDB(db);

    return send(res, 200, {
      ok: true,
      added,
      updated,
      total: courses.length,
      lastImport: db.lastImport
    });
  }

  const db = await loadDB();

  if (pathname === '/api/overview' && method === 'GET') {
    const recent = db.meetings
      .map(meeting => ({
        ...meeting,
        courseTitle:
          db.courses.find(
            course => course.id === meeting.courseId
          )?.title || 'Curso removido',
        presence: db.attendances.filter(
          attendance => attendance.meetingId === meeting.id
        ).length
      }))
      .sort((a, b) => b.date.localeCompare(a.date))
      .slice(0, 5);

    return send(res, 200, {
      stats: {
        courses: db.courses.length,
        meetings: db.meetings.length,
        students: db.students.length,
        attendance: db.attendances.length,
        announcements: db.announcements.length,
        messages: db.messages.length
      },
      recent,
      lastImport: db.lastImport
    });
  }

  const entities = {
    courses: 'courses',
    meetings: 'meetings',
    students: 'students',
    announcements: 'announcements',
    messages: 'messages',
    attendances: 'attendances'
  };

  const segments = uuidPart(pathname);
  const type = segments[1];
  const id = segments[2];

  if (
    segments[0] !== 'api' ||
    !Object.hasOwn(entities, type) ||
    segments.length > 3
  ) {
    return fail(res, 404, 'Rota não encontrada.');
  }

  if (method === 'GET' && !id) {
    let result = db[type];

    if (
      type === 'messages' &&
      uuid(url.searchParams.get('studentId'))
    ) {
      result = result.filter(
        item =>
          item.studentId === url.searchParams.get('studentId')
      );
    }

    if (
      type === 'attendances' &&
      uuid(url.searchParams.get('meetingId'))
    ) {
      result = result.filter(
        item =>
          item.meetingId === url.searchParams.get('meetingId')
      );
    }

    return send(res, 200, {
      items: result,
      lastImport: db.lastImport
    });
  }

  if (method === 'GET' && id) {
    const item = db[type].find(record => record.id === id);

    return item
      ? send(res, 200, { item })
      : fail(res, 404, 'Registro não encontrado.');
  }

  if (method === 'POST' && !id) {
    const body = await readBody(req);
    let item;

    if (type === 'courses') {
      item = {
        ...courseInput(body),
        id: newId(),
        origin: 'local'
      };
    } else if (type === 'meetings') {
      item = {
        ...meetingInput(body, db),
        id: newId(),
        demo: false
      };
    } else if (type === 'students') {
      item = {
        ...studentInput(body, db),
        id: newId()
      };
    } else if (type === 'announcements') {
      if (
        !clean(body.title, 150) ||
        !clean(body.message, 4000)
      ) {
        return fail(res, 400, 'Informe título e mensagem.');
      }

      item = {
        id: newId(),
        title: clean(body.title, 150),
        message: clean(body.message, 4000),
        audience: 'todos',
        createdAt: now(),
        demo: true
      };
    } else if (type === 'messages') {
      if (
        !db.students.some(
          student => student.id === body.studentId
        ) ||
        !clean(body.text, 2000)
      ) {
        return fail(
          res,
          400,
          'Escolha um aluno e digite uma mensagem.'
        );
      }

      item = {
        id: newId(),
        studentId: body.studentId,
        author: 'equipe',
        text: clean(body.text, 2000),
        createdAt: now(),
        demo: true
      };
    } else if (type === 'attendances') {
      const meeting = db.meetings.find(
        record => record.id === body.meetingId
      );

      const student = db.students.find(
        record => record.id === body.studentId
      );

      if (
        !meeting ||
        !student ||
        student.status !== 'ativo'
      ) {
        return fail(res, 400, 'Encontro ou aluno inválido.');
      }

      if (meeting.status === 'encerrado') {
        return fail(res, 409, 'Este encontro está encerrado. Reabra-o antes de registrar presença.');
      }

      if (
        !Array.isArray(student.enrollments) ||
        !student.enrollments.includes(meeting.courseId)
      ) {
        return fail(
          res,
          422,
          'Aluno não inscrito no curso deste encontro.'
        );
      }

      if (
        db.attendances.some(
          attendance =>
            attendance.meetingId === meeting.id &&
            attendance.studentId === student.id
        )
      ) {
        return fail(
          res,
          409,
          'Presença já registrada para este encontro.'
        );
      }

      if (
        !['aluno', 'encontro', 'manual'].includes(body.method)
      ) {
        return fail(
          res,
          400,
          'Método de check-in inválido.'
        );
      }

      item = {
        id: newId(),
        meetingId: meeting.id,
        studentId: student.id,
        method: body.method,
        createdAt: now(),
        demo: true
      };
    }

    db[type].push(item);
    await saveDB(db);

    return send(res, 201, { item });
  }

  if (
    (method === 'PUT' || method === 'PATCH') &&
    id &&
    ['courses', 'meetings', 'students'].includes(type)
  ) {
    const old = db[type].find(record => record.id === id);

    if (!old) {
      return fail(res, 404, 'Registro não encontrado.');
    }

    const body = await readBody(req);
    const input = method === 'PATCH' ? { ...old, ...body } : body;

    const item =
      type === 'courses'
        ? courseInput(input, old)
        : type === 'meetings'
          ? meetingInput(input, db, old)
          : studentInput(input, db, old);

    if (type === 'courses') {
      item.origin = 'local';
    }

    Object.assign(old, item);
    await saveDB(db);

    return send(res, 200, { item: old });
  }

  if (
    method === 'DELETE' &&
    id &&
    ['courses', 'meetings', 'announcements', 'students'].includes(type)
  ) {
    const index = db[type].findIndex(record => record.id === id);

    if (index < 0) {
      return fail(res, 404, 'Registro não encontrado.');
    }

    if (
      type === 'courses' &&
      db.meetings.some(meeting => meeting.courseId === id)
    ) {
      return fail(
        res,
        409,
        'Exclua os encontros relacionados antes.'
      );
    }

    if (type === 'courses') {
      for (const student of db.students) {
        if (Array.isArray(student.enrollments)) {
          student.enrollments = student.enrollments.filter(
            courseId => courseId !== id
          );
        }
      }
    }

    if (type === 'students') {
      db.messages = db.messages.filter(
        message => message.studentId !== id
      );

      db.attendances = db.attendances.filter(
        attendance => attendance.studentId !== id
      );
    }

    if (type === 'meetings') {
      db.attendances = db.attendances.filter(
        attendance => attendance.meetingId !== id
      );
    }

    db[type].splice(index, 1);
    await saveDB(db);

    return send(res, 200, { ok: true });
  }

  return fail(res, 405, 'Operação não suportada.');
}

function serveStatic(req, res, pathname) {
  let safe;

  try {
    safe = decodeURIComponent(pathname);
  } catch {
    return fail(res, 400, 'Endereço inválido.');
  }

  if (
    safe.includes('\\') ||
    safe.includes('\0') ||
    safe.split('/').includes('..')
  ) {
    return fail(res, 403, 'Acesso negado.');
  }

  const relative =
    safe === '/' ? 'index.html' : safe.replace(/^\//, '');

  const file = path.resolve(PUBLIC, relative);

  if (
    file !== PUBLIC &&
    !file.startsWith(PUBLIC + path.sep)
  ) {
    return fail(res, 403, 'Acesso negado.');
  }

  if (
    !fs.existsSync(file) ||
    !fs.statSync(file).isFile()
  ) {
    return fail(res, 404, 'Página não encontrada.');
  }

  const ext = path.extname(file).toLowerCase();

  const mime = {
    '.html': 'text/html; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.js': 'text/javascript; charset=utf-8',
    '.svg': 'image/svg+xml',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.jpeg': 'image/jpeg',
    '.webp': 'image/webp',
    '.ico': 'image/x-icon'
  }[ext] || 'application/octet-stream';

  res.writeHead(200, { 'Content-Type': mime });

  if (req.method === 'HEAD') {
    return res.end();
  }

  const stream = fs.createReadStream(file);

  stream.on('error', () => {
    res.destroy();
  });

  stream.pipe(res);
}

const server = http.createServer(async (req, res) => {
  headers(res);

  try {
    const url = new URL(req.url, 'http://localhost');

    if (
      url.pathname === '/health' &&
      (req.method === 'GET' || req.method === 'HEAD')
    ) {
      if (req.method === 'HEAD') {
        res.writeHead(200);
        res.end();
      } else {
        send(res, 200, { status: 'ok' });
      }

      return;
    }

    if (url.pathname.startsWith('/api/')) {
      await route(req, res, url);
    } else if (
      req.method === 'GET' ||
      req.method === 'HEAD'
    ) {
      serveStatic(req, res, url.pathname);
    } else {
      fail(res, 405, 'Método não permitido.');
    }
  } catch (error) {
    if (!res.headersSent) {
      fail(
        res,
        error.status || 500,
        error.status
          ? error.message
          : 'Erro inesperado do servidor.'
      );
    } else {
      res.destroy();
    }

    console.error(
      error.status
        ? 'Validação: ' + error.message
        : 'Erro interno ao processar a solicitação.'
    );
  }
});

const cleanupTimer = setInterval(() => {
  const current = Date.now();

  for (const [token, session] of sessions) {
    if (session.expires < current) {
      sessions.delete(token);
    }
  }

  for (const [ip, attempt] of loginAttempts) {
    if (current - attempt.since > 10 * 60 * 1000) {
      loginAttempts.delete(ip);
    }
  }
}, 60_000);

cleanupTimer.unref();

server.on('error', error => {
  console.error(
    error.code === 'EADDRINUSE'
      ? `A porta ${PORT} já está em uso. Encerre o outro painel antes de iniciar este.`
      : 'Não foi possível iniciar o servidor.'
  );

  if (require.main === module) {
    process.exitCode = 1;
  }
});

if (require.main === module) {
  if (!EMAIL || !PASSWORD) {
    console.warn(
      'Configure ADMIN_EMAIL e ADMIN_PASSWORD antes de usar o painel.'
    );
  }

  try {
    databaseConfig();

    server.listen(PORT, HOST, () => {
      console.log(
        `Próxima Etapa: http://${HOST}:${PORT}`
      );
    });
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}

module.exports = {
  server,
  initialData,
  qrSvg,
  loadDB,
  saveDB
};
