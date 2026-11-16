'use strict';
const byId = id => document.getElementById(id);
let meetings = [];
let busy = false;
let selection = 0;
async function api(path, method = 'GET', body) {
    const response = await fetch('/api/integracao' + path, {
        method, headers: { 'Content-Type': 'application/json', 'X-Panel-Request': '1' },
        body: body === undefined ? undefined : JSON.stringify(body)
    });
    const data = await response.json();
    if (!response.ok) throw Error(response.status === 401 ? 'Entre no painel para continuar.' : data.error || data.erro || 'Não foi possível concluir. Atualize antes de tentar novamente.');
    return data;
}
async function run(action) {
    if (busy) return;
    busy = true;
    document.querySelectorAll('button,select').forEach(el => el.disabled = true);
    byId('feedback').textContent = 'Aguarde…';
    try { await action(); byId('feedback').textContent = 'Dados atualizados.'; }
    catch (error) { byId('feedback').textContent = error.message; }
    finally { busy = false; document.querySelectorAll('button,select').forEach(el => el.disabled = false); }
}
function fill(select, rows, label) {
    const old = select.value;
    select.replaceChildren(...rows.map(row => new Option(label(row), row.id)));
    if (rows.some(row => row.id === old)) select.value = old;
}
async function showMeeting() {
    const version = ++selection;
    const item = meetings.find(row => row.id === byId('meeting').value);
    byId('students').replaceChildren();
    byId('qr').hidden = true;
    byId('detail').textContent = item ? `${item.curso} · ${new Date(item.data).toLocaleString()} · ${item.status}` : 'Nenhum encontro cadastrado.';
    if (!item) return;
    const students = await api(`/encontros/${item.id}/alunos`);
    if (version !== selection) return;
    byId('qr').src = `/api/integracao/qr?id=${encodeURIComponent(item.id)}`;
    byId('qr').hidden = item.status !== 'aberto';
    for (const student of students) {
        const li = document.createElement('li');
        li.textContent = `${student.name} — ${student.data_hora ? 'Presença confirmada em ' + new Date(student.data_hora).toLocaleString() : 'Sem presença'}`;
        if (!student.data_hora && item.status === 'aberto') {
            const button = document.createElement('button');
            button.type = 'button'; button.textContent = 'Registrar presença';
            button.addEventListener('click', () => run(async () => {
                await api(`/encontros/${item.id}/presencas`, 'POST', { aluno_id: student.id });
                await showMeeting();
            }));
            li.append(button);
        }
        byId('students').append(li);
    }
}
async function refresh() {
    const [courses, rows] = await Promise.all([api('/courses'), api('/encontros')]);
    meetings = rows;
    fill(byId('course'), courses, row => row.title);
    fill(byId('meeting'), meetings, row => row.titulo);
    await showMeeting();
}
byId('refresh').addEventListener('click', () => run(refresh));
byId('meeting').addEventListener('change', () => run(showMeeting));
byId('create').addEventListener('submit', event => {
    event.preventDefault();
    run(async () => {
        await api('/encontros', 'POST', { course_id: byId('course').value, titulo: byId('title').value, data: new Date(byId('date').value).toISOString() });
        byId('title').value = ''; await refresh();
    });
});
for (const [id, status] of [['open','aberto'], ['close','encerrado']]) {
    byId(id).addEventListener('click', () => run(async () => {
        const meetingId = byId('meeting').value;
        if (!meetingId) throw Error('Escolha um encontro.');
        await api(`/encontros/${meetingId}`, 'PATCH', { status });
        await refresh();
    }));
}
run(refresh);
