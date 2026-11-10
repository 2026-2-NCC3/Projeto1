# Entrega 1 - Programação Orientada a Objetos e Estrutura de Dados

## Projeto Próxima Etapa

Esta entrega apresenta a modelagem e a implementação inicial da área do
aluno do aplicativo Próxima Etapa.

O sistema foi desenvolvido em Java no Android Studio e possui classes de
domínio, operações CRUD, estruturas de dados, regras de negócio e testes
unitários.

## Funcionalidades implementadas

- Cadastro, consulta, edição e remoção de alunos;
- Busca de aluno por ID, RA e nome;
- Ordenação alfabética dos alunos;
- Cadastro, consulta, edição e remoção de cursos;
- Busca de curso por ID e título;
- Ordenação alfabética dos cursos;
- Controle de vagas disponíveis;
- Inscrição de alunos em cursos;
- Cancelamento de inscrições;
- Devolução de vaga após cancelamento;
- Impedimento de inscrição duplicada;
- Bloqueio de alunos após duas faltas;
- Impedimento de inscrição para alunos bloqueados;
- Código individual para representação do QR Code;
- Registro de presença em atividades;
- Impedimento de presença duplicada;
- Validação lógica do QR Code;
- Listagem de cursos obtidos por API;
- Tela de detalhes dos cursos.

## Classes de domínio

As principais classes implementadas foram:

- `Aluno`;
- `Curso`;
- `Atividade`;
- `Inscricao`;
- `Presenca`;
- `StatusAluno`;
- `TipoAtividade`.

### Aluno

Representa o aluno cadastrado no aplicativo. Armazena informações como
ID, RA, nome, e-mail, faculdade, curso, situação, QR Code, resultado DISC,
progresso e quantidade de faltas.

### Curso

Representa um curso disponível. Armazena título, descrição, carga horária,
local, universidade, quantidade de vagas, pontos, certificado, categoria e
situação.

### Atividade

Representa uma aula, palestra, oficina ou evento associado a um curso.

### Inscricao

Representa o vínculo entre um aluno e um curso. Armazena a data, o status,
os pontos recebidos e a data da presença.

### Presenca

Representa o registro de entrada de um aluno em uma atividade, utilizando
um código de validação relacionado ao QR Code.

## Enumerações

### StatusAluno

Possui os valores:

- `ATIVO`;
- `SUSPENSO`;
- `BLOQUEADO`;
- `INATIVO`.

### TipoAtividade

Possui os valores:

- `AULA`;
- `PALESTRA`;
- `OFICINA`;
- `EVENTO`.

## Conceitos de Programação Orientada a Objetos

### Encapsulamento

Os atributos das classes foram declarados como privados. O acesso e a
alteração dos dados ocorrem por meio de métodos públicos, como getters,
setters e métodos de negócio.

### Abstração

Cada classe representa uma entidade do domínio do projeto. Por exemplo,
`Aluno` representa o estudante e `Curso` representa uma formação
disponibilizada pela plataforma.

### Associação

As classes possuem relacionamentos entre si:

- uma inscrição associa um aluno a um curso;
- uma atividade pertence a um curso;
- uma presença associa um aluno a uma atividade.

### Separação de responsabilidades

O projeto foi organizado em três pacotes principais:

- `model`: contém as classes de domínio;
- `repository`: contém as operações de armazenamento e CRUD;
- `service`: contém as regras de negócio.

## Estruturas de dados

Foi utilizado `ArrayList` para armazenar objetos em memória.

A estrutura permite:

- adicionar registros;
- listar registros;
- localizar objetos;
- editar objetos;
- remover objetos;
- ordenar os dados.

A ordenação é realizada com `Comparator` e as buscas percorrem as coleções
para localizar os objetos conforme seus identificadores, nomes ou RA.

## Repositories

Foram implementados:

- `AlunoRepository`;
- `CursoRepository`;
- `InscricaoRepository`;
- `PresencaRepository`.

Os repositories são responsáveis pelas operações de cadastro, listagem,
busca, edição, remoção e organização dos dados.

## Services

Foram implementados:

- `InscricaoService`;
- `PresencaService`.

O `InscricaoService` verifica:

- se o aluno pode acessar o sistema;
- se o curso está ativo;
- se existem vagas disponíveis;
- se o aluno já possui inscrição no curso;
- se uma vaga deve ser ocupada ou liberada.

O `PresencaService` verifica:

- se o aluno está ativo;
- se o QR Code corresponde ao aluno;
- se a presença já foi registrada;
- se a entrada pode ser autorizada.

## Regra de blacklist

O aluno é bloqueado depois de duas faltas.

Um aluno bloqueado não pode:

- realizar uma nova inscrição;
- validar seu QR Code;
- registrar presença em uma atividade.

## QR Code

Cada aluno possui um código individual no formato:

`ALUNO-RA_DO_ALUNO`

Nesta primeira entrega foi implementada a validação lógica do código. A
leitura real pela câmera será implementada em uma etapa futura.

## Integração com API

O aplicativo possui uma integração para consultar cursos na API do projeto.

Os cursos são mostrados em um `RecyclerView` e podem ser selecionados para
abrir uma tela de detalhes.

A integração com a API não substitui os repositories. Os repositories foram
implementados para demonstrar os conceitos de POO, CRUD e estruturas de
dados exigidos pela disciplina.

## Testes unitários

Foram executados 42 testes unitários com sucesso.

Distribuição dos testes:

- 9 testes do CRUD de alunos;
- 11 testes do CRUD e controle de vagas dos cursos;
- 10 testes de inscrição;
- 11 testes de presença e QR Code;
- 1 teste padrão criado pelo Android Studio.

Os testes validam:

- cadastro;
- busca;
- edição;
- remoção;
- ordenação;
- bloqueio;
- vagas;
- inscrição;
- cancelamento;
- duplicidade;
- QR Code;
- presença.

Resultado obtido:

`42 tests passed`

## Diagrama de classes

Esta pasta contém:

- `Diagrama de Classes - Proxima Etapa.drawio`;
- `Diagrama de Classes - Proxima Etapa.jpg`.

O arquivo `.drawio` permite editar o diagrama e o arquivo `.jpg` permite
visualizá-lo diretamente.

## Localização do código

O código da aplicação está localizado no projeto Android:

`frontend/app/src/main/java/com/example/app_proxima_etapa/`

As classes de teste estão localizadas em:

`frontend/app/src/test/java/com/example/app_proxima_etapa/`

## Como executar os testes

1. Abrir o projeto no Android Studio;
2. Aguardar a sincronização do Gradle;
3. Localizar o pacote com a indicação `(test)`;
4. Clicar com o botão direito no pacote;
5. Selecionar `Run Tests`;
6. Conferir o resultado no painel `Test Results`.

Resultado esperado:

`42 tests passed`

## Como executar o aplicativo

1. Abrir o projeto no Android Studio;
2. Selecionar um emulador ou dispositivo Android;
3. Executar o módulo `app`;
4. Aguardar o carregamento da lista de cursos;
5. Selecionar o botão `Detalhes` para visualizar um curso.