function normalizarCpf(valor) {
    return typeof valor === 'string' ? valor.replace(/[.\s-]/g, '') : '';
}

function cpfValido(valor) {
    const cpf = normalizarCpf(valor);
    if (!/^\d{11}$/.test(cpf) || /^(\d)\1{10}$/.test(cpf)) return false;
    for (let tamanho = 9; tamanho <= 10; tamanho++) {
        let soma = 0;
        for (let indice = 0; indice < tamanho; indice++) {
            soma += Number(cpf[indice]) * (tamanho + 1 - indice);
        }
        const resto = soma % 11;
        const digito = resto < 2 ? 0 : 11 - resto;
        if (Number(cpf[tamanho]) !== digito) return false;
    }
    return true;
}

function contaBloqueada(usuario) {
    return usuario?.bloqueado === true || usuario?.bloqueado === 'true';
}

module.exports = { normalizarCpf, cpfValido, contaBloqueada };
