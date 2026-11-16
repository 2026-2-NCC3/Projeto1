# Explicação do código e organização

## Componentes

| Componente | Código | Responsabilidade |
| --- | --- | --- |
| Android | [frontend](../../../src/Entrega%202/frontend/) | Telas do aluno, leitura de QR, chamadas à API e fila local |
| Backend | [backend](../../../src/Entrega%202/backend/) | Autenticação, regras de acesso e gravação no PostgreSQL |
| Painel web | [painel_web](../../../src/Entrega%202/painel_web/) | Interface administrativa e comunicação de servidor com a API |

## Fluxo de encontros

Na integração preparada, o navegador envia a operação ao servidor do painel. Esse servidor usa a chave administrativa para chamar a API do aplicativo. A API valida o encontro, a inscrição e o acesso antes de gravar a presença no PostgreSQL.

No Android, `ApiService` declara as chamadas HTTP; `EncontroRequest` e `EncontroResposta` representam os dados de envio e confirmação; `CodigoEncontro` valida a estrutura do código; `FilaEncontros` mantém pendências por conta e encontro; `LeitorQrActivity` coordena a interação da tela.

Essas classes separam comunicação, validação, persistência e interface. Essa explicação documenta o sistema para Projeto Interdisciplinar; não substitui os documentos ou diagramas exigidos pela disciplina de POO.

O banco impede duplicidade por encontro/inscrição. O reenvio de uma presença existente deve confirmar o registro sem duplicá-lo. A fila do Android só é removida quando há confirmação explícita do servidor.

## Dados e limitações

As tabelas de encontros estão criadas. As rotas correspondentes ainda precisam ser publicadas. A tela “Encontros do aplicativo” usa IDs compartilhados com a API; as telas anteriores do painel continuam com armazenamento independente em `painel_web_dados` e não sincronizam automaticamente todos os recursos.

O histórico antigo por curso e o histórico novo por encontro são distintos. Não há conversão automática de presenças antigas para encontros arbitrários. As sessões administrativas do painel ficam em memória e há uma conta de equipe configurada; reiniciar o servidor encerra essas sessões.

Consulte [status](STATUS.md) e [roteiro de testes](TESTES.md) para o que falta validar.
