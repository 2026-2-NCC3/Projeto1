'use strict';
const test=require('node:test');
const assert=require('node:assert/strict');
const http=require('node:http');
process.env.NODE_ENV='test';
process.env.SUPABASE_URL='https://painel-test.invalid';
process.env.SUPABASE_SECRET_KEY='sb_secret_TEST_ONLY';
process.env.ADMIN_EMAIL='audit@example.test';
process.env.ADMIN_PASSWORD='test-password-only';
process.env.COOKIE_SECURE='true';
const nativeFetch=global.fetch;
let row=null,dbFailure=false,importFailure=false,importTimeout=false,base,remoteUrl;
const network=[];
global.fetch=async(input,options={})=>{
  const url=new URL(String(input));
  if(base && url.origin===base) return nativeFetch(input,options);
  if(remoteUrl && url.href===remoteUrl){
    assert.equal(options.method,'GET');
    network.push('GET courses');
    if(importTimeout) throw new DOMException('Simulated timeout','TimeoutError');
    if(importFailure) return Response.json({error:'unavailable'},{status:503});
    return Response.json([{id:'c8470d6a-5c1b-4969-9aac-e789220c4e09',title:'Importado',course_date:'2026-12-01T12:00:00Z',total_spots:20,available_spots:20}]);
  }
  assert.equal(url.origin,'https://painel-test.invalid','External network forbidden');
  assert.equal(url.pathname,'/rest/v1/painel_web_dados');
  assert.equal(options.headers.apikey,'sb_secret_TEST_ONLY');
  if(dbFailure) return Response.json({error:'test failure'},{status:503});
  const method=options.method; network.push(method+' database');
  let result=[];
  if(method==='GET')result=row?[row]:[];
  else if(method==='POST'){
    if(row)return Response.json({}, {status:409});
    row=JSON.parse(options.body);result=[row];
  }else if(method==='PATCH'){
    if(row && url.searchParams.get('atualizado_em')==='eq.'+row.atualizado_em){
      row={...row,...JSON.parse(options.body)};result=[row];
    }
  }else throw Error('Unexpected database operation');
  return Response.json(result);
};

test('Painel com Supabase simulado e rede externa bloqueada',async t=>{
  remoteUrl='https://courses-test.invalid/courses';process.env.COURSES_API_URL=remoteUrl;
  const mod=require('../server');const server=mod.server;
  await new Promise(r=>server.listen(0,'127.0.0.1',r));base=`http://127.0.0.1:${server.address().port}`;
  t.after(async()=>{await new Promise(r=>server.close(r));global.fetch=nativeFetch});
  let cookie='',c,s,m;
  async function req(p,method='GET',body){
    const response=await fetch(base+p,{method,headers:{Cookie:cookie,'x-panel-request':'1','Content-Type':'application/json'},body:body?JSON.stringify(body):undefined});
    return {status:response.status,data:await response.json(),headers:response.headers};
  }
  await t.test('health, arquivos e proteção de acesso',async()=>{
    assert.equal((await req('/health')).status,200);
    for(const file of ['/','/css/style.css','/js/app.js','/js/import-courses.js','/img/logo-proxima-etapa.png'])assert.equal((await fetch(base+file)).status,200);
    assert.equal((await fetch(base+'/.env')).status,404);
    assert.equal((await req('/api/courses')).status,401);
    assert.equal((await req('/api/login','POST',{email:process.env.ADMIN_EMAIL,password:'wrong'})).status,401);
    const r=await req('/api/login','POST',{email:process.env.ADMIN_EMAIL,password:process.env.ADMIN_PASSWORD});
    assert.equal(r.status,200);assert.match(r.headers.get('set-cookie'),/HttpOnly/);assert.match(r.headers.get('set-cookie'),/Secure/);cookie=r.headers.get('set-cookie').split(';')[0];
  });
  await t.test('banco novo vazio e importação independente',async()=>{
    assert.deepEqual((await req('/api/courses')).data.items,[]);
    const imported=await req('/api/courses/import','POST',{});assert.equal(imported.status,200);assert.equal(imported.data.added,1);
    assert.equal((await req('/api/courses')).data.items.length,1);
  });
  await t.test('erro e timeout da API preservam os dados',async()=>{
    const before=structuredClone(row);importFailure=true;
    assert.equal((await req('/api/courses/import','POST',{})).status,502);importFailure=false;
    importTimeout=true;const result=await req('/api/courses/import','POST',{});importTimeout=false;
    assert.equal(result.status,502);assert.match(result.data.error,/60 segundos/);assert.deepEqual(row,before);
  });
  await t.test('cadastro, edição e validação de curso',async()=>{
    const created=await req('/api/courses','POST',{title:'Curso',course_date:'2026-12-01T12:00:00Z',total_spots:10,available_spots:10});assert.equal(created.status,201);c=created.data.item;
    const edited=await req('/api/courses/'+c.id,'PATCH',{title:'Editado'});assert.equal(edited.status,200);assert.equal(edited.data.item.title,'Editado');
    assert.equal((await req('/api/courses','POST',{title:''})).status,400);
  });
  await t.test('aluno, encontro e QR Code',async()=>{
    const student=await req('/api/students','POST',{name:'Aluno',email:'aluno@example.test',enrollments:[c.id]});assert.equal(student.status,201);s=student.data.item;
    const meeting=await req('/api/meetings','POST',{title:'Aula',courseId:c.id,date:'2026-12-01T12:00:00Z',status:'aberto'});assert.equal(meeting.status,201);m=meeting.data.item;
    for(const [type,id] of [['student',s.id],['meeting',m.id]]){
      const qr=await fetch(base+`/api/qr?type=${type}&id=${id}`,{headers:{Cookie:cookie}});assert.equal(qr.status,200);assert.match(await qr.text(),/<svg/);
    }
  });
  await t.test('presença, duplicidade e encontro encerrado',async()=>{
    const body={studentId:s.id,meetingId:m.id,method:'manual'};
    assert.equal((await req('/api/attendances','POST',body)).status,201);
    assert.equal((await req('/api/attendances','POST',body)).status,409);
    const closed=(await req('/api/meetings','POST',{title:'Encerrada',courseId:c.id,date:'2026-12-01T12:00:00Z',status:'encerrado'})).data.item;
    const before=structuredClone(row);const result=await req('/api/attendances','POST',{...body,meetingId:closed.id});
    assert.equal(result.status,409);assert.match(result.data.error,/encerrado/);assert.deepEqual(row,before);
    await req('/api/meetings/'+closed.id,'PATCH',{status:'aberto'});
    assert.equal((await req('/api/attendances','POST',{...body,meetingId:closed.id})).status,201);
    await req('/api/meetings/'+closed.id,'DELETE');
  });
  await t.test('comunicados e mensagens',async()=>{
    assert.equal((await req('/api/announcements','POST',{title:'Aviso',message:'Mensagem'})).status,201);
    assert.equal((await req('/api/messages','POST',{studentId:s.id,text:'Resposta'})).status,201);
    assert.equal((await req('/api/messages')).data.items.length,1);
  });
  await t.test('falha de banco não apaga dados e conflito não sobrescreve',async()=>{
    const before=structuredClone(row);dbFailure=true;
    assert.equal((await req('/api/students')).status,503);dbFailure=false;assert.deepEqual(row,before);
    const a=await mod.loadDB(),b=await mod.loadDB();a.description='primeira gravação';await mod.saveDB(a);
    await assert.rejects(()=>mod.saveDB(b),e=>e.status===409);
    assert.equal((await mod.loadDB()).description,'primeira gravação');
  });
  await t.test('exclusão limpa relações e logout encerra sessão',async()=>{
    assert.equal((await req('/api/courses/'+c.id,'DELETE')).status,409);
    await req('/api/meetings/'+m.id,'DELETE');assert.deepEqual((await req('/api/attendances')).data.items,[]);
    assert.equal((await req('/api/courses/'+c.id,'DELETE')).status,200);
    assert.deepEqual((await req('/api/students/'+s.id)).data.item.enrollments,[]);
    await req('/api/students/'+s.id,'DELETE');assert.deepEqual((await req('/api/messages')).data.items,[]);
    await req('/api/logout','POST',{});assert.equal((await req('/api/students')).status,401);
    assert.ok(network.includes('PATCH database'));assert.ok(network.includes('GET courses'));
  });
});

test('botão de importação se recupera de sucesso e falhas',async()=>{
  const {runCourseImport}=await import('../public/js/import-courses.js');
  for(const scenario of ['success','request-error','refresh-error']){
    const button={disabled:false,innerHTML:'Buscar cursos do app',textContent:''};let calls=0;
    const request=async()=>{calls++;assert.equal(button.disabled,true);if(scenario==='request-error')throw Error('API indisponível');return {added:1,updated:0}};
    const refresh=async()=>{if(scenario==='refresh-error')throw Error('Falha ao atualizar')};
    if(scenario==='success')await runCourseImport(button,request,refresh,()=>{});
    else await assert.rejects(()=>runCourseImport(button,request,refresh,()=>{}));
    assert.equal(calls,1);assert.equal(button.disabled,false);assert.equal(button.innerHTML,'Buscar cursos do app');
  }
  let requested=false;await runCourseImport({disabled:true},async()=>{requested=true},()=>{},()=>{});assert.equal(requested,false);
});
