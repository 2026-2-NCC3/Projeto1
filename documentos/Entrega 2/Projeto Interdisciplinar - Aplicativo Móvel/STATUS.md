# Status e pendências — Projeto Interdisciplinar

Atualização: 16/11/2026. Este registro descreve verificações realizadas, sem declarar a entrega global concluída.

## Verificado

- Arquivos de backend, Android e painel aplicados localmente, com backup dos arquivos substituídos.
- Backend: 17 testes automatizados aprovados, com banco simulado.
- Painel: 15 testes aprovados, incluindo acesso, integração HTTP simulada e QR.
- Testes da interpretação dos códigos QR em Java realizados; não equivalem à compilação do aplicativo Android.
- Tela web de encontros conferida no navegador com dados fictícios.
- Tabelas `app_encontros` e `app_presencas_encontros` criadas no Supabase, com RLS e acesso público restrito.
- Restrições de duplicidade, referências e situação do encontro testadas no PostgreSQL real. Os registros de teste foram revertidos.
- Endereço e chave administrativa configurados no `.env` local do painel, ignorado pelo Git.
- API existente respondeu à consulta de cursos.

## Pendente

- Publicar as novas rotas de encontros no backend Render. O serviço atual recebe o código do GitHub; envio depende de ação/autorização do responsável pelo repositório.
- Publicar o painel e registrar a URL pública correta.
- Testar conjuntamente painel, API publicada, banco e aplicativo.
- Compilar o APK atualizado e substituir a versão disponível somente após validação em aparelho.
- Validar câmera, QR, histórico, troca de conta, operação offline e sincronização.
- Concluir integração de comunicados, atendimento, carga horária e critérios de certificados.
- Revisar o documento de extensão, a declaração de uso de IA e os materiais de apresentação com a equipe.

## Limites das verificações

Os testes automatizados de API não gravaram dados reais. A validação das tabelas ocorreu em transação revertida. Não foi realizado teste de concorrência entre conexões simultâneas nem homologação completa do Android.

Nenhum commit, push ou deploy foi realizado pelo assistente durante a organização desta documentação.
