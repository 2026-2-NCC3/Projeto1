# Roteiro de testes — Projeto Interdisciplinar

## Pré-condições

Usar ambiente e contas de demonstração autorizados. Confirmar a versão do APK, a URL do painel, a versão do backend e a existência das tabelas novas. Não executar o roteiro com registros pessoais reais sem autorização.

## Fluxo de avaliação integrado

| Etapa | Ação | Resultado esperado |
| --- | --- | --- |
| 1 | Entrar com aluno e responsável | Acesso conforme a identidade e o perfil |
| 2 | Consultar curso e inscrição | Aplicativo e painel identificam o mesmo curso e aluno |
| 3 | Criar dois encontros do curso no painel | Ambos aparecem vinculados ao curso correto |
| 4 | Abrir o primeiro encontro | QR disponível para registro |
| 5 | Aluno ler o QR do encontro | Presença registrada somente para o aluno autenticado |
| 6 | Responsável ler o QR de aluno inscrito | Presença vinculada ao encontro selecionado |
| 7 | Repetir o envio | Nenhuma presença duplicada |
| 8 | Registrar presença no segundo encontro | Nova presença permitida, apesar de ser o mesmo curso |
| 9 | Encerrar encontro e tentar nova presença | Nova presença rejeitada |
| 10 | Reenviar presença já confirmada após fechar | Confirmação idempotente, sem novo registro |
| 11 | Desconectar durante o envio e reabrir a tela | Pendência conservada no aparelho |
| 12 | Reconectar e atualizar/sincronizar | Confirmação única ou erro claro; sem confirmação falsa |
| 13 | Trocar de conta com pendências | Uma conta não envia a fila da outra |
| 14 | Consultar histórico | Apenas os registros do aluno autenticado |
| 15 | Sair e tentar acessar dados restritos | Acesso bloqueado |

Se um encontro fechar antes da chegada de uma presença offline, a equipe deve revisar a ocorrência; o aplicativo não deve contornar a regra de fechamento.

## Registro de evidências

Para cada etapa registrar data, versão, resultado obtido e evidência sem senhas ou dados pessoais. Os passos acima são um roteiro; não estão declarados como aprovados. Consultar [status atual](STATUS.md) para distinguir testes já executados e pendentes.

Os testes automatizados podem ser executados com `npm test` nas pastas do backend e do painel. Eles usam simulações e não substituem o roteiro em aparelho e ambiente publicado.
