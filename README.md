# PRÓXIMA ETAPA - APP DO ALUNO

Aplicativo mobile integrado a dados educacionais para acompanhamento da jornada do aluno.

Projeto Interdisciplinar do 3º semestre de Ciência da Computação da FECAP, Turma A, período noturno, desenvolvido no segundo semestre de 2026.

> Status: Em planejamento e desenvolvimento.
>
> Este README apresenta o escopo planejado. As funcionalidades deverão ser atualizadas conforme forem implementadas e validadas pelo grupo.

---

## 1. SOBRE O PROJETO

A Próxima Etapa é uma organização sem fins lucrativos que atua na democratização do acesso à educação superior e à orientação profissional para jovens de escolas públicas.

O projeto Próxima Etapa - App do Aluno propõe uma solução digital integrada, formada por aplicativo mobile, API REST, banco de dados e módulo web administrativo. A solução deverá concentrar informações e serviços relacionados à jornada dos estudantes atendidos pela organização.

O foco principal do projeto será a área do aluno e suas funcionalidades. Cada estudante deverá possuir acesso individual e visualizar somente os próprios dados autorizados.

A solução deverá permitir o acompanhamento de cursos, palestras, agenda, presença, certificados, card digital, perfil, resultados, mensagens e notificações.

Site institucional da organização:
https://proximaetapa.org.br/

---

## 2. PROBLEMA

As informações relacionadas aos estudantes, cursos, atividades, presenças, certificados e comunicados podem estar distribuídas em diferentes canais. Essa distribuição pode dificultar o acesso do estudante às informações e o acompanhamento de sua própria jornada educacional.

---

## 3. SOLUÇÃO PROPOSTA

Desenvolver uma plataforma integrada que ofereça:

- Aplicativo mobile voltado ao estudante;
- API REST para comunicação entre os módulos;
- Banco de dados para armazenamento das informações;
- Módulo web administrativo para gerenciamento dos dados;
- Autenticação individual por RA e senha;
- QR Code individual para identificação e registro em cursos ou palestras;
- Consulta de cursos, palestras e agenda;
- Histórico de presença e participação;
- Consulta de certificados e diplomas organizados por instituição de ensino;
- Perfil do estudante com resultados de cursos, teste de perfil e informações gerais;
- Mensagens, notificações e canal de atendimento;
- Possível integração futura com chatbot ou assistente de IA.

---

## 4. OBJETIVO GERAL

Desenvolver uma solução tecnológica composta por aplicativo mobile, API REST, banco de dados e módulo web, proporcionando aos estudantes atendidos pela Próxima Etapa uma experiência digital integrada para acompanhamento de cursos, agenda, presença, certificados, perfil e comunicação com a organização.

---

## 5. OBJETIVOS ESPECÍFICOS

- Desenvolver um aplicativo centrado na experiência do estudante;
- Permitir login individual com RA e senha;
- Integrar o aplicativo ao banco de dados por meio de uma API REST;
- Permitir a consulta de cursos, palestras e atividades;
- Exibir a agenda individual do estudante;
- Implementar QR Code individual para identificação e controle de presença;
- Evitar registros de presença duplicados;
- Disponibilizar o histórico de participação;
- Disponibilizar card digital e certificados;
- Organizar certificados e diplomas por instituição de ensino, quando aplicável;
- Apresentar resultados de cursos e testes de perfil na área do estudante;
- Enviar notificações sobre novos cursos, palestras e alterações de agenda;
- Implementar mensagens e um canal para esclarecimento de dúvidas;
- Permitir o bloqueio de acesso de estudantes suspensos por meio de uma lista de restrição;
- Aplicar técnicas de análise descritiva sobre dados autorizados ou simulados;
- Aplicar conceitos de Programação Orientada a Objetos e estruturas de dados;
- Preservar a identidade visual da Próxima Etapa;
- Manter o aplicativo leve e adequado a diferentes dispositivos;
- Desenvolver a solução considerando segurança, acessibilidade, privacidade e LGPD.

---

## 6. PÚBLICO-ALVO

Estudantes participantes dos cursos, palestras, atividades e programas educacionais oferecidos pela Próxima Etapa.

---

## 7. ESCOPO DO PROJETO

### 7.1 Aplicativo mobile - Área do aluno

O aplicativo será o principal canal do estudante com a Próxima Etapa. O foco da apresentação e da demonstração deverá estar na área do aluno e nas funções disponíveis para acompanhamento da jornada educacional.

### 7.2 API REST

A API será responsável por validar e processar as requisições entre o aplicativo, o módulo web e o banco de dados.

### 7.3 Banco de dados

O banco de dados armazenará estudantes, cursos, palestras, atividades, inscrições, presenças, certificados, cards, testes, resultados, mensagens, notificações e restrições de acesso.

### 7.4 Módulo web administrativo

O módulo web permitirá à equipe autorizada da organização gerenciar cursos, atividades, certificados, mensagens, notificações e demais informações exibidas no aplicativo.

---

## 8. FUNCIONALIDADES PLANEJADAS

### 8.1 Autenticação

- Login com RA e senha;
- Logout;
- Manutenção segura da sessão;
- Recuperação de acesso, se aprovada para o escopo;
- Restrição dos dados ao estudante autenticado;
- Verificação da situação do estudante antes de liberar o acesso.

### 8.2 Área inicial do aluno

- Resumo da jornada do estudante;
- Próximos cursos e palestras;
- Agenda próxima;
- Notificações recentes;
- Atalhos para cursos, QR Code, certificados e perfil.

### 8.3 Cursos, palestras e atividades

- Consulta de oportunidades disponíveis;
- Consulta de inscrições;
- Exibição de descrição, local, período, carga horária e quantidade de encontros;
- Aviso sobre novos cursos e palestras.

### 8.4 Agenda

- Agenda individual;
- Datas e horários dos encontros;
- Locais e informações das atividades;
- Atualizações e alterações de programação.

### 8.5 QR Code individual

- QR Code personalizado para cada estudante;
- Identificação do estudante em cursos e palestras;
- Registro de entrada ou presença;
- Associação da presença ao estudante, atividade, data e horário;
- Prevenção de presença duplicada para o mesmo encontro;
- Validação da situação do estudante antes do registro.

### 8.6 Histórico

- Consulta de presenças;
- Percentual de participação;
- Cursos concluídos ou em andamento;
- Histórico de atividades.

### 8.7 Card digital

- Card individual do estudante;
- Exibição somente de informações autorizadas;
- Identificação visual integrada ao aplicativo.

### 8.8 Certificados e diplomas

- Listagem de certificados obtidos;
- Visualização e download;
- Organização por curso;
- Organização por faculdade ou instituição de ensino, quando aplicável;
- Disponibilização condicionada aos critérios definidos pela Próxima Etapa.

### 8.9 Testes de perfil

- Questionários de orientação educacional e profissional;
- Perguntas, alternativas e pontuação;
- Resultado exibido no perfil do estudante;
- Histórico de resultados, se aprovado para o escopo.

Os resultados terão finalidade de orientação educacional e profissional e não serão apresentados como diagnóstico psicológico ou clínico.

### 8.10 Perfil do estudante

- Dados pessoais autorizados;
- Resultados de cursos;
- Resultado do teste de perfil ou DISC, caso o instrumento seja autorizado e definido pela organização;
- Informações gerais da jornada do estudante;
- Atualização de dados permitidos.

### 8.11 Mensagens e atendimento

- Comunicados da organização;
- Canal de dúvidas;
- Histórico de mensagens;
- Possível combinação futura do canal de mensagens com chatbot ou assistente de IA;
- Encaminhamento para atendimento humano quando necessário.

### 8.12 Notificações

- Novos cursos;
- Novas palestras;
- Alterações de agenda;
- Certificados disponíveis;
- Informações relevantes da organização.

### 8.13 Lista de restrição

- Registro administrativo de estudantes suspensos ou temporariamente impedidos;
- Bloqueio controlado de acesso ou check-in;
- Motivo e período da restrição disponíveis somente para usuários administrativos autorizados;
- Registro das alterações para auditoria;
- Tratamento respeitoso e adequado à LGPD.

### 8.14 Sincronização

- Comunicação entre aplicativo e API;
- Persistência local de informações essenciais;
- Tratamento da indisponibilidade da internet;
- Sincronização posterior;
- Mensagens claras em caso de falha.

---

## 9. REQUISITOS FUNCIONAIS

- RFM01 - Autenticação: permitir login e logout do estudante e manter a sessão com segurança.
- RFM02 - Página inicial: apresentar resumo da jornada, atividades, mensagens e atalhos.
- RFM03 - Cursos: consultar cursos disponíveis e cursos inscritos.
- RFM04 - Novos cursos: informar novas oportunidades ao estudante.
- RFM05 - Agenda: exibir agenda individual com cursos, palestras e atividades.
- RFM06 - QR Code: permitir identificação e registro de presença por QR Code.
- RFM07 - Histórico: permitir consulta de presenças e participação.
- RFM08 - Card do aluno: exibir card digital com informações autorizadas.
- RFM09 - Certificados: listar, visualizar e permitir download de certificados.
- RFM10 - Testes de perfil: permitir resposta aos questionários e consulta de resultados.
- RFM11 - Mensagens: disponibilizar mensagens e comunicados.
- RFM12 - Canal de dúvidas: permitir comunicação entre estudante e organização.
- RFM13 - Notificações: enviar avisos sobre cursos, palestras, agenda e certificados.
- RFM14 - Perfil: permitir consulta e atualização de informações autorizadas.
- RFM15 - Sincronização: sincronizar dados locais com a API e tratar falhas de conexão.
- RFM16 - Restrição de acesso: impedir acesso ou check-in de estudante com restrição ativa.
- RFM17 - Resultados do aluno: reunir resultados de cursos, participação e testes autorizados.
- RFM18 - Organização de documentos: organizar certificados e diplomas por curso e instituição.

---

## 10. REGRAS DE NEGÓCIO

- Cada estudante deverá possuir uma conta individual;
- O login inicial será realizado por RA e senha;
- Cada estudante visualizará somente os próprios dados autorizados;
- Cada estudante deverá possuir QR Code individual;
- O QR Code deverá identificar o estudante sem expor dados pessoais desnecessários;
- Cada presença estará relacionada ao estudante, curso ou atividade, data e horário;
- O sistema deverá evitar presença duplicada no mesmo encontro;
- Um estudante com restrição ativa não poderá realizar check-in enquanto a restrição estiver vigente;
- Informações sobre suspensão ou restrição serão acessíveis apenas a usuários autorizados;
- Cursos poderão ter diferentes datas, horários, locais, cargas horárias e quantidades de encontros;
- Certificados serão liberados somente após o estudante atender aos critérios cadastrados;
- Testes de perfil deverão possuir perguntas, alternativas, pontuação e classificação previamente definidas;
- Resultados de perfil serão usados apenas para orientação educacional e profissional;
- Mensagens poderão ser destinadas a todos os estudantes ou a grupos específicos;
- A API será a camada de comunicação entre os clientes e o banco de dados;
- O banco disponibilizado pela organização deverá ser preservado;
- Dados reais somente poderão ser usados quando houver autorização;
- Testes e apresentações deverão priorizar dados fictícios, anonimizados ou previamente preparados.

---

## 11. REQUISITOS NÃO FUNCIONAIS

### 11.1 Plataforma

- A entrega acadêmica principal deverá funcionar em dispositivos Android;
- Uma versão para iOS poderá ser estudada como evolução futura, sem substituir a entrega Android obrigatória;
- O código deverá ser organizado para facilitar manutenção e possível expansão multiplataforma.

### 11.2 Usabilidade e identidade visual

- Interface simples e adequada ao público jovem;
- Preservação da identidade visual da Próxima Etapa;
- Navegação consistente;
- Textos claros e legíveis;
- Mensagens de erro compreensíveis.

### 11.3 Desempenho

- Aplicativo leve;
- Redução de imagens e arquivos desnecessariamente grandes;
- Respostas adequadas nas consultas;
- Paginação ou carregamento gradual quando necessário;
- Uso eficiente do armazenamento local.

### 11.4 Acessibilidade

- Contraste adequado;
- Textos legíveis;
- Rótulos compreensíveis;
- Elementos interativos identificados;
- Navegação clara;
- Boas práticas de acessibilidade no Android.

### 11.5 Segurança e privacidade

- Comunicação HTTPS;
- Senhas protegidas e nunca armazenadas em texto simples;
- Tokens e sessões armazenados de forma segura;
- Consultas parametrizadas;
- Controle de acesso por perfil;
- Coleta mínima de dados;
- Proteção contra exposição indevida de dados pessoais;
- Variáveis sensíveis mantidas fora do repositório.

### 11.6 Manutenibilidade

- Separação de responsabilidades em camadas;
- Organização adequada de classes e componentes;
- Documentação da API;
- Histórico de commits;
- Testes e tratamento de erros.

---

## 12. PRIORIZAÇÃO DO PRODUTO

### 12.1 MVP - Prioridade principal

- Login com RA e senha;
- Área inicial do estudante;
- Consulta de cursos;
- Agenda individual;
- Integração inicial com API e banco de dados;
- QR Code individual;
- Registro e histórico de presença;
- Persistência local básica;
- Perfil básico do estudante;
- Funcionamento em Android.

### 12.2 Segunda etapa

- Card digital;
- Certificados;
- Notificações de novos cursos e palestras;
- Teste de perfil;
- Mensagens e canal de dúvidas;
- Lista de restrição administrativa;
- Resultados detalhados no perfil;
- Organização de certificados por instituição.

### 12.3 Evoluções futuras

- Chatbot ou assistente de IA;
- Versão para iOS;
- Gamificação;
- Integração com calendários externos;
- Carteira digital de certificados;
- Recomendações de cursos;
- Funcionamento offline ampliado;
- Dashboard de evolução;
- Lista de espera;
- Avaliação de cursos;
- Integração ampliada com o site institucional.

---

## 13. TECNOLOGIAS PREVISTAS

A stack definitiva deverá ser aprovada pelos professores e atualizada conforme as decisões do grupo.

### Aplicativo Android

- Java;
- Android Studio;
- Android SDK;
- XML e ConstraintLayout;
- Activities, Fragments e Intents;
- RecyclerView;
- SQLite ou Room;
- Retrofit ou biblioteca equivalente;
- JSON;
- ZXing ou Google ML Kit para QR Code.

### Backend e API

Uma opção deverá ser definida pelo grupo:

- Node.js com Express;
- Java com Spring Boot;
- .NET Web API;
- Tecnologia equivalente aprovada.

### Banco de dados

- Estrutura disponibilizada pela Próxima Etapa;
- MySQL, PostgreSQL ou SQLite em ambiente de desenvolvimento, quando autorizado.

### Módulo web

- HTML5;
- CSS3;
- JavaScript ou TypeScript;
- React, Vue ou tecnologia equivalente.

### Ferramentas

- Git e GitHub;
- GitHub Projects;
- Figma;
- Swagger ou OpenAPI;
- Postman ou Insomnia;
- Excel;
- Python, Pandas e Matplotlib para análise de dados.

---

## 14. ARQUITETURA GERAL

Fluxo principal:

Aplicativo Android -> API REST -> Banco de dados
Módulo web administrativo -> API REST -> Banco de dados

O aplicativo e o módulo web não deverão acessar diretamente o banco de dados.

Camadas sugeridas:

- Apresentação: telas mobile e web;
- Aplicação: casos de uso e serviços;
- Domínio: entidades e regras de negócio;
- Infraestrutura: banco, API, arquivos e serviços externos;
- Segurança: autenticação, autorização e auditoria.

---

## 15. ENTIDADES PRINCIPAIS

- Aluno;
- Curso;
- Palestra;
- Atividade;
- Inscrição;
- Encontro;
- Presença;
- QRCodeAluno;
- Certificado;
- Instituição;
- Card;
- TestePerfil;
- Pergunta;
- Alternativa;
- ResultadoPerfil;
- Mensagem;
- Notificação;
- RestricaoAcesso;
- UsuarioAdministrativo.

---

## 16. ESTRUTURA RECOMENDADA DE PASTAS E ARQUIVOS

A estrutura abaixo organiza as entregas acadêmicas e técnicas. As pastas deverão ser criadas conforme os módulos forem iniciados.

```text
Projeto4/
|
|-- README.md
|-- LICENSE
|-- .gitignore
|-- .env.example
|-- CONTRIBUTING.md
|
|-- mobile/
|   |-- README.md
|   |-- android/
|       |-- app/
|       |-- gradle/
|       |-- build.gradle
|       |-- settings.gradle
|       |-- gradle.properties
|
|-- backend/
|   |-- README.md
|   |-- src/
|   |   |-- config/
|   |   |-- controllers/
|   |   |-- domain/
|   |   |-- dtos/
|   |   |-- middlewares/
|   |   |-- repositories/
|   |   |-- routes/
|   |   |-- services/
|   |   |-- utils/
|   |   |-- app/
|   |-- tests/
|   |-- .env.example
|
|-- web/
|   |-- README.md
|   |-- public/
|   |-- src/
|   |   |-- assets/
|   |   |-- components/
|   |   |-- pages/
|   |   |-- services/
|   |   |-- styles/
|   |   |-- utils/
|   |-- .env.example
|
|-- database/
|   |-- README.md
|   |-- schema/
|   |   |-- 001_create_tables.sql
|   |-- migrations/
|   |-- seeds/
|   |   |-- dados_ficticios.sql
|   |-- diagrams/
|       |-- modelo_entidade_relacionamento.png
|
|-- docs/
|   |-- README.md
|   |-- 01-requisitos/
|   |   |-- requisitos-funcionais.md
|   |   |-- requisitos-nao-funcionais.md
|   |   |-- regras-de-negocio.md
|   |   |-- criterios-de-aceitacao.md
|   |-- 02-arquitetura/
|   |   |-- arquitetura.md
|   |   |-- diagrama-de-classes.png
|   |   |-- diagrama-de-componentes.png
|   |-- 03-ux-ui/
|   |   |-- identidade-visual.md
|   |   |-- wireframes/
|   |   |-- prototipos/
|   |-- 04-api/
|   |   |-- openapi.yaml
|   |   |-- colecao-postman.json
|   |-- 05-testes/
|   |   |-- plano-de-testes.md
|   |   |-- evidencias/
|   |-- 06-lgpd-seguranca/
|   |   |-- privacidade-e-lgpd.md
|   |   |-- analise-de-riscos.md
|   |-- 07-business-english/
|   |   |-- README.md
|   |   |-- project-abstract.md
|   |   |-- technical-glossary.md
|   |   |-- product-documentation.md
|   |-- 08-analise-de-dados/
|   |   |-- README.md
|   |   |-- planilhas/
|   |   |-- graficos/
|   |   |-- relatorio.md
|   |-- 09-poo-estrutura-de-dados/
|   |   |-- README.md
|   |   |-- diagrama-de-classes.png
|   |   |-- estruturas-utilizadas.md
|   |-- 10-apresentacao/
|       |-- pitch.md
|       |-- demonstracao.md
|
|-- DOCUMENTOS/
|   |-- README.md
|   |-- banner/
|   |-- relatorio-final/
|   |-- documento-extensao/
|   |-- apresentacao/
|
|-- deploy/
|   |-- README.md
|   |-- docker/
|   |-- scripts/
|
|-- releases/
    |-- README.md
    |-- apk/
    |-- aab/
```

Observações sobre a estrutura:

- `README.md`: apresentação principal e instruções gerais;
- `mobile/`: código do aplicativo Android;
- `backend/`: API REST e regras do servidor;
- `web/`: módulo administrativo;
- `database/`: scripts, migrações, dados fictícios e diagramas do banco;
- `docs/`: documentação técnica e acadêmica organizada por assunto;
- `DOCUMENTOS/`: entregáveis formais, incluindo banner e relatório final;
- `deploy/`: configurações de implantação;
- `releases/`: APK e AAB destinados às entregas;
- Arquivos vazios poderão conter `.gitkeep` para que o GitHub preserve a pasta;
- Dados pessoais reais, senhas e arquivos `.env` não deverão ser enviados.

---

## 17. COMO EXECUTAR O PROJETO

As instruções serão completadas após a definição da stack e a criação dos módulos.

### Clonar o repositório

```bash
git clone https://github.com/2026-2-NCC3/Projeto1.git
cd Projeto1
```

### Aplicativo Android

1. Instale o Android Studio e o JDK exigido pelo projeto;
2. Abra a pasta `mobile/android`;
3. Aguarde a sincronização do Gradle;
4. Configure um emulador ou conecte um dispositivo Android;
5. Execute o aplicativo.

### Backend

1. Acesse a pasta `backend`;
2. Leia o arquivo `backend/README.md`;
3. Copie `.env.example` para `.env` apenas no computador local;
4. Configure o banco de dados;
5. Instale as dependências da tecnologia escolhida;
6. Execute a API.

### Módulo web

1. Acesse a pasta `web`;
2. Leia o arquivo `web/README.md`;
3. Configure as variáveis locais;
4. Instale as dependências;
5. Execute o módulo web.

---

## 18. VARIÁVEIS DE AMBIENTE

Exemplo de `.env.example`:

```env
DATABASE_HOST=
DATABASE_PORT=
DATABASE_NAME=
DATABASE_USER=
DATABASE_PASSWORD=
API_PORT=
TOKEN_SECRET=
```

Nunca preencher senhas reais no `.env.example`.

---

## 19. SEGURANÇA, PRIVACIDADE E LGPD

- Cada estudante visualizará apenas os próprios dados;
- Credenciais não serão armazenadas em texto simples;
- O QR Code não deverá exibir informações pessoais desnecessárias;
- O acesso administrativo deverá ser protegido;
- Restrições de estudantes serão tratadas como informação confidencial;
- Logs não deverão registrar senhas ou dados sensíveis;
- Dados pessoais reais não serão publicados no GitHub;
- Arquivos `.env`, tokens e chaves permanecerão fora do versionamento;
- Dados fictícios ou anonimizados serão usados em testes e apresentações;
- O sistema deverá observar coleta mínima, finalidade e controle de acesso.

---

## 20. ANÁLISE DESCRITIVA DE DADOS

O projeto deverá incluir:

- Nome, tipo e subtipo das variáveis;
- Medidas de tendência central;
- Percentil 95, quando aplicável;
- Gráficos adequados;
- Coeficiente de variação;
- Análise de dispersão;
- Identificação de outliers;
- Box Plot;
- Comparação com e sem valores atípicos;
- Probabilidades relacionadas a frequência, ausência ou conclusão, quando aplicável.

---

## 21. PROGRAMAÇÃO ORIENTADA A OBJETOS E ESTRUTURAS DE DADOS

O projeto deverá aplicar:

- Classes e objetos;
- Encapsulamento;
- Abstração;
- Herança e polimorfismo, quando aplicáveis;
- Separação de responsabilidades;
- Coleções;
- Busca;
- Ordenação;
- Organização eficiente dos dados.

Documentação específica:

`docs/09-poo-estrutura-de-dados/README.md`

---

## 22. TESTES

Deverão ser realizados testes de:

- Login e logout;
- Restrição por estudante autenticado;
- Conta suspensa ou com restrição;
- Comunicação com a API;
- Consulta de cursos e agenda;
- Geração e leitura do QR Code;
- Registro de presença;
- Prevenção de presença duplicada;
- Persistência local;
- Perda de conexão e sincronização;
- Certificados;
- Notificações;
- Segurança;
- Acessibilidade;
- Usabilidade;
- Integração entre os módulos;
- Desempenho e tamanho do aplicativo.

---

## 23. PLANEJAMENTO

### Etapa 1 - Descoberta e planejamento

- Entendimento da organização;
- Definição dos usuários;
- Análise da base de dados;
- Definição do MVP;
- Backlog e critérios de aceitação.

### Etapa 2 - Modelagem

- Arquitetura;
- Diagrama de classes;
- Modelo do banco;
- Contratos da API;
- Fluxos, wireframes e protótipos.

### Etapa 3 - Fundação técnica

- Autenticação;
- Banco de dados;
- API inicial;
- Comunicação aplicativo-servidor.

### Etapa 4 - Mobile inicial

- Login;
- Cursos;
- Agenda;
- Primeira entrega funcional.

### Etapa 5 - Integração e persistência

- Consumo da API;
- JSON;
- Persistência local;
- Tratamento de conexão;
- Sincronização.

### Etapa 6 - Presença e notificações

- QR Code individual;
- Check-in;
- Histórico;
- Notificações.

### Etapa 7 - Recursos complementares

- Card;
- Certificados;
- Teste de perfil;
- Mensagens;
- Restrição administrativa;
- Resultados no perfil;
- Ajustes de experiência e identidade visual.

### Etapa 8 - Finalização

- Testes;
- Deploy;
- Documentação;
- APK ou AAB;
- Relatório;
- Banner;
- Pitch e apresentação.

---

## 24. CRONOGRAMA ACADÊMICO

- Formação do grupo: 14 de agosto de 2026;
- Entrega 1: 25 de setembro de 2026, até 23:59:56;
- Envio do banner para aprovação: até 2 de novembro de 2026;
- Entrega 2: 6 de novembro de 2026, até 23:59:56;
- Avaliação entre pares: 7 e 8 de novembro de 2026;
- Apresentações: 17, 18, 19, 23 e 24 de novembro de 2026.

---

## 25. ENTREGÁVEIS

- Repositório versionado;
- Histórico de commits;
- Código-fonte;
- Aplicativo Android;
- APK ou AAB;
- Backend e API;
- Banco de dados e scripts;
- Módulo web;
- Documentação da API;
- Diagramas;
- Evidências de testes;
- Análise descritiva de dados;
- Documentação em inglês;
- Ambiente de demonstração;
- Relatório final;
- Documento de extensão;
- Banner;
- Pitch e demonstração.

---

## 26. INTEGRANTES

### Guilherme Belcastro Medeiros

- RA: 25028093
- GitHub: https://github.com/GBM011

### Julia Valério da Silva

- RA: 25028281
- GitHub: https://github.com/JuliaSilva1207

### Arthur Henrique dos Anjos Ferreira

- RA: 25028270
- GitHub: https://github.com/Tuzimx

### William Takuya Takeuchi Takaki

- RA: 25028225
- GitHub: preencher o endereço completo

---

## 27. INSTITUIÇÃO

Fundação Escola de Comércio Álvares Penteado - FECAP

- Curso: Ciência da Computação;
- Semestre: 3º semestre;
- Período: Noturno;
- Ano: 2026;

---

## 28. PROJECT ABSTRACT

The Próxima Etapa Student App is an interdisciplinary academic project developed by third-semester Computer Science students at FECAP. The project aims to create an integrated digital solution for students assisted by Próxima Etapa, a nonprofit organization focused on expanding access to higher education and professional guidance for public-school students.

The proposed solution consists of an Android mobile application, a REST API, a database, and a web-based administrative module. Through the application, students will be able to access information about courses and lectures, view their individual schedules, use a personalized QR code for identification and attendance, check their participation history, access certificates, view educational profile results, and receive messages and notifications.

Each student will have an individual account and will only be able to access authorized personal information. The API will provide secure communication between the application, the administrative module, and the database. The project will also apply object-oriented programming, data structures, descriptive data analysis, accessibility, information security, and privacy principles. During development and demonstrations, the team will prioritize fictional, anonymized, or previously authorized data.

---

## 29. PRODUCT OVERVIEW

### Target audience

Students enrolled in courses, lectures and activities offered by Próxima Etapa.

### Problem

Students may need to access course information, schedules, attendance records, certificates and messages through different channels.

### Value proposition

The application provides a centralized, accessible and secure digital experience for managing the student's educational journey.

### Main features

- Authentication using student registration number and password;
- Courses and lectures;
- Individual schedule;
- Personalized QR Code;
- Attendance history;
- Digital student card;
- Certificates;
- Educational profile results;
- Messages and notifications;
- Student profile;
- Data synchronization.

---

## 30. TECHNICAL GLOSSARY

- API: Application Programming Interface used for communication between systems.
- REST: Architectural style used to design web services.
- Authentication: Process used to verify the identity of a user.
- Authorization: Process used to define which resources a user may access.
- QR Code: Two-dimensional code used for student identification and attendance.
- Check-in: Process of confirming participation in an activity.
- Database: Structured collection of information used by the system.
- JSON: Data format used to exchange information between systems.
- Endpoint: Address provided by an API for a specific operation.
- Token: Information used to represent an authenticated session.
- Synchronization: Process of updating local and remote information.
- Accessibility: Practices that make the application usable by more people.
- Privacy: Protection and responsible use of personal information.
- Certificate: Document released after the student meets defined requirements.
- Blacklist or restriction list: Administrative access-control mechanism. In the product interface, prefer respectful terms such as restriction list or suspended access.

---

## 31. STATUS DO PROJETO

Legenda:

- [ ] Não iniciado
- [~] Em desenvolvimento
- [x] Concluído

Situação inicial:

- [~] Levantamento de requisitos;
- [~] Documentação inicial;
- [~] Organização do repositório;
- [ ] Definição final da stack;
- [ ] Protótipo no Figma;
- [ ] Modelagem do banco;
- [ ] Diagrama de classes;
- [ ] API REST;
- [ ] Aplicativo Android;
- [ ] Módulo web;
- [ ] QR Code individual;
- [ ] Histórico de presença;
- [ ] Notificações;
- [ ] Certificados;
- [ ] Teste de perfil;
- [ ] Testes;
- [ ] Deploy;
- [ ] Documentação final.

---

## 32. CONTRIBUIÇÃO E VERSIONAMENTO

Cada integrante deverá:

1. Trabalhar em tarefa registrada no GitHub Project;
2. Criar branch quando necessário;
3. Fazer commits claros e frequentes;
4. Não publicar credenciais ou dados pessoais;
5. Testar antes de integrar;
6. Abrir Pull Request quando a branch principal estiver protegida;
7. Participar das revisões do grupo;
8. Atualizar a documentação da funcionalidade criada.

Exemplos de commits:

```text
docs: cria README inicial do projeto
feat: adiciona tela de cursos
feat: implementa login com RA
feat: cria QR Code individual do aluno
fix: impede registro duplicado de presença
test: adiciona testes da API de cursos
refactor: reorganiza camada de serviços
```

---

## 33. CUIDADOS COM O REPOSITÓRIO

Não publicar:

- Arquivos `.env`;
- Senhas;
- Tokens;
- Chaves de API;
- Chaves privadas;
- Credenciais do banco;
- Dados pessoais reais não autorizados;
- Bancos com informações pessoais reais;
- Informações confidenciais sobre suspensão ou restrição;
- Arquivos pesados desnecessários.

---

## 34. LICENÇA

Este projeto foi desenvolvido para fins acadêmicos no curso de Ciência da Computação da FECAP.

A utilização, distribuição ou modificação deverá respeitar as orientações da FECAP, da Próxima Etapa e dos integrantes do grupo. Quando a licença definitiva for definida, esta seção e o arquivo `LICENSE` serão atualizados.

---

## 35. AGRADECIMENTOS

A equipe agradece:

- À FECAP;
- Aos professores envolvidos no Projeto Interdisciplinar;
- À Próxima Etapa;
- Aos integrantes da equipe;
- Às pessoas que contribuírem com orientação, testes e validação.

---

Desenvolvido para o Projeto Interdisciplinar do 3º semestre de Ciência da Computação da FECAP.

São Paulo, 2026.
