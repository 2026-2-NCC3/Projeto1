const { test } = require('node:test');
const assert = require('node:assert/strict');
const registrar = require('../recursos/rotas');

function preparar(rows = []) {
    const rotas = new Map();
    const consultas = [];
    const autenticar = () => {};
    const administrar = () => {};
    const app = {};
    for (const metodo of ['get', 'post']) {
        app[metodo] = (url, ...etapas) => rotas.set(metodo + ' ' + url, etapas);
    }
    const pool = {
        async query(sql, parametros) {
            consultas.push({ sql, parametros });
            return { rows };
        }
    };
    registrar(app, pool, autenticar, administrar);
    async function chamar(chave, body = {}, query = {}) {
        const etapas = rotas.get(chave);
        const resposta = {
            codigo: 200,
            status(codigo) { this.codigo = codigo; return this; },
            json(dados) { this.dados = dados; return this; }
        };
        await etapas.at(-1)({ usuario: { id: 'aluno-autenticado' }, body, query }, resposta);
        return resposta;
    }
    return { rotas, consultas, autenticar, administrar, chamar };
}

test('todas as rotas exigem autenticação ou chave administrativa', () => {
    const contexto = preparar();
    for (const [nome, etapas] of contexto.rotas) {
        assert.equal(etapas[0], nome.includes('/admin/') ? contexto.administrar : contexto.autenticar);
    }
});

test('consultas privadas usam identidade da sessão e não a enviada pelo cliente', async () => {
    const contexto = preparar();
    for (const caminho of ['comunicados', 'novidades', 'certificados', 'atendimento', 'resultado-perfil']) {
        await contexto.chamar('get /me/' + caminho, { usuario_id: 'outro-aluno' });
        assert.deepEqual(contexto.consultas.at(-1).parametros, ['aluno-autenticado']);
    }
});

test('mensagens vazias ou acima do limite não acessam o banco', async () => {
    const contexto = preparar();
    for (const texto of ['', '   ', 'a'.repeat(2001), 123]) {
        assert.equal((await contexto.chamar('post /me/atendimento', { texto })).codigo, 400);
    }
    assert.equal(contexto.consultas.length, 0);
});

test('aluno não pode escolher o autor nem o destinatário da própria mensagem', async () => {
    const contexto = preparar([{ id: 'mensagem' }]);
    const resposta = await contexto.chamar('post /me/atendimento', {
        texto: '  Preciso de ajuda  ', autor: 'equipe', usuario_id: 'outro-aluno'
    });
    assert.equal(resposta.codigo, 201);
    assert.deepEqual(contexto.consultas[0].parametros, ['aluno-autenticado', 'Preciso de ajuda']);
    assert.match(contexto.consultas[0].sql, /'aluno'/);
});

test('certificado rejeita documento sem HTTPS antes de consultar o banco', async () => {
    const contexto = preparar();
    const resposta = await contexto.chamar('post /admin/certificados', {
        usuario_id: 'e77341d0-711e-4a70-835b-e24f848d7951',
        curso_id: 'e77341d0-711e-4a70-835b-e24f848d7951',
        documento_url: 'http://example.com/certificado.pdf'
    });
    assert.equal(resposta.codigo, 400);
    assert.equal(contexto.consultas.length, 0);
});
