# Integração de presença por encontro — preparada, não publicada

## Aplicação controlada

As mudanças adicionam encontros vinculados aos cursos existentes e presenças vinculadas às inscrições existentes. Não convertem presenças antigas por curso em presenças de encontros: essa correspondência não pode ser inferida com segurança.

1. Revisar `database/encontros.sql` com uma cópia autorizada do banco. O script pressupõe IDs UUID nas tabelas `courses` e `enrollments`. Confirmar esses tipos antes de executá-lo. Não executar diretamente em produção sem validação e aprovação.
2. Executar e validar a migração na cópia. As duas tabelas novas usam RLS, com acesso público revogado. O backend deve conectar com uma função de banco autorizada (proprietário das tabelas ou política específica revisada). Não liberar acesso a `anon` ou `authenticated` para fazer o servidor funcionar.
3. Executar `npm test` no backend e no painel. Os testes usam simulações; não substituem execução da migração e concorrência no PostgreSQL real.
4. Configurar no servidor do painel `APP_API_URL` com a origem HTTPS da API e `APP_ADMIN_KEY` com a mesma chave administrativa do backend. Não colocar essa chave no navegador ou no Android.
5. Publicar backend e painel apenas após aprovação. Gerar novo APK, instalar em aparelho de teste e validar todo o fluxo.

Referência: [Supabase — Row Level Security](https://supabase.com/docs/guides/database/postgres/row-level-security).

## Contrato

| Método | Rota | Acesso e uso |
| --- | --- | --- |
| GET | `/admin/encontros` | Chave administrativa; lista com curso e situação |
| GET | `/admin/encontros/:id` | Chave administrativa; consulta um encontro |
| POST | `/admin/encontros` | Chave administrativa; `course_id`, `titulo`, `data` ISO; cria agendado |
| PATCH | `/admin/encontros/:id` | Chave administrativa; `status`: agendado, aberto ou encerrado |
| GET | `/admin/encontros/:id/alunos` | Chave administrativa; inscritos e presença no encontro |
| POST | `/admin/encontros/:id/presencas` | Chave administrativa; `aluno_id` |
| GET | `/encontros` | JWT; responsáveis veem todos, alunos veem seus cursos |
| POST | `/encontros/:id/presencas` | JWT; `codigoQrCode`: responsável usa ALUNO-UUID, aluno usa ENCONTRO-UUID para si |
| GET | `/me/presencas-encontros` | JWT; histórico somente da identidade autenticada |

Criação de presença retorna 201 e `confirmado: true`. Reenvio de presença existente retorna 200 e `repetido: true`, inclusive depois do fechamento, sem criar outra presença. Encontro não aberto ou falta de inscrição retorna 422; aluno tentando registrar outra pessoa recebe 403. Erros nunca devem ser interpretados como confirmação.

O controle de duplicidade usa uma restrição única por encontro/inscrição. Uma transação mantém o encontro e a inscrição estáveis durante o registro; o fechamento aguarda a transação concorrente. A data confirmada é a data do servidor.

## Android

A tela de leitura passa a listar encontros. Um aluno pode ler o QR do encontro para si; um responsável seleciona o encontro e lê o QR do aluno. A fila `encontros.db` é separada do banco legado `presencas.db` e identifica o dono da sessão, o encontro e o código. Uma conta não envia pendências de outra.

O registro entra na fila antes da tentativa de envio. A fila só é removida depois de resposta bem-sucedida com `confirmado: true`. A abertura da tela e o botão “Atualizar e sincronizar” tentam enviar pendências. Não há sincronização em segundo plano com o aplicativo fechado. Se um encontro fechar antes de uma presença offline chegar, a equipe precisa revisar e reabrir o encontro para aceitá-la. A data local não contorna esse bloqueio.

O histórico por encontro está no botão da nova tela; o histórico antigo por curso continua disponível no fluxo anterior. O leitor novo não envia a fila legada automaticamente. Pendências legadas devem ser revisadas separadamente; não devem ser convertidas para um encontro arbitrário.

## Verificações ainda necessárias

- Executar migração em PostgreSQL de homologação e verificar tipos, RLS, privilégios e restrição única sob concorrência real.
- Compilar o APK e testar com aluno e responsável, câmera, encerramento, dois encontros distintos, perda de resposta, reinício, mudança de conta e sincronização.
- Integrar demais recursos do painel, especialmente comunicados, atendimento, carga horária e critérios de certificado. Esta etapa não conclui esses recursos nem a entrega global.
