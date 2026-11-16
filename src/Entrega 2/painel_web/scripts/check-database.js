'use strict';
const {loadDB}=require('../server');
loadDB().then(()=>console.log('Consulta ao banco concluída; formato dos dados válido. Confira RLS e privilégios com database/verificar_permissoes.sql.')).catch(error=>{console.error(error.status ? error.message : 'Não foi possível validar o banco.');process.exitCode=1;});
