const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const file = path.resolve(__dirname, '..', '.env');
if (fs.existsSync(file)) {
  console.log('A configuração .env já existe. Nenhuma senha foi substituída.');
  process.exit(0);
}
const email = 'admin@proximaetapa.local';
const pass = crypto.randomBytes(18).toString('base64url');
const lines = [
  '# ACESSO DE TESTE LOCAL - NÃO UTILIZAR EM PRODUÇÃO',
  'PORT=3000',
  `ADMIN_EMAIL=${email}`,
  `ADMIN_PASSWORD=${pass}`,
  'COURSES_API_URL=https://proxima-etapa-api-ncc3.onrender.com/courses',
  'SUPABASE_URL=https://SEU_PROJETO.supabase.co',
  'SUPABASE_SECRET_KEY=SUBSTITUA_PELA_CHAVE_SECRETA',
  ''
];
fs.writeFileSync(file, lines.join('\n'), {encoding:'utf8', mode:0o600, flag:'wx'});
console.log('Configuração local criada.');
console.log('Login:',email);
console.log('Senha temporária:',pass);
console.log('Guarde a senha; ela não aparece novamente.');

console.log('Antes de iniciar, configure SUPABASE_URL e SUPABASE_SECRET_KEY no .env e confira a tabela conforme o README.');
