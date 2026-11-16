# Acesso para avaliação

## Aplicativo Android

[APK disponível nesta pasta](app-debug.apk).

Esta versão não foi recompilada com as alterações recentes de encontros. A equipe deve fornecer o APK final validado e informar sua versão antes da avaliação do fluxo integrado.

As credenciais de aluno e responsável devem ser enviadas privadamente aos avaliadores. Usar contas de demonstração autorizadas, sem dados pessoais reais de alunos.

## API, painel e publicação

- [Backend existente no Render](https://proxima-etapa-api-ncc3.onrender.com/)
- [Consulta de cursos](https://proxima-etapa-api-ncc3.onrender.com/courses)
- [Código e instruções do backend](../../../src/Entrega%202/backend/README.md)
- [Contrato de encontros e homologação](../../../src/Entrega%202/backend/ENCONTROS.md)
- [Código e instruções do painel](../../../src/Entrega%202/painel_web/README.md)

Na versão local correta do painel, execute `npm start` em `src/Entrega 2/painel_web` depois de conferir a configuração privada. O endereço padrão é `http://127.0.0.1:3000`, acessível somente no computador em que o servidor está rodando. Se a porta já estiver ocupada, confirme qual versão está em execução antes de iniciar outra.

O `.env` local já recebeu o endereço da API e a chave administrativa. Essa chave fica somente no servidor; não deve ser copiada para JavaScript público, APK ou documentação.

**URL pública do painel atual: pendente.** A configuração local e a criação das tabelas não publicam as rotas novas do backend. As funções de encontros dependem dessa publicação.

## Documento de acessos recebido

O PDF `Acessos_Avaliacao_Proxima_Etapa.pdf`, fornecido como referência, contém credenciais e um endereço Vercel que não foi confirmado como a publicação deste repositório. Não foi copiado para esta pasta nem usado para preencher senhas ou declarar a publicação concluída.

Arquivos antigos de acesso já existentes nesta pasta não foram alterados. O responsável deve conferir seu conteúdo e destinatários antes de publicar a pasta. Este índice não reproduz nem vincula documentos com credenciais.
