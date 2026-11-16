const {test} = require('node:test');
const assert = require('node:assert/strict');
const {solicitar,destino} = require('../integracao');

test('proxy permite apenas rotas administrativas previstas', () => {
    assert.equal(destino('/api/integracao/courses','GET'),'/courses');
    assert.equal(destino('/api/integracao/encontros','POST'),'/admin/encontros');
    for (const [path,method] of [['/api/integracao/auth/register','POST'],['/api/integracao/courses','DELETE'],['/api/integracao/encontros/../users','GET']]) assert.throws(()=>destino(path,method), {status:404});
});

test('integração usa chave somente no servidor, bloqueia redirects e exige HTTPS', async () => {
    process.env.APP_API_URL='https://backend-test.invalid';
    process.env.APP_ADMIN_KEY='fake-test-key';
    const result=await solicitar('/admin/encontros','POST',{titulo:'Teste'},async(url,options)=>{
        assert.equal(url.href,'https://backend-test.invalid/admin/encontros');
        assert.equal(options.headers['x-admin-key'],'fake-test-key');
        assert.equal(options.redirect,'error');
        assert.deepEqual(JSON.parse(options.body),{titulo:'Teste'});
        return Response.json({id:'test'},{status:201});
    });
    assert.deepEqual(result,{status:201,data:{id:'test'}});
    for(const base of ['http://example.test','https://user:pass@example.test','https://example.test/path','https://example.test?key=x']){
        process.env.APP_API_URL=base;
        await assert.rejects(solicitar('/courses','GET',undefined,()=>{throw Error('Network must not run');}),{status:503});
    }
    delete process.env.APP_API_URL;delete process.env.APP_ADMIN_KEY;
});

test('falha ou credencial inválida não vaza resposta privada nem afirma sucesso',async()=>{
    process.env.APP_API_URL='https://backend-test.invalid';process.env.APP_ADMIN_KEY='fake-test-key';
    await assert.rejects(solicitar('/admin/encontros','GET',undefined,async()=>Response.json({erro:'secret database details'},{status:401})),error=>error.status===503&&!error.message.includes('secret'));
    await assert.rejects(solicitar('/admin/encontros','GET',undefined,async()=>{throw Error('offline');}),{status:503});
    delete process.env.APP_API_URL;delete process.env.APP_ADMIN_KEY;
});
