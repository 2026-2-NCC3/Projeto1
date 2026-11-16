const { test } = require('node:test');
const assert = require('node:assert/strict');
const { instalar, registrarPresenca } = require('../recursos/encontros');
const aluno = '11111111-1111-4111-8111-111111111111';
const encontro = '22222222-2222-4222-8222-222222222222';
const outro = '33333333-3333-4333-8333-333333333333';

function banco({ status = 'aberto', inscrito = true, existente = false, falha = false } = {}) {
    const chamadas = [];
    const registros = new Map();
    if (existente) registros.set(encontro, { id: 'presenca', data_hora: '2026-11-16T12:00:00Z' });
    const client = {
        async query(sql, args) {
            chamadas.push({ sql, args });
            if (['BEGIN', 'COMMIT', 'ROLLBACK'].includes(sql)) return { rows: [] };
            if (sql.startsWith('SELECT * FROM app_encontros')) return { rows: [{ id: args[0], course_id: 'curso', status }] };
            if (sql.includes('FROM enrollments e')) return { rows: inscrito ? [{ id: 'inscricao' }] : [] };
            if (sql.startsWith('SELECT id, data_hora')) return { rows: registros.has(args[0]) ? [registros.get(args[0])] : [] };
            if (sql.startsWith('INSERT INTO app_presencas_encontros')) {
                if (falha) throw Error('simulated write failure');
                const row = { id: args[0], data_hora: '2026-11-16T12:00:00Z' };
                registros.set(args[1], row); return { rows: [row] };
            }
            throw Error('Unexpected SQL');
        },
        release() { chamadas.push({ sql: 'RELEASE' }); }
    };
    return { connect: async () => client, chamadas, registros };
}

test('dois encontros do mesmo curso aceitam presenças distintas e repetição é idempotente', async () => {
    const pool = banco();
    const primeira = await registrarPresenca(pool, encontro, aluno);
    const segunda = await registrarPresenca(pool, outro, aluno);
    const repetida = await registrarPresenca(pool, encontro, aluno);
    assert.equal(primeira.confirmado, true);
    assert.notEqual(primeira.id, segunda.id);
    assert.equal(repetida.id, primeira.id);
    assert.equal(repetida.repetido, true);
    assert.equal(pool.registros.size, 2);
    assert.match(pool.chamadas.find(x => x.sql.startsWith('INSERT')).sql, /ON CONFLICT \(encontro_id, enrollment_id\) DO NOTHING/);
});

test('encontro encerrado e aluno não inscrito não geram presença', async () => {
    for (const options of [{ status: 'encerrado' }, { status: 'agendado' }, { inscrito: false }]) {
        const pool = banco(options);
        await assert.rejects(registrarPresenca(pool, encontro, aluno), { status: 422 });
        assert.equal(pool.registros.size, 0);
        assert.equal(pool.chamadas.at(-2).sql, 'ROLLBACK');
        assert.equal(pool.chamadas.at(-1).sql, 'RELEASE');
    }
});

test('reenvio após fechamento confirma presença já existente', async () => {
    const pool = banco({ status: 'encerrado', existente: true });
    assert.equal((await registrarPresenca(pool, encontro, aluno)).repetido, true);
});

test('falha de escrita desfaz transação e libera conexão', async () => {
    const pool = banco({ falha: true });
    await assert.rejects(registrarPresenca(pool, encontro, aluno));
    assert.equal(pool.chamadas.at(-2).sql, 'ROLLBACK');
    assert.equal(pool.chamadas.at(-1).sql, 'RELEASE');
});

test('UUID inválido é rejeitado antes de abrir conexão', async () => {
    const pool = banco();
    await assert.rejects(registrarPresenca(pool, 'invalid', aluno), { status: 400 });
    assert.equal(pool.chamadas.length, 0);
});

test('rotas protegem administração e identidade do aluno', async () => {
    const routes = new Map();
    const auth = () => {}, admin = () => {};
    const app = Object.fromEntries(['get','post','patch'].map(method => [method, (url,...handlers) => routes.set(method+' '+url,handlers)]));
    const pool = banco();
    const queries = [];
    pool.query = async (sql,args) => { queries.push({sql,args}); return { rows: [{ tipo_usuario: 'aluno' }] }; };
    instalar(app,pool,auth,admin);
    for (const [name, handlers] of routes) assert.equal(handlers[0], name.includes('/admin/') ? admin : auth);
    const response = () => ({ code:200, status(code){this.code=code;return this;}, json(data){this.data=data;return this;} });
    const handler = routes.get('post /encontros/:id/presencas').at(-1);
    let res=response();
    await handler({params:{id:encontro},usuario:{id:aluno},body:{codigoQrCode:'ALUNO-'+outro}},res);
    assert.equal(res.code,403);
    assert.equal(pool.chamadas.length,0);
    res=response();
    await handler({params:{id:encontro},usuario:{id:aluno},body:{codigoQrCode:'ENCONTRO-'+encontro,aluno_id:outro}},res);
    assert.equal(res.code,201);
    assert.equal(pool.chamadas.find(x=>x.sql.includes('FROM enrollments e')).args[0],aluno);
    res=response();
    await routes.get('get /me/presencas-encontros').at(-1)({usuario:{id:aluno},query:{aluno_id:outro}},res);
    assert.deepEqual(queries.at(-1).args,[aluno]);
});
