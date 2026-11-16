const { randomUUID } = require('node:crypto');
const uuid = value => typeof value === 'string' && /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(value);
const erro = (status, message) => Object.assign(new Error(message), { status });

async function registrarPresenca(pool, encontroId, alunoId) {
    if (!uuid(encontroId) || !uuid(alunoId)) throw erro(400, 'Encontro ou aluno inválido.');
    const client = await pool.connect();
    try {
        await client.query('BEGIN');
        const encontro = (await client.query('SELECT * FROM app_encontros WHERE id = $1 FOR SHARE', [encontroId])).rows[0];
        if (!encontro) throw erro(404, 'Encontro não encontrado.');
        const inscricao = (await client.query(
            `SELECT e.id FROM enrollments e JOIN users u ON u.id = e.user_id
             WHERE e.user_id = $1 AND e.course_id = $2
             AND COALESCE(to_jsonb(u)->>'blacklist', 'false') = 'false' FOR SHARE OF e, u`,
            [alunoId, encontro.course_id])).rows[0];
        if (!inscricao) throw erro(422, 'Aluno não inscrito ou com acesso restrito.');
        const anterior = (await client.query(
            'SELECT id, data_hora FROM app_presencas_encontros WHERE encontro_id = $1 AND enrollment_id = $2',
            [encontroId, inscricao.id])).rows[0];
        if (anterior) {
            await client.query('COMMIT');
            return { ...anterior, confirmado: true, repetido: true };
        }
        if (encontro.status !== 'aberto') throw erro(422, 'O encontro precisa estar aberto para registrar presença.');
        const result = await client.query(
            `INSERT INTO app_presencas_encontros (id, encontro_id, enrollment_id)
             VALUES ($1, $2, $3) ON CONFLICT (encontro_id, enrollment_id) DO NOTHING RETURNING id, data_hora`,
            [randomUUID(), encontroId, inscricao.id]);
        const registro = result.rows[0] || (await client.query(
            'SELECT id, data_hora FROM app_presencas_encontros WHERE encontro_id = $1 AND enrollment_id = $2',
            [encontroId, inscricao.id])).rows[0];
        if (!registro) throw erro(409, 'Não foi possível confirmar. Consulte o histórico.');
        await client.query('COMMIT');
        return { ...registro, confirmado: true, repetido: !result.rows.length };
    } catch (failure) {
        await client.query('ROLLBACK');
        throw failure;
    } finally {
        client.release();
    }
}

function instalar(app, pool, autenticar, administrar) {
    const tratar = fn => async (req, res) => {
        try { await fn(req, res); }
        catch (failure) {
            const status = failure.status || (failure.code === '23503' ? 422 : 503);
            res.status(status).json({ erro: failure.status ? failure.message : 'Não foi possível concluir. Confira a configuração e tente novamente.' });
        }
    };
    const validarId = req => {
        if (!uuid(req.params.id)) throw erro(400, 'ID inválido.');
        return req.params.id;
    };
    app.get('/admin/encontros', administrar, tratar(async (req, res) => {
        res.json((await pool.query('SELECT m.*, c.title AS curso FROM app_encontros m JOIN courses c ON c.id = m.course_id ORDER BY m.data')).rows);
    }));
    app.get('/admin/encontros/:id', administrar, tratar(async (req, res) => {
        const item = (await pool.query('SELECT * FROM app_encontros WHERE id = $1', [validarId(req)])).rows[0];
        if (!item) throw erro(404, 'Encontro não encontrado.');
        res.json(item);
    }));
    app.post('/admin/encontros', administrar, tratar(async (req, res) => {
        const { course_id, titulo, data } = req.body || {};
        if (!uuid(course_id) || typeof titulo !== 'string' || !titulo.trim() || titulo.length > 150 || typeof data !== 'string' || Number.isNaN(Date.parse(data))) throw erro(400, 'Informe curso, título e data válidos.');
        const item = (await pool.query(
            `INSERT INTO app_encontros (id, course_id, titulo, data) VALUES ($1, $2, $3, $4) RETURNING *`,
            [randomUUID(), course_id, titulo.trim(), new Date(data).toISOString()])).rows[0];
        res.status(201).json(item);
    }));
    app.patch('/admin/encontros/:id', administrar, tratar(async (req, res) => {
        const id = validarId(req);
        const status = req.body?.status;
        if (!['agendado', 'aberto', 'encerrado'].includes(status)) throw erro(400, 'Situação inválida.');
        const item = (await pool.query('UPDATE app_encontros SET status = $1 WHERE id = $2 RETURNING *', [status, id])).rows[0];
        if (!item) throw erro(404, 'Encontro não encontrado.');
        res.json(item);
    }));
    app.get('/admin/encontros/:id/alunos', administrar, tratar(async (req, res) => {
        res.json((await pool.query(
            `SELECT u.id, u.name, p.data_hora FROM app_encontros m
             JOIN enrollments e ON e.course_id = m.course_id JOIN users u ON u.id = e.user_id
             LEFT JOIN app_presencas_encontros p ON p.encontro_id = m.id AND p.enrollment_id = e.id
             WHERE m.id = $1 ORDER BY u.name`, [validarId(req)])).rows);
    }));
    app.post('/admin/encontros/:id/presencas', administrar, tratar(async (req, res) => {
        const result = await registrarPresenca(pool, validarId(req), req.body?.aluno_id);
        res.status(result.repetido ? 200 : 201).json(result);
    }));
    app.get('/encontros', autenticar, tratar(async (req, res) => {
        res.json((await pool.query(
            `SELECT m.*, c.title AS curso FROM app_encontros m JOIN courses c ON c.id = m.course_id
             WHERE EXISTS (SELECT 1 FROM users u WHERE u.id = $1 AND u.tipo_usuario = 'responsavel')
             OR EXISTS (SELECT 1 FROM enrollments e WHERE e.user_id = $1 AND e.course_id = m.course_id)
             ORDER BY m.data`, [req.usuario.id])).rows);
    }));
    app.post('/encontros/:id/presencas', autenticar, tratar(async (req, res) => {
        const id = validarId(req);
        const code = req.body?.codigoQrCode;
        let alunoId;
        if (code === 'ENCONTRO-' + id) {
            alunoId = req.usuario.id;
        } else if (typeof code === 'string' && code.startsWith('ALUNO-') && uuid(code.slice(6))) {
            const user = (await pool.query('SELECT tipo_usuario FROM users WHERE id = $1', [req.usuario.id])).rows[0];
            if (user?.tipo_usuario !== 'responsavel') throw erro(403, 'Somente responsáveis registram presença de outro aluno.');
            alunoId = code.slice(6);
        } else throw erro(400, 'QR inválido.');
        const result = await registrarPresenca(pool, id, alunoId);
        res.status(result.repetido ? 200 : 201).json(result);
    }));
    app.get('/me/presencas-encontros', autenticar, tratar(async (req, res) => {
        res.json((await pool.query(
            `SELECT p.id, c.title AS curso, m.titulo AS encontro, m.id AS encontro_id, p.data_hora
             FROM app_presencas_encontros p JOIN app_encontros m ON m.id = p.encontro_id
             JOIN enrollments e ON e.id = p.enrollment_id JOIN courses c ON c.id = m.course_id
             WHERE e.user_id = $1 ORDER BY p.data_hora DESC`, [req.usuario.id])).rows);
    }));
}

module.exports = { instalar, registrarPresenca };
