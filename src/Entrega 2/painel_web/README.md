# Próxima Etapa — painel independente

HTML, CSS, JavaScript e Node.js 22 ou superior. Sem dependências de instalação.

## Documentação da entrega

- [API do painel](docs/API.md)
- [Arquitetura e critérios de aceitação em inglês](docs/ACCEPTANCE-EN.md)
- [Conferência e pendências do PDF](docs/PENDENCIAS-ENTREGA.md)

## Onde os dados ficam

A tela adicional “Encontros do aplicativo” (`/integracao.html`) usa o backend do aplicativo por meio do servidor do painel. Ela depende da nova versão do backend e da migração de encontros, ainda não publicadas. Configure APP_API_URL e APP_ADMIN_KEY somente no servidor. Veja `docs/INTEGRACAO-FUTURA.md`. As demais telas descritas abaixo continuam independentes.

Este servidor lê e grava a linha principal da tabela public.painel_web_dados no Supabase. Os dados do painel são independentes das tabelas e funcionalidades do aplicativo Android. A API Render existente é consultada somente por GET /courses; importar cursos cria uma cópia no painel, não uma sincronização de escrita com o aplicativo.

Uma instalação nova começa vazia. O arquivo data/painel-local.json de versões anteriores não é utilizado nem importado automaticamente. Excluir a pasta data não limpa o banco.

## Configuração

1. Execute npm run setup apenas se não existir .env. O comando não substitui configurações existentes.
2. Configure ADMIN_EMAIL, ADMIN_PASSWORD, SUPABASE_URL e SUPABASE_SECRET_KEY no .env. Use a chave secreta de servidor iniciada por sb_secret_. Nunca envie essa chave ao navegador, Git ou mensagens.
3. Em um ambiente de homologação autorizado, revise database/painel_web_dados.sql antes de executar. O script cria a tabela independente e restringe o acesso. Não modifica tabelas do aplicativo. Sua execução não acontece automaticamente.
4. Execute npm run check:database para consultar a estrutura e a resposta do banco sem gravar dados. Execute também as consultas de database/verificar_permissoes.sql no SQL Editor para conferir RLS e privilégios.
5. Execute npm start e abra http://127.0.0.1:3000.

## Testes

npm test executa verificações de sintaxe, fluxos HTTP e comportamento do botão de importação. Os testes definem NODE_ENV=test antes de carregar o servidor, ignoram o .env, usam Supabase simulado em memória e bloqueiam destinos externos. Nenhuma chave real ou instalação Docker é necessária.

Os testes simulados não confirmam permissões, existência da tabela ou credenciais de um projeto Supabase real. Não execute testes de escrita contra produção.

## Regras

- Presença exige aluno ativo e inscrito no curso, sem duplicidade por encontro. Encontro encerrado deve ser reaberto antes de registrar presença.
- Excluir curso com encontros é bloqueado; excluir curso sem encontros remove suas inscrições dos alunos.
- Gravações usam a versão atualizado_em para detectar conflitos. Atualize a tela após conflito.
- A importação aguarda até 60 segundos para acomodar a inicialização do Render. Se falhar, os dados existentes são preservados e o botão volta ao estado normal. Tente novamente depois; não há repetição automática de escrita.
- QR de encontro ainda requer integração futura com o Android. Câmera depende do navegador e de permissão.

## Hospedagem futura

Node Web Service; diretório raiz src/Entrega 2/painel_web, início npm start e verificação de saúde /health. Não há compilação. Em Render, RENDER=true faz o servidor escutar em 0.0.0.0 e usar cookie Secure; não configure HOST=127.0.0.1 lá. Defina as variáveis secretas na plataforma, sem copiar .env para o repositório.

O login atual usa uma conta de equipe e sessões em memória: reiniciar encerra sessões. Não há gestão individual de usuários administrativos nem trilha de auditoria completa. O armazenamento JSON em uma linha é adequado ao protótipo, não substitui uma modelagem relacional para grande volume.

Este procedimento não realiza commit, push, deploy ou alterações no Android. O backend Render existente deve ser preservado.
