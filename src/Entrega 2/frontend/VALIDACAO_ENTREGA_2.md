# Entrega 2 — desenvolvimento mobile

## Primeira implementação

- Logout limpa a sessão e a pilha de navegação.
- Meu perfil consulta GET /auth/me com Bearer token. Resposta 401 encerra a sessão.
- O catálogo público é armazenado em SQLite após uma resposta válida da API.
- Em falhas de rede/servidor, a lista usa a última resposta salva e informa sua origem.
- Atualizar cursos repete a consulta e substitui o cache, inclusive quando a API retorna lista vazia.
- Foram preservados ID, descrição, local e categoria dos cursos, antes descartados.
- Apenas MainActivity permanece como entrada do launcher.
- Login não registra o e-mail do aluno no log.

## Validação manual no Android Studio

1. Abra esta pasta frontend e execute app em um emulador.
2. Entre com uma conta de teste válida. Abra Meu perfil e confira nome e e-mail.
3. Volte a Cursos, aguarde o carregamento e abra um curso.
4. Desligue a internet do emulador e toque em Atualizar cursos. A última consulta deve aparecer com aviso de indisponibilidade. Pode haver espera pelo timeout de rede.
5. Religue a conexão e atualize. O aviso deve mudar para Cursos atualizados.
6. Escolha Sair. Pressionar Voltar não deve reabrir a área autenticada.
7. Para testar SQLite automaticamente, execute CursosCacheTest com um emulador conectado. Ele utiliza um banco exclusivo de teste.

## Pendências da entrega completa

- Agenda individual, inscrições, QR/check-in e histórico precisam de contratos e endpoints no backend: server.js atual oferece apenas autenticação, perfil e catálogo/CRUD administrativo de cursos.
- Implementar fila de presenças local, sincronização com deduplicação no servidor e isolamento por aluno.
- Card, certificados, testes de perfil, mensagens/chat e notificações ainda precisam ser implementados e integrados.
- Sessão continua em memória: fechar o processo exige novo login. Persistência segura da sessão ainda não foi implementada.
- Configurações ainda não possui tela; Início e Meus cursos continuam apontando para o catálogo disponível.
- Cache atual é somente do catálogo público; não representa inscrições nem agenda pessoal e não habilita login offline.
- É necessário executar validação visual e funcional em dispositivo, além dos testes locais de compilação.
