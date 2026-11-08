const { test } = require('node:test');
const assert = require('node:assert/strict');
const { calcularResultado } = require('../recursos/perfil');
const teste = {
    perguntas: [0, 1].map(() => ({ alternativas: [{ perfil: 'a' }, { perfil: 'b' }] })),
    perfis: { a: { titulo: 'A', descricao: 'Área A' }, b: { titulo: 'B', descricao: 'Área B' } }
};
test('rejeita questionário incompleto e alternativas inválidas', () => {
    for (const respostas of [null, [], [0], [-1, 0], [2, 0], ['0', 0]]) {
        assert.throws(() => calcularResultado(teste, respostas));
    }
});
test('calcula predominância sem omitir o aviso educacional', () => {
    const resultado = calcularResultado(teste, [0, 0]);
    assert.equal(resultado.titulo, 'A');
    assert.deepEqual(resultado.pontos, { a: 2, b: 0 });
    assert.match(resultado.aviso, /Não é diagnóstico/);
});
test('empates apresentam todas as áreas em destaque', () => {
    assert.equal(calcularResultado(teste, [0, 1]).titulo, 'A / B');
});
