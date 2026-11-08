const { test } = require('node:test');
const assert = require('node:assert/strict');
const { normalizarCpf, cpfValido, contaBloqueada } = require('../recursos/validacoes');

test('aceita CPF com dígitos corretos, com ou sem pontuação', () => {
    assert.equal(cpfValido('52998224725'), true);
    assert.equal(cpfValido('529.982.247-25'), true);
    assert.equal(normalizarCpf('529.982.247-25'), '52998224725');
});

test('rejeita CPF com dígito incorreto, repetição, letras ou tamanho inválido', () => {
    for (const cpf of ['52998224724', '00000000000', '11111111111',
        '123456789', 'abcdefghijk', '5299822472a', null, 52998224725]) {
        assert.equal(cpfValido(cpf), false);
    }
});

test('reconhece bloqueio recebido do PostgreSQL e preserva conta sem restrição', () => {
    assert.equal(contaBloqueada({ bloqueado: true }), true);
    assert.equal(contaBloqueada({ bloqueado: 'true' }), true);
    for (const usuario of [{ bloqueado: false }, { bloqueado: 'false' }, {}, null]) {
        assert.equal(contaBloqueada(usuario), false);
    }
});
