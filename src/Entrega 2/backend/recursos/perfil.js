function calcularResultado(questionario, respostas) {
    const perguntas = questionario.perguntas;
    const perfis = questionario.perfis;
    if (!Array.isArray(respostas) || respostas.length !== perguntas.length) {
        throw new Error('Responda todas as perguntas.');
    }
    const pontos = Object.fromEntries(Object.keys(perfis).map(chave => [chave, 0]));
    perguntas.forEach((pergunta, indice) => {
        const escolha = respostas[indice];
        if (!Number.isInteger(escolha) || escolha < 0 || escolha >= pergunta.alternativas.length) {
            throw new Error('Há uma resposta inválida.');
        }
        const alternativa = pergunta.alternativas[escolha];
        if (!Object.hasOwn(pontos, alternativa.perfil)) {
            throw new Error('Questionário inválido.');
        }
        pontos[alternativa.perfil]++;
    });
    const maior = Math.max(...Object.values(pontos));
    const destaques = Object.keys(pontos).filter(chave => pontos[chave] === maior);
    return {
        titulo: destaques.map(chave => perfis[chave].titulo).join(' / '),
        descricao: destaques.map(chave => perfis[chave].descricao).join('\n\n'),
        pontos,
        aviso: 'Orientação educacional baseada nas suas respostas. Não é diagnóstico psicológico ou clínico.'
    };
}
module.exports = { calcularResultado };
