# Situação da integração

Foi preparada a tela /integracao.html para listar cursos do aplicativo, criar/abrir/encerrar encontros, listar alunos inscritos, registrar presença e mostrar QR de encontro. O servidor do painel encaminha somente as rotas previstas ao backend e mantém a chave administrativa fora do navegador.

Essa integração depende do módulo backend/recursos/encontros.js e da migração backend/database/encontros.sql. Código preparado não equivale a API publicada. A configuração exige APP_API_URL (origem HTTPS, sem caminho) e APP_ADMIN_KEY (mesmo valor de ADMIN_API_KEY do backend). A migração e a publicação não são executadas automaticamente.

O Android foi preparado para esses endpoints, com fila offline separada por conta e por encontro. A compilação completa e a validação em aparelho ainda são necessárias. Consulte backend/ENCONTROS.md para o contrato e roteiro de homologação.

As telas originais do painel continuam usando public.painel_web_dados de forma independente. Cursos, alunos, presenças, comunicados e mensagens criados nessas telas não são migrados nem sincronizados automaticamente. A nova tela usa cursos e inscrições do aplicativo; não reutiliza IDs de alunos independentes.

Continuam pendentes integração de comunicados/atendimento, carga horária e certificados, gestão individual de administradores, auditoria completa, validação do banco e homologação do conjunto. Não apresentar o sistema como totalmente integrado antes de concluir esses pontos.
