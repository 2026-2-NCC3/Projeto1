'use strict';

const uuid = '[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}';
const fail = (status, message) => Object.assign(new Error(message), { status });

async function solicitar(pathname, method, body, transport = fetch) {
    const base = process.env.APP_API_URL;
    const key = process.env.APP_ADMIN_KEY;
    if (!base || !key) throw fail(503, 'Configure a integração com a API do aplicativo no servidor do painel.');
    let url;
    try { url = new URL(base); } catch { throw fail(503, 'Endereço da API inválido.'); }
    if (url.protocol !== 'https:' || url.username || url.password || url.search || url.hash || url.pathname !== '/') throw fail(503, 'Use a origem HTTPS da API, sem caminho ou credenciais.');
    url.pathname = pathname;
    let response;
    try {
        response = await transport(url, {
            method, redirect: 'error', signal: AbortSignal.timeout(60000),
            headers: { 'x-admin-key': key, 'Content-Type': 'application/json', Accept: 'application/json' },
            body: body === undefined ? undefined : JSON.stringify(body)
        });
    } catch { throw fail(503, 'Não foi possível confirmar a operação. Atualize a lista antes de tentar novamente.'); }
    let data;
    try { data = await response.json(); } catch { throw fail(503, 'Resposta inesperada da API.'); }
    if (!response.ok) throw fail([400,403,404,409,422].includes(response.status) ? response.status : 503,
        response.status === 401 ? 'Credencial administrativa da integração inválida.' :
        response.status === 404 ? 'Recurso não encontrado. Confira se a versão integrada da API está publicada.' :
        'A API não confirmou a operação. Confira o aluno, a inscrição e se o encontro está aberto.');
    return { status: response.status, data };
}

function destino(pathname, method) {
    const route = pathname.replace(/^\/api\/integracao/, '');
    if (route === '/courses' && method === 'GET') return '/courses';
    if (route === '/encontros' && ['GET', 'POST'].includes(method)) return '/admin/encontros';
    if (new RegExp(`^/encontros/${uuid}$`).test(route) && ['GET', 'PATCH'].includes(method)) return '/admin' + route;
    if (new RegExp(`^/encontros/${uuid}/alunos$`).test(route) && method === 'GET') return '/admin' + route;
    if (new RegExp(`^/encontros/${uuid}/presencas$`).test(route) && method === 'POST') return '/admin' + route;
    throw fail(404, 'Rota de integração não encontrada.');
}

module.exports = { solicitar, destino };
