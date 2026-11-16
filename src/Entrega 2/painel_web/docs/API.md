# API do painel

Esta API pertence exclusivamente ao painel. As rotas não são equivalentes às do aplicativo Android. Os identificadores criados no painel não identificam automaticamente usuários do aplicativo.

## Acesso

Corpos de requisição e respostas usam JSON, exceto o QR em SVG. Envie `Content-Type: application/json` e `X-Panel-Request: 1` em operações de escrita. O login recebe `email` e `password` e cria um cookie de sessão HttpOnly, SameSite=Strict, válido por oito horas. Em HTTPS no Render o cookie usa Secure. Reiniciar o processo encerra as sessões. As rotas protegidas exigem esse cookie.

| Método | Caminho | Finalidade |
| --- | --- | --- |
| GET/HEAD | `/health` | Disponibilidade do processo, sem comprovar o banco |
| POST | `/api/login` | Autenticar equipe |
| GET | `/api/session` | Consultar sessão, inclusive sem login |
| POST | `/api/logout` | Encerrar sessão |
| GET | `/api/meta` | Consultar modo e limitações de integração |
| GET | `/api/overview` | Resumo do painel |
| GET | `/api/{colecao}` | Listar registros |
| GET | `/api/{colecao}/{id}` | Consultar um registro |
| POST | `/api/{colecao}` | Criar registro |
| PUT/PATCH | `/api/{colecao}/{id}` | Atualizar cursos, encontros ou alunos |
| DELETE | `/api/{colecao}/{id}` | Excluir cursos, encontros, alunos ou comunicados |
| POST | `/api/courses/import` | Copiar cursos da API existente para o painel |
| GET | `/api/qr?type=student&id={id}` | QR `ALUNO-{id}` em SVG |
| GET | `/api/qr?type=meeting&id={id}` | QR `ENCONTRO-{id}` em SVG |

Coleções: `courses`, `meetings`, `students`, `attendances`, `announcements`, `messages`. Listagens retornam `items`; consultas e criações retornam `item`. As mensagens aceitam filtro `studentId`; as presenças aceitam filtro `meetingId`.

## Dados de criação

- Curso: `title`, `course_date` (data ISO), `description`, `location`, `category`, `total_spots`, `available_spots`, `points_awarded`, `has_certificate`, `is_active`. Título e data são obrigatórios; vagas totais devem ser inteiras positivas e as disponíveis devem estar entre zero e o total.
- Encontro: `title`, `courseId`, `date` (data ISO), `location`, `status` (`agendado`, `aberto` ou `encerrado`). O curso deve existir.
- Aluno: `name`, `email` único, `phone`, `status` (`ativo` ou `inativo`), `enrollments` (IDs de cursos existentes).
- Presença: `meetingId`, `studentId`, `method` (`manual`, `aluno` ou `encontro`). Exige aluno ativo, inscrição e encontro não encerrado; duplicidade por aluno/encontro é rejeitada.
- Comunicado: `title`, `message`. Fica no painel; não envia notificação ao aplicativo.
- Mensagem: `studentId`, `text`. Fica no painel; não envia mensagem ao aplicativo.

PATCH preserva campos omitidos. PUT exige os campos necessários à validação. Excluir encontro remove suas presenças. Excluir aluno remove suas mensagens e presenças. Curso com encontros não pode ser excluído; a exclusão permitida remove as inscrições correspondentes.

## Falhas e testes

401: sessão ausente ou credenciais inválidas. 403: cabeçalho de escrita ausente. 400/422: dados ou relacionamento inválidos. 404: rota/registro inexistente. 409: duplicidade, encontro encerrado, dependência ou conflito de versão. 429: limite de login. 503: indisponibilidade/configuração do banco. Consulte a mensagem da resposta; não repita gravações automaticamente após falha de comunicação.

Execute `npm test` para exercitar os fluxos com banco simulado, sem carregar `.env` e sem acessar serviços reais. O teste não comprova integração Android, leitura de QR pela câmera, permissões do banco real ou publicação.
