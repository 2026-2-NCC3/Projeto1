import {runCourseImport} from './import-courses.js';
import {api} from './api.js';
import {esc,dateBR,dateInput,short,initials,icon,badge} from './utils.js';

const tabs=[
  {id:'overview',title:'Visão geral',icon:'grid'},
  {id:'courses',title:'Cursos',icon:'book'},
  {id:'meetings',title:'Encontros e presença',icon:'calendar'},
  {id:'students',title:'Alunos',icon:'users'},
  {id:'announcements',title:'Comunicados',icon:'megaphone'},
  {id:'messages',title:'Dúvidas e mensagens',icon:'message'}
];
const state={tab:'overview',courses:[],meetings:[],students:[],announcements:[],messages:[],attendances:[],selectedStudent:null,lastImport:null};
const $=id=>document.getElementById(id);
const text=v=>esc(v);
const course=id=>state.courses.find(c=>c.id===id);
const student=id=>state.students.find(s=>s.id===id);
const meeting=id=>state.meetings.find(m=>m.id===id);
const countAttendance=id=>state.attendances.filter(a=>a.meetingId===id).length;
const demoHint = '';
const empty=(title,subtitle,ico='book')=>`<div class="empty"><div class="empty-icon">${icon(ico)}</div><strong>${text(title)}</strong><p>${text(subtitle)}</p></div>`;
const action=(label,name,extra='',cls='btn btn-primary')=>`<button type="button" class="${cls}" data-action="${name}" ${extra}>${label}</button>`;
const pageIntro=(eyebrow,title,desc,buttons='')=>`<header class="page-intro"><div><span class="eyebrow">${eyebrow}</span><h1>${title}</h1><p>${desc}</p></div><div class="page-intro-actions">${buttons}</div></header>`;
const commonTable=(head,body)=>`<div class="panel"><div class="table-wrap"><table class="data-table"><thead><tr>${head.map(h=>`<th>${h}</th>`).join('')}</tr></thead><tbody>${body}</tbody></table></div></div>`;
const qrcodeImg=(type,id)=>`/api/qr?type=${encodeURIComponent(type)}&id=${encodeURIComponent(id)}`;
let videoStream=null,scanAnimation=null,scanLocked=false;

function toast(message,isError=false){const d=document.createElement('div');d.className='toast'+(isError?' error':'');d.textContent=message;$('toastRoot').append(d);setTimeout(()=>d.remove(),4400)}
function showLogin(){ $('loginScreen').classList.remove('is-hidden');$('appScreen').classList.add('is-hidden');closeModal(); }
function showApp(){ $('loginScreen').classList.add('is-hidden');$('appScreen').classList.remove('is-hidden');}
async function loadAll(){
  const sources=['courses','meetings','students','announcements','messages','attendances'];
  const data=await Promise.all(sources.map(x=>api('/'+x)));
  sources.forEach((key,i)=>state[key]=data[i].items||[]);
  state.lastImport=data[0].lastImport||null;
  if(!state.selectedStudent && state.students.length) state.selectedStudent=state.students[0].id;
  render();
}
function sidebar(){
  $('mainNav').innerHTML=tabs.map(t=>`<button type="button" class="nav-link ${state.tab===t.id?'active':''}" data-action="nav" data-target="${t.id}">${icon(t.icon)}<span>${t.title}</span></button>`).join('');
  $('breadcrumbCurrent').textContent=tabs.find(t=>t.id===state.tab)?.title||'Visão geral';
}
function navigate(tab){state.tab=tab;$('sidebar').classList.remove('open');window.location.hash=tab;render();window.scrollTo({top:0,behavior:'instant'});}
function render(){sidebar();const sections={overview:renderOverview,courses:renderCourses,meetings:renderMeetings,students:renderStudents,announcements:renderAnnouncements,messages:renderMessages};$('mainContent').innerHTML=(sections[state.tab]||renderOverview)();}

function renderOverview(){
  const stats=[
    {label:'Cursos disponíveis',value:state.courses.filter(c=>c.is_active).length,icon:'book',color:''},
    {label:'Encontros criados',value:state.meetings.length,icon:'calendar',color:'blue'},
    {label:'Alunos cadastrados',value:state.students.length,icon:'users',color:'amber'},
    {label:'Presenças registradas',value:state.attendances.length,icon:'qr',color:'purple'}
  ];
  const recent=[...state.meetings].sort((a,b)=>new Date(a.date)-new Date(b.date)).slice(0,5);
  const activity=[...state.announcements.map(a=>({id:a.id,title:'Comunicado: '+a.title,date:a.createdAt,ico:'megaphone'})),...state.messages.map(m=>({id:m.id,title:(m.author==='aluno'?'Dúvida de ':'Resposta para ')+(student(m.studentId)?.name||'aluno'),date:m.createdAt,ico:'message'}))].sort((a,b)=>new Date(b.date)-new Date(a.date)).slice(0,5);
  return `${pageIntro('BEM-VINDO AO PAINEL','Olá, equipe Próxima Etapa! ✦','Aqui está um resumo do que acontece no seu espaço de trabalho.',action(icon('plus')+' Novo encontro','meeting-new'))}
    <div class="top-banner"><div><span class="eyebrow light">JUNTOS, VAMOS MAIS LONGE</span><h2>Cada oportunidade faz a diferença.</h2><p>Uma visão centralizada para organizar experiências, acompanhar jornadas e apoiar o crescimento dos alunos.</p></div><div class="banner-emblem">✦</div></div>
    ${demoHint}
    <div class="stat-grid">${stats.map(s=>`<div class="stat-card"><div class="stat-top"><span class="stat-icon ${s.color}">${icon(s.icon)}</span>${badge('Dados do painel','sage')}</div><div class="stat-value">${s.value}</div><div class="stat-label">${s.label}</div><div class="stat-footer">↗ Dados deste ambiente</div></div>`).join('')}</div>
    <div class="section-grid"><section class="panel"><div class="panel-head"><div><h2>Próximos encontros</h2><p>Aulas e atividades cadastradas para demonstração</p></div>${action('Ver todos '+icon('arrow'),'nav',`data-target="meetings"`,'btn btn-soft btn-sm')}</div><div class="panel-body flat-list">${recent.length?recent.map(m=>`<div class="flat-row"><div class="row-icon">${icon('calendar')}</div><div class="row-main"><strong>${text(m.title)}</strong><small>${text(course(m.courseId)?.title||'Curso não encontrado')} · ${text(m.location||'Sem local')}</small></div><div class="row-trailing">${dateBR(m.date,true)}</div></div>`).join(''):empty('Nenhum encontro','Crie o primeiro encontro para começar.','calendar')}</div></section>
    <section class="panel"><div class="panel-head"><div><h2>Atividade recente</h2><p>Movimentações deste painel</p></div></div><div class="panel-body flat-list">${activity.length?activity.map(a=>`<div class="flat-row"><div class="row-icon">${icon(a.ico)}</div><div class="row-main"><strong>${text(short(a.title,45))}</strong><small>${dateBR(a.date,true)}</small></div></div>`).join(''):empty('Sem atividade','As novidades aparecem aqui.','message')}</div></section></div>`;
}
function renderCourses(){
  const imported=state.courses.filter(c=>c.origin==='api-readonly').length;
  const rows=[...state.courses].sort((a,b)=>String(a.title).localeCompare(b.title)).map(c=>`<tr data-filter-text="${text([c.title,c.location,c.category].join(' ').toLowerCase())}"><td><div class="table-title">${text(c.title)}</div><div class="table-desc">${text(short(c.description||'Sem descrição',60))}</div></td><td>${text(c.category||'Geral')}</td><td>${dateBR(c.course_date)}</td><td>${text(c.location||'Não informado')}</td><td>${badge(c.origin==='api-readonly'?'API Render':c.origin==='seed-do-app'?'Seed do app':'Criado aqui',c.origin==='api-readonly'?'blue':'sage')}</td><td>${badge(c.is_active?'Ativo':'Inativo',c.is_active?'green':'neutral')}</td><td><div class="table-actions">${action(icon('edit'),'course-edit',`data-id="${c.id}" aria-label="Editar curso"`,'icon-button')}${action(icon('trash'),'course-delete',`data-id="${c.id}" aria-label="Excluir curso"`,'icon-button')}</div></td></tr>`).join('');
  return `${pageIntro('APRENDIZAGEM E DESCOBERTAS','Gerenciamento de cursos','Cadastre e organize os cursos que ficam disponíveis neste painel de testes.',`${action(icon('sync')+' Buscar cursos do app','courses-import','', 'btn btn-outline')}${action(icon('plus')+' Novo curso','course-new')}`)}
    <div class="info-banner">${icon('shield')} <span><strong>Integração segura:</strong> “Buscar cursos do app” faz apenas um GET na API existente e copia os dados para o armazenamento independente deste painel no Supabase. Importados: <strong>${imported}</strong>. ${state.lastImport?'Última consulta: '+dateBR(state.lastImport,true):'Nenhuma sincronização realizada.'}</span></div>
    <div class="toolbar"><div class="searchbar">${icon('search')}<input id="tableSearch" type="search" placeholder="Buscar cursos por nome, local ou categoria" aria-label="Buscar cursos"></div><div class="toolbar-right">${badge(state.courses.length+' cursos','sage')}</div></div>
    ${state.courses.length?commonTable(['CURSO','CATEGORIA','DATA','LOCAL','ORIGEM','SITUAÇÃO','AÇÕES'],rows):`<div class="panel">${empty('Nenhum curso cadastrado','Cadastre um curso ou importe da API do app.','book')}</div>`}`;
}
function renderMeetings(){
  const cards=[...state.meetings].sort((a,b)=>new Date(a.date)-new Date(b.date)).map(m=>`<article class="meeting-card"><div class="meeting-band"></div><div class="meeting-inner"><div style="display:flex;justify-content:space-between;gap:8px;align-items:center">${badge(m.status==='aberto'?'Em andamento':m.status==='encerrado'?'Encerrado':'Agendado',m.status==='aberto'?'green':m.status==='encerrado'?'neutral':'blue')}${m.demo?badge('Exemplo','amber'):''}</div><h3>${text(m.title)}</h3><div class="meeting-course">${text(course(m.courseId)?.title||'Curso não encontrado')}</div><div class="meeting-meta">${icon('calendar')}${dateBR(m.date,true)}</div><div class="meeting-meta">${icon('map')}${text(m.location||'Local não informado')}</div></div><div class="meeting-footer"><span class="meeting-count">${countAttendance(m.id)} presenças</span>${action('Gerenciar '+icon('arrow'),'meeting-open',`data-id="${m.id}"`,'btn btn-soft btn-sm')}</div></article>`).join('');
  return `${pageIntro('PRESENÇA E PARTICIPAÇÃO','Encontros e check-in','Crie encontros, gere QR Codes e controle a presença de cada aluno.',action(icon('plus')+' Novo encontro','meeting-new'))}
    ${demoHint}
    <div class="info-banner warn">${icon('info')} <span>Os dois tipos de QR Code são gerados aqui. O QR de aluno segue o formato <code>ALUNO-UUID</code>. O QR de encontro é uma proposta de protótipo que ainda exigirá implementação no aplicativo Android para funcionar entre dispositivos.</span></div>
    <div class="cards-grid">${cards||`<div class="panel" style="grid-column:1/-1">${empty('Nenhum encontro cadastrado','Use “Novo encontro” para adicionar o primeiro.','calendar')}</div>`}</div>`;
}
function renderStudents(){
  const rows=[...state.students].sort((a,b)=>a.name.localeCompare(b.name)).map(s=>`<tr data-filter-text="${text([s.name,s.email,s.phone].join(' ').toLowerCase())}"><td><div style="display:flex;align-items:center;gap:11px"><div class="student-avatar">${text(initials(s.name))}</div><div><div class="table-title">${text(s.name)}</div><div class="table-desc">${text(s.email)}</div></div></div></td><td>${text(s.phone||'—')}</td><td>${(s.enrollments||[]).length} curso(s)</td><td>${badge(s.status==='ativo'?'Ativo':'Inativo',s.status==='ativo'?'green':'neutral')}</td><td><div class="table-actions">${action(icon('qr'),'student-qr',`data-id="${s.id}" aria-label="QR do aluno"`,'icon-button')}${action(icon('edit'),'student-edit',`data-id="${s.id}" aria-label="Editar aluno"`,'icon-button')}${action(icon('trash'),'student-delete',`data-id="${s.id}" aria-label="Excluir aluno"`,'icon-button')}</div></td></tr>`).join('');
  return `${pageIntro('JORNADA DOS ESTUDANTES','Alunos','Gerencie estudantes fictícios, inscrições e seus cartões QR.',action(icon('plus')+' Novo aluno','student-new'))}
    ${demoHint}<div class="toolbar"><div class="searchbar">${icon('search')}<input id="tableSearch" type="search" placeholder="Buscar aluno por nome ou e-mail"></div>${badge(state.students.length+' alunos','sage')}</div>
    ${state.students.length?commonTable(['ALUNO','CONTATO','INSCRIÇÕES','SITUAÇÃO','AÇÕES'],rows):`<div class="panel">${empty('Nenhum aluno','Cadastre o primeiro aluno de teste.','users')}</div>`}`;
}
function renderAnnouncements(){
  const rows=[...state.announcements].sort((a,b)=>new Date(b.createdAt)-new Date(a.createdAt)).map(a=>`<article class="announce-card"><div class="announce-meta"><span>${badge('Comunicado do painel','sage')}</span><span>${dateBR(a.createdAt,true)}</span></div><h3>${text(a.title)}</h3><p>${text(a.message)}</p><div class="announce-foot"><span class="muted small">Público: todos os estudantes de teste</span>${action(icon('trash')+' Excluir','announcement-delete',`data-id="${a.id}"`,'btn btn-danger btn-sm')}</div></article>`).join('');
  return `${pageIntro('CONEXÃO E INFORMAÇÃO','Comunicados','Prepare comunicados e teste como serão apresentados na área administrativa.',action(icon('plus')+' Novo comunicado','announcement-new'))}
    ${demoHint}<div class="announcement-list">${rows||`<div class="panel">${empty('Nenhum comunicado','Crie um comunicado de demonstração.','megaphone')}</div>`}</div>`;
}
function renderMessages(){
  if(!state.selectedStudent && state.students.length) state.selectedStudent=state.students[0].id;
  const current=student(state.selectedStudent);
  const people=state.students.filter(s=>state.messages.some(m=>m.studentId===s.id));
  const messages=state.messages.filter(m=>m.studentId===state.selectedStudent).sort((a,b)=>new Date(a.createdAt)-new Date(b.createdAt));
  const names=people.map(s=>`<button class="conversation-tab ${s.id===state.selectedStudent?'active':''}" type="button" data-action="conversation" data-id="${s.id}"><div class="student-avatar">${text(initials(s.name))}</div><div><b>${text(s.name)}</b><small>${state.messages.filter(m=>m.studentId===s.id).length} mensagens</small></div></button>`).join('');
  return `${pageIntro('ESCUTA E ORIENTAÇÃO','Dúvidas e mensagens','Acompanhe as conversas de demonstração e responda aos estudantes.')}${demoHint}
    <div class="panel split-panel"><aside class="conversation-list"><div class="small muted" style="padding:8px 10px 12px;font-weight:750;letter-spacing:.7px">CONVERSAS</div>${names||empty('Nenhuma conversa','As mensagens aparecerão aqui.','message')}</aside>
      <section class="chat-column">${current?`<div class="chat-header"><div class="student-avatar">${text(initials(current.name))}</div><div><strong>${text(current.name)}</strong><div class="small muted">${text(current.email)}</div></div></div><div class="chat-window" id="chatWindow">${messages.length?messages.map(m=>`<div class="bubble ${m.author==='equipe'?'staff':''}">${text(m.text)}<time>${dateBR(m.createdAt,true)}</time></div>`).join(''):empty('Conversa vazia','Nenhuma mensagem desta pessoa.','message')}</div><form id="replyForm" class="chat-form"><input name="message" type="text" placeholder="Digite sua resposta de teste..." maxlength="2000" aria-label="Responder mensagem" required><button class="btn btn-primary" type="submit">${icon('send')} Enviar</button></form>`:empty('Vamos conversar?','Cadastre um aluno e uma conversa para começar.','message')}</section></div>`;
}

function modal(title,body,footer='',large=false){
  closeModal(); $('modalRoot').innerHTML=`<div class="modal-backdrop" id="backdrop"><div class="modal ${large?'large':''}" role="dialog" aria-modal="true" aria-label="${text(title)}"><div class="modal-head"><h2>${text(title)}</h2><button class="icon-button" type="button" data-action="modal-close" aria-label="Fechar janela">${icon('close')}</button></div><div class="modal-body">${body}</div>${footer?`<div class="modal-foot">${footer}</div>`:''}</div></div>`;
  $('modalRoot').querySelector('input:not([type=checkbox]),select,textarea,button')?.focus();
}
function stopCamera(){scanLocked=true; if(scanAnimation) cancelAnimationFrame(scanAnimation);scanAnimation=null;if(videoStream){videoStream.getTracks().forEach(t=>t.stop());videoStream=null}}
function closeModal(){stopCamera();$('modalRoot').innerHTML=''}
const saveFooter=id=>`${action('Cancelar','modal-close','','btn btn-outline')}<button class="btn btn-primary" type="submit" form="${id}">${icon('check')} Salvar no painel</button>`;
const opt=(value,label,selected)=>`<option value="${text(value)}" ${String(value)===String(selected)?'selected':''}>${text(label)}</option>`;
function courseForm(id){
  const c=course(id)||{};
  const fields=`<form id="courseForm" class="form-grid"><input type="hidden" name="id" value="${text(c.id||'')}"><label class="full">Nome do curso<input name="title" required maxlength="140" value="${text(c.title||'')}" placeholder="Ex.: Introdução à Programação"></label><label>Data e horário<input type="datetime-local" name="date" required value="${text(dateInput(c.course_date)||'')}"></label><label>Categoria<input name="category" maxlength="60" value="${text(c.category||'tecnologia')}"></label><label class="full">Local<input name="location" maxlength="160" value="${text(c.location||'')}" placeholder="Ex.: São Paulo - SP / Online"></label><label class="full">Descrição<textarea name="description" maxlength="2000" rows="3">${text(c.description||'')}</textarea></label><label>Total de vagas<input type="number" name="total" min="1" max="100000" required value="${c.total_spots??30}"></label><label>Vagas disponíveis<input type="number" name="available" min="0" max="100000" required value="${c.available_spots??30}"></label><label>Pontos concedidos<input type="number" name="points" min="0" value="${c.points_awarded??100}"></label><label>Situação<select name="status">${opt('true','Ativo',String(c.is_active??true))}${opt('false','Inativo',String(c.is_active??true))}</select></label><label class="full checkline"><input type="checkbox" name="certificate" ${c.has_certificate?'checked':''}><span>Emite certificado</span></label></form><p class="help">Ao salvar, os dados são modificados no Supabase deste painel, sem alterar os dados do aplicativo.</p>`;
  modal(c.id?'Editar curso':'Novo curso',fields,saveFooter('courseForm'),true);
}
function meetingForm(id){
  const m=meeting(id)||{};
  if(!state.courses.length){toast('Cadastre um curso antes de criar encontros.',true);return}
  modal(m.id?'Editar encontro':'Novo encontro',`<form class="form-grid" id="meetingForm"><input type="hidden" name="id" value="${text(m.id||'')}"><label class="full">Nome do encontro<input name="title" required maxlength="150" value="${text(m.title||'')}" placeholder="Ex.: Aula 1: Introdução"></label><label>Curso<select name="courseId" required>${state.courses.map(c=>opt(c.id,c.title,m.courseId||state.courses[0].id)).join('')}</select></label><label>Data e horário<input type="datetime-local" name="date" required value="${text(dateInput(m.date)||'')}"></label><label>Local<input name="location" maxlength="120" value="${text(m.location||'')}" placeholder="Ex.: Sala 03 ou Online"></label><label>Status<select name="status">${['agendado','aberto','encerrado'].map(s=>opt(s,s.charAt(0).toUpperCase()+s.slice(1),m.status||'agendado')).join('')}</select></label></form>`,saveFooter('meetingForm'),true);
}
function studentForm(id){
  const s=student(id)||{};
  const checks=state.courses.map(c=>`<label class="checkline"><input type="checkbox" name="enrollments" value="${c.id}" ${(s.enrollments||[]).includes(c.id)?'checked':''}><span>${text(c.title)}</span></label>`).join('');
  modal(s.id?'Editar aluno':'Novo aluno',`<form class="form-grid" id="studentForm"><input type="hidden" name="id" value="${text(s.id||'')}"><label class="full">Nome completo<input name="name" maxlength="140" required value="${text(s.name||'')}" placeholder="Estudante de demonstração"></label><label>E-mail<input name="email" type="email" maxlength="180" required value="${text(s.email||'')}" placeholder="aluno@example.test"></label><label>Telefone<input name="phone" maxlength="40" value="${text(s.phone||'')}"></label><label>Status<select name="status">${opt('ativo','Ativo',s.status||'ativo')}${opt('inativo','Inativo',s.status||'ativo')}</select></label><div class="full"><strong class="small">Inscrições nos cursos</strong><div class="student-mini-list" style="padding-top:10px">${checks||'<p class="help">Cadastre cursos para permitir inscrições.</p>'}</div></div></form>`,saveFooter('studentForm'),true);
}
function announcementForm(){modal('Novo comunicado',`<form id="announcementForm" class="form-grid"><label class="full">Título<input name="title" required maxlength="150" placeholder="Ex.: Nova turma disponível"></label><label class="full">Mensagem<textarea name="message" rows="6" maxlength="4000" required placeholder="Escreva um comunicado para testar a visualização..."></textarea></label></form><div class="help">O comunicado será salvo apenas neste painel e não aparecerá no app Android real.</div>`,saveFooter('announcementForm'));}
function openQr(type,id){
  const item=type==='student'?student(id):meeting(id);if(!item)return;
  const payload=type==='student'?`ALUNO-${id}`:`ENCONTRO-${id}`;
  const intro=type==='student'?'Identificação individual compatível com o formato de QR usado pelo Android.':'QR Code proposto para este encontro; a leitura pelo aplicativo Android ainda precisa de uma rota própria.';
  const body=`<div class="qrcode-preview"><div style="text-align:center"><strong>${text(item.name||item.title)}</strong><p class="help">${intro}</p></div><img src="${qrcodeImg(type,id)}" alt="QR Code ${text(item.name||item.title)}"><div class="qr-data">${text(payload)}</div>${badge('Demonstração do painel','amber')}</div>`;
  modal(type==='student'?'QR Code do aluno':'QR Code do encontro',body,`${action('Fechar','modal-close','','btn btn-outline')}<a href="${qrcodeImg(type,id)}" download="qr-${type}-${id}.svg" class="btn btn-primary">${icon('download')} Baixar SVG</a>`);
}
function openMeeting(id){
  const m=meeting(id);if(!m)return;
  const attendances=state.attendances.filter(a=>a.meetingId===m.id);
  const rows=attendances.map(a=>`<div class="student-mini"><span><strong>${text(student(a.studentId)?.name||'Aluno')}</strong><br><small class="muted">${dateBR(a.createdAt,true)} · ${text(a.method)}</small></span>${badge('Presente','green')}</div>`).join('');
  modal(m.title,`<div class="two-col"><div><div class="meeting-course">${text(course(m.courseId)?.title||'Curso')}</div><div class="detail-meta"><div>${icon('calendar')} ${dateBR(m.date,true)}</div><div>${icon('map')} ${text(m.location)}</div><div>${icon('users')} ${attendances.length} presença(s) registrada(s)</div></div><div style="display:flex;flex-wrap:wrap;gap:8px;margin:14px 0">${action(icon('qr')+' Ver QR do encontro','meeting-qr',`data-id="${id}"`,'btn btn-soft btn-sm')}${action(icon('edit')+' Editar encontro','meeting-edit',`data-id="${id}"`,'btn btn-outline btn-sm')}</div></div><div><strong>Participações registradas</strong><div class="student-mini-list" style="margin-top:12px">${rows||'<div class="line-note">Ainda não há presenças para este encontro.</div>'}</div></div></div><hr class="modal-divider"><div class="line-note">O QR do aluno e o QR do encontro podem ser usados no teste de check-in. As presenças são registradas no Supabase deste painel, com bloqueio de duplicidade por aluno e encontro.</div>`,`${action('Excluir encontro','meeting-delete',`data-id="${id}"`,'btn btn-danger')}${action(icon('check')+' Registrar presença','checkin',`data-id="${id}"`)}`,true);
}
function checkinForm(id){
  if(meeting(id)?.status==='encerrado'){toast('Este encontro está encerrado. Reabra-o antes de registrar presença.',true);return;}
  const m=meeting(id);if(!m)return;
  const enrolled=state.students.filter(s=>(s.enrollments||[]).includes(m.courseId));
  modal('Registrar presença',`<div class="info-banner">${icon('shield')} Check-in de teste para: <strong>${text(m.title)}</strong></div><form id="checkinForm" class="form-grid"><input type="hidden" name="meetingId" value="${id}"><label class="full">Método de registro<select name="method" id="methodField"><option value="manual">Seleção manual</option><option value="aluno">Leitura do QR do aluno</option><option value="encontro">Leitura do QR do encontro</option></select></label><label class="full">Aluno inscrito<select name="studentId">${enrolled.map(s=>opt(s.id,s.name,'')).join('')}</select></label><label class="full">Código lido (necessário para métodos de QR)<input id="scanInput" name="code" placeholder="ALUNO-... ou ENCONTRO-..." autocomplete="off"><small class="help">Cole o conteúdo do QR ou use um leitor que digita o código neste campo.</small></label></form><div style="display:flex;gap:9px;flex-wrap:wrap;margin-top:12px">${action(icon('qr')+' Abrir câmera','camera-start','','btn btn-outline btn-sm')}${action(icon('qr')+' QR do encontro','meeting-qr',`data-id="${id}"`,'btn btn-soft btn-sm')}</div><div id="cameraArea" class="is-hidden" style="margin-top:15px"><video id="scanVideo" autoplay playsinline muted style="max-width:100%;border-radius:12px;width:320px;background:#061d19"></video><p class="help">Aponte a câmera para o QR. Para funcionar, o navegador precisa autorizar a câmera e oferecer detecção de QR.</p></div>${enrolled.length?'':`<p class="error-inline">Não há alunos inscritos neste curso. Edite um aluno e selecione o curso para testar a presença.</p>`}`,`${action('Cancelar','modal-close','','btn btn-outline')}<button type="submit" class="btn btn-primary" form="checkinForm" ${enrolled.length?'':'disabled'}>${icon('check')} Confirmar presença</button>`,true);
}
async function startCamera(){
  if(!('BarcodeDetector' in window) || !navigator.mediaDevices?.getUserMedia){toast('Leitura por câmera não disponível. Você pode colar o texto do QR.',true);return}
  const area=$('cameraArea'),video=$('scanVideo');if(!area||!video)return;
  try{
    stopCamera();scanLocked=false;videoStream=await navigator.mediaDevices.getUserMedia({video:{facingMode:'environment'},audio:false});video.srcObject=videoStream;area.classList.remove('is-hidden');await video.play();
    const detector=new BarcodeDetector({formats:['qr_code']});
    const scan=async()=>{
      if(scanLocked || !videoStream)return;
      try {const codes=await detector.detect(video);if(codes?.[0]?.rawValue){$('scanInput').value=codes[0].rawValue;const code=codes[0].rawValue;if(code.startsWith('ALUNO-'))$('methodField').value='aluno';else if(code.startsWith('ENCONTRO-'))$('methodField').value='encontro';stopCamera();area.classList.add('is-hidden');toast('QR Code lido! Confira e confirme a presença.');return}}catch{}
      scanAnimation=requestAnimationFrame(scan);
    };scan();
  }catch{stopCamera();toast('Não foi possível iniciar a câmera. Cole o conteúdo do QR no campo.',true)}
}
async function saveFromForm(form){
  const f=new FormData(form),id=f.get('id'),mode=form.id;
  if(mode==='courseForm'){
    const item={title:f.get('title'),course_date:new Date(f.get('date')).toISOString(),category:f.get('category'),location:f.get('location'),description:f.get('description'),total_spots:Number(f.get('total')),available_spots:Number(f.get('available')),points_awarded:Number(f.get('points')),is_active:f.get('status')==='true',has_certificate:f.has('certificate')};
    await api('/courses'+(id?'/'+id:''),id?'PUT':'POST',item);toast(id?'Curso atualizado no painel.':'Curso cadastrado no painel.');
  }else if(mode==='meetingForm'){
    const item={title:f.get('title'),date:new Date(f.get('date')).toISOString(),location:f.get('location'),courseId:f.get('courseId'),status:f.get('status')};
    await api('/meetings'+(id?'/'+id:''),id?'PUT':'POST',item);toast(id?'Encontro atualizado.':'Encontro criado.');
  }else if(mode==='studentForm'){
    const item={name:f.get('name'),email:f.get('email'),phone:f.get('phone'),status:f.get('status'),enrollments:f.getAll('enrollments')};
    await api('/students'+(id?'/'+id:''),id?'PUT':'POST',item);toast(id?'Aluno atualizado.':'Aluno cadastrado.');
  }else if(mode==='announcementForm'){
    await api('/announcements','POST',{title:f.get('title'),message:f.get('message')});toast('Comunicado salvo somente neste painel.');
  }else if(mode==='checkinForm'){
    let method=f.get('method'),studentId=f.get('studentId'),code=String(f.get('code')||'').trim(),meetingId=f.get('meetingId');
    if(method==='aluno'){
      if(!/^ALUNO-[0-9a-f-]{36}$/i.test(code))throw Error('Leia ou cole um código ALUNO-UUID válido.');
      studentId=code.slice(6).toLowerCase();
    }
    if(method==='encontro' && code!==`ENCONTRO-${meetingId}`)throw Error('Este QR Code não corresponde ao encontro selecionado.');
    await api('/attendances','POST',{studentId,meetingId,method});toast('Presença registrada no ambiente do painel.');
  }
  closeModal();await loadAll();
}
async function removeEntity(type,id,description){
  if(!window.confirm(`Excluir ${description}? Essa ação afeta os dados deste painel no Supabase; não altera o aplicativo.`))return;
  await api('/'+type+'/'+id,'DELETE');closeModal();toast('Registro excluído do painel.');await loadAll();
}
async function processAction(element){
  const {action:name,id,target}=element.dataset;
  try{
    if(name==='nav')navigate(target);
    else if(name==='modal-close')closeModal();
    else if(name==='course-new')courseForm();
    else if(name==='course-edit')courseForm(id);
    else if(name==='course-delete')await removeEntity('courses',id,'este curso');
    else if(name==='courses-import'){
      await runCourseImport(element,()=>api('/courses/import','POST'),loadAll,toast);
    }
    else if(name==='meeting-new')meetingForm();
    else if(name==='meeting-open')openMeeting(id);
    else if(name==='meeting-edit')meetingForm(id);
    else if(name==='meeting-qr')openQr('meeting',id);
    else if(name==='meeting-delete')await removeEntity('meetings',id,'este encontro');
    else if(name==='checkin')checkinForm(id);
    else if(name==='student-new')studentForm();
    else if(name==='student-edit')studentForm(id);
    else if(name==='student-qr')openQr('student',id);
    else if(name==='student-delete')await removeEntity('students',id,'este aluno');
    else if(name==='announcement-new')announcementForm();
    else if(name==='announcement-delete')await removeEntity('announcements',id,'este comunicado');
    else if(name==='conversation'){state.selectedStudent=id;render();}
    else if(name==='camera-start')await startCamera();
  } catch(e){toast(e.message,true)}
}

document.addEventListener('click',e=>{const element=e.target.closest('[data-action]');if(element){e.preventDefault();processAction(element)}else if(e.target.id==='backdrop')closeModal()});
document.addEventListener('keydown',e=>{if(e.key==='Escape')closeModal()});
document.addEventListener('input',e=>{if(e.target.id==='tableSearch'){const term=e.target.value.toLowerCase().trim();document.querySelectorAll('[data-filter-text]').forEach(row=>row.classList.toggle('is-hidden',!row.dataset.filterText.includes(term)))}});
document.addEventListener('submit',async e=>{
  if(e.target.id==='loginForm'){
    e.preventDefault();const f=new FormData(e.target),error=$('loginError');error.classList.add('is-hidden');
    try{await api('/login','POST',{email:f.get('email'),password:f.get('password')});e.target.reset();showApp();await loadAll()}catch(ex){error.textContent=ex.message;error.classList.remove('is-hidden')}return;
  }
  if(e.target.id==='replyForm'){
    e.preventDefault();const input=e.target.elements.message;
    try{await api('/messages','POST',{studentId:state.selectedStudent,text:input.value});input.value='';await loadAll();requestAnimationFrame(()=>{$('chatWindow')?.scrollTo(0,100000)})}catch(ex){toast(ex.message,true)}return;
  }
  if(['courseForm','meetingForm','studentForm','announcementForm','checkinForm'].includes(e.target.id)){
    e.preventDefault();const form=e.target;
    try {await saveFromForm(form)} catch(ex) {toast(ex.message,true)}
  }
});
$('logoutButton').addEventListener('click',async()=>{try{await api('/logout','POST');showLogin()}catch(e){toast(e.message,true)}});
$('mobileMenu').addEventListener('click',()=>$('sidebar').classList.toggle('open'));
async function init(){try{const session=await api('/session');if(session.authenticated){showApp();state.tab=tabs.some(t=>t.id===location.hash.slice(1))?location.hash.slice(1):'overview';await loadAll()}else showLogin()}catch(e){showLogin();toast(e.message,true)}}
init();
