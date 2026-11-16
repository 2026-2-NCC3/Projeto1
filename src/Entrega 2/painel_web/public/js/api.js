const BASE='/api';
export async function api(path,method='GET',body) {
  const options={method,credentials:'same-origin',headers:{'Accept':'application/json'}};
  if(method!=='GET') options.headers['x-panel-request']='1';
  if(body!==undefined) { options.headers['Content-Type']='application/json';options.body=JSON.stringify(body); }
  let response;
  try {response=await fetch(BASE+path,options)} catch {throw Error('Falha de conexão com o servidor do painel.');}
  let data;
  try {data=await response.json()} catch {throw Error('Resposta inválida do servidor.');}
  if(!response.ok) throw Error(data.error||'Não foi possível concluir esta ação.');
  return data;
}
