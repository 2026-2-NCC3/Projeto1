# Conferência da entrega

Base: PI_3CCOMP_202602_Proxima_Etapa_FINAL.pdf, especialmente os entregáveis e critérios da segunda entrega. Esta conferência delimita o painel; não certifica a execução do APK, análise estatística, banner ou apresentação.

| Exigência | Evidência atual | Pendência |
| --- | --- | --- |
| Painel administrativo | Interface, servidor e testes isolados | Validar a versão final publicada |
| Integração aplicativo/servidor/web | Importação de cursos por GET | Unificar IDs e operações com as APIs do aplicativo |
| Presença sem duplicidade por encontro | Regra implementada e testada no painel | Backend atual limita presença por inscrição em curso; falta contrato de encontros e uso pelo Android |
| Persistência e integridade | Supabase separado, controle de versão e relações validadas no servidor | Conferir RLS/privilégios, backup e restauração em ambiente autorizado; avaliar modelo relacional exigido |
| Cursos com carga horária e critérios de certificado | Cadastro parcial e marcador de certificado | Modelo e cálculo integrado de carga horária/elegibilidade ainda não concluídos |
| Comunicados e atendimento integrados | Coleções próprias do painel | Conectar às rotas administrativas existentes usando IDs reais e chave apenas no servidor |
| Documentação e testes de API | API.md, README e testes isolados | Acrescentar evidência do sistema integrado e da URL final |
| Documentação técnica em inglês | ACCEPTANCE-EN.md | Revisar em conjunto com a documentação global da equipe |
| APK e fluxo offline/sincronização | Fora dos testes do painel | Executar no Android, incluindo perda e retorno da conexão, sem duplicar presença |
| Publicação acessível | Preparação descrita no README | Publicar somente após aprovação; preservar backend existente |
| Histórico de código | Alterações locais | Commit/push dependem de autorização; não realizados |

## Atualização: encontros preparados

Código de integração por encontro foi acrescentado à cópia de trabalho: nova tela do painel, rotas no backend e fila/QR no Android. Os testes isolados passaram. A aplicação na pasta original, execução da migração, compilação do APK e homologação real continuam pendentes. A tabela acima descreve o que ainda não está comprovado no ambiente entregue.

## Contrato existente e limites

O backend local possui rotas administrativas de cursos, comunicados, atendimento e certificados. A presença é vinculada à inscrição em curso, sem ID de encontro. A existência dessas rotas nos arquivos não comprova que a mesma versão esteja publicada no Render.

Antes de conectar escritas, confirmar a versão publicada, credenciais administrativas, IDs compartilhados e ambiente de homologação. Não usar IDs de alunos criados no painel como se fossem usuários reais do aplicativo. Não enviar comunicados ou mensagens reais como teste.

## Roteiro final de homologação

Com dados fictícios autorizados: autenticar; cadastrar/consultar curso; matricular aluno; criar dois encontros do mesmo curso; registrar e consultar presença em ambos; tentar duplicidade; encerrar encontro e tentar nova presença; reiniciar servidor e confirmar persistência; testar offline e reconexão no app; confirmar comunicado e resposta de atendimento no destinatário correto; verificar certificado segundo critérios; sair e confirmar bloqueio de acesso.

Registrar ambiente, versão, resultado esperado/obtido e evidências para cada passo. Nenhum passo que grava dados reais foi executado por este roteiro. O painel não deve ser apresentado como totalmente integrado enquanto essas pendências permanecerem.
