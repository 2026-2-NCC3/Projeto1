Próxima Etapa: Programação Orientada a Objetos e Estrutura de Dados

ENTREGA 2

Este documento apresenta os conceitos de Programação Orientada a Objetos (POO), as estruturas de dados e as principais decisões de implementação identificadas no código Java do aplicativo Próxima Etapa.

Código-fonte da Entrega 2:
https://github.com/2026-2-NCC3/Projeto1/tree/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa

ORGANIZAÇÃO DO CÓDIGO

model: representação das entidades do domínio e de seus comportamentos.
repository: cadastro, consulta, edição e remoção de objetos em coleções em memória.
service: aplicação das regras de negócio relacionadas a inscrições e presenças.
api: contratos de comunicação e objetos utilizados nas requisições e respostas da API.
storage: persistência local, cache e sincronização de presenças.
Pacotes de telas: apresentação das informações e interação com o usuário, como cursos, agenda, perfil e QR Code.

Os repositórios de domínio armazenam objetos em memória. Eles não substituem o banco SQLite nem o banco de dados acessado pelo backend.

A presença de uma regra nos serviços de domínio não garante que todas as telas ou rotas da API utilizem essa mesma regra. Por isso, a análise considera separadamente o modelo de domínio, a persistência local, a comunicação com a API e os fluxos utilizados pelas telas Android.

CLASSES PRINCIPAIS

Modelos

- Aluno: representa o participante cadastrado no sistema.
- Curso: reúne identificação, título, descrição, carga horária, vagas, categoria e situação do curso.
- Atividade: representa uma atividade e mantém uma referência ao curso relacionado.
- Inscricao: relaciona um aluno a um curso e representa a situação da inscrição.
- Presenca: representa a participação de um aluno em uma atividade.
- StatusAluno: enumeração que representa os estados possíveis de um aluno.
- TipoAtividade: enumeração que representa os tipos de atividade reconhecidos pelo modelo.

Repositórios

- AlunoRepository: organiza as operações de cadastro, consulta, alteração, remoção, busca e ordenação de alunos em memória.
- CursoRepository: organiza as operações de cadastro, consulta, alteração, remoção, busca e ordenação de cursos em memória.
- InscricaoRepository: organiza as inscrições e permite consultar inscrições por aluno ou por curso.
- PresencaRepository: organiza os registros de presença e permite consultar presenças relacionadas aos alunos e às atividades.

Serviços

- InscricaoService: concentra as regras de negócio relacionadas ao processo de inscrição.
- PresencaService: concentra as regras de negócio relacionadas ao registro de presença.

Persistência e sincronização

- PresencasSQLite: oferece armazenamento local para os registros de presença.
- CursosCache: mantém localmente informações relacionadas aos cursos.
- SincronizadorPresencas: envia à API os registros de presença que ainda estão pendentes de sincronização.

DIAGRAMA DE CLASSES

O diagrama apresenta as principais classes da Entrega 2, incluindo as entidades do domínio, as enumerações, os repositórios em memória, os serviços e seus relacionamentos.

Arquivo do diagrama:
docs/diagrama-classes.png

O diagrama considera as classes implementadas nos pacotes model, repository e service. Os relacionamentos e as multiplicidades devem corresponder às referências, às coleções e às regras encontradas no código-fonte.

PRINCÍPIOS DE POO APLICADOS

Encapsulamento

As classes de domínio mantêm seus atributos privados e disponibilizam métodos para leitura e alteração controlada dos dados.

A classe Curso, por exemplo, possui atributos privados e utiliza métodos de acesso e alteração. Métodos como setCargaHoraria() e setVagasDisponiveis() validam os valores recebidos antes de modificar o estado do objeto.

Os comportamentos ocuparVaga() e liberarVaga() concentram a atualização das vagas dentro da própria classe Curso. Dessa forma, a regra não precisa ser repetida em diferentes partes do programa.

Abstração

As classes representam conceitos relevantes do domínio do projeto:

- Aluno representa o participante;
- Curso representa uma formação ou curso disponível;
- Atividade representa uma ação vinculada a um curso;
- Inscricao representa o vínculo entre aluno e curso;
- Presenca representa a participação do aluno em uma atividade.

Separação de responsabilidades

O projeto distribui as responsabilidades entre diferentes camadas:

- as classes de model representam os objetos do domínio;
- as classes de repository organizam e manipulam coleções de objetos;
- as classes de service aplicam as regras de negócio;
- as classes de api realizam a comunicação com o backend;
- as classes de storage realizam o armazenamento local e a sincronização;
- as Activities e Fragments apresentam as informações ao usuário.

Associações entre objetos

A classe Atividade mantém uma referência para Curso. Essa associação indica a qual curso uma atividade pertence.

A classe Inscricao relaciona um objeto Aluno a um objeto Curso. Essa relação permite identificar qual aluno está inscrito em determinado curso.

A classe Presenca representa a participação de um aluno em uma atividade e mantém as referências definidas no código.

Herança e polimorfismo

As telas Android herdam características e comportamentos de classes do framework Android, como AppCompatActivity.

No pacote de recursos, RecursoActivity é uma classe abstrata que reúne comportamentos compartilhados por diferentes telas.

Classes como CardAlunoActivity e CertificadosActivity especializam RecursoActivity, reutilizando seu comportamento e implementando métodos específicos, como titulo() e carregar(). Isso demonstra sobrescrita de métodos e polimorfismo.

A classe Curso também sobrescreve o método toString(), fornecendo uma representação textual própria.

Implementação de interface

A classe Curso implementa Serializable. Essa interface permite que instâncias sejam serializadas, inclusive para transporte entre componentes Android quando utilizadas em objetos como Intent ou Bundle.

ESTRUTURAS DE DADOS

List e ArrayList

Os repositórios utilizam a interface List, normalmente com a implementação ArrayList.

Exemplo:

List<Curso> cursos = new ArrayList<>();

Essa estrutura permite manter uma sequência de objetos, adicionar elementos, percorrer registros, localizar elementos, remover objetos e criar novas listas com resultados filtrados ou ordenados.

A escolha do ArrayList favorece simplicidade e acesso sequencial para conjuntos pequenos de dados.

Complexidade das buscas

As buscas por identificador percorrem os elementos da lista até localizar o objeto desejado. Esse tipo de busca possui custo linear O(n) no pior caso.

Busca de cursos

CursoRepository.buscarPorId() percorre a coleção de cursos e compara o identificador de cada objeto.

CursoRepository.buscarPorTitulo() procura cursos cujos títulos contenham o texto pesquisado, desconsiderando diferenças entre letras maiúsculas e minúsculas.

Busca de alunos

AlunoRepository.buscarPorNome() filtra os alunos com base no nome informado.

Busca de inscrições

InscricaoRepository.buscarPorAluno() e InscricaoRepository.buscarPorCurso() utilizam os relacionamentos de Inscricao para selecionar os registros correspondentes.

ORDENAÇÃO

CursoRepository.listarOrdenadosPorTitulo() cria uma nova lista e utiliza Collections.sort() com um Comparator para ordenar os cursos pelo título.

AlunoRepository.listarOrdenadosPorNome() utiliza Comparator.comparing() para ordenar os alunos pelo nome, sem distinguir letras maiúsculas de minúsculas.

Os dois métodos ordenam uma nova lista, preservando a ordem da coleção interna do repositório. A cópia é superficial: a lista é nova, mas os objetos continuam sendo as mesmas instâncias da coleção original.

PROTEÇÃO DAS COLEÇÕES

Métodos como listarTodos() retornam uma nova instância de ArrayList.

Exemplo:

return new ArrayList<>(cursos);

Essa decisão impede que a adição ou remoção de elementos na lista retornada modifique diretamente a estrutura interna do repositório. Entretanto, os objetos da lista continuam compartilhados.

REGRAS DE NEGÓCIO

Inscrição

InscricaoService.realizarInscricao() concentra verificações necessárias antes de cadastrar uma inscrição, como existência do aluno e do curso, situação ativa, disponibilidade de vagas e inscrição anterior.

Quando a inscrição é permitida, o serviço registra a inscrição, ocupa uma vaga no objeto Curso e confirma a operação conforme as regras implementadas.

Presença

PresencaService concentra regras relacionadas ao registro de presença, como validar aluno e atividade, impedir registros duplicados, criar o objeto Presenca e solicitar seu armazenamento.

PERSISTÊNCIA E INTEGRAÇÃO

O aplicativo contém comunicação com uma API REST e armazenamento local separado das coleções em memória utilizadas pelos repositórios.

PresencasSQLite armazena localmente informações relacionadas às presenças.

CursosCache mantém dados de cursos disponíveis localmente.

SincronizadorPresencas trata o envio de registros pendentes para a API.

Algumas telas também realizam chamadas diretas aos contratos da API. Portanto, a avaliação da integração deve considerar o modelo de domínio e os fluxos efetivamente utilizados pelas telas.

LIMITES DA INTEGRAÇÃO

As classes de model, repository e service representam o modelo de domínio desenvolvido para a Entrega 2.

Essas classes possuem relacionamentos entre si e são verificadas pelos testes do projeto. Entretanto, parte das telas Android utiliza diretamente a API REST ou o armazenamento local.

Por esse motivo, nem todas as Activities e Fragments dependem necessariamente dos repositórios em memória ou dos serviços de domínio.

TESTES PRESENTES NO PROJETO

A pasta de testes contém classes como:

- AlunoRepositoryTest;
- CursoRepositoryTest;
- InscricaoServiceTest;
- PresencaServiceTest.

Testes unitários da Entrega 2:
https://github.com/2026-2-NCC3/Projeto1/tree/main/src/Entrega%202/frontend/app/src/test/java/com/example/app_proxima_etapa

A existência dos arquivos de teste não comprova que todos foram executados com sucesso. Os resultados devem ser verificados no Gradle ou no painel de testes do Android Studio.

COMO VALIDAR O PROJETO

1. Clone o repositório.
2. Abra a pasta src/Entrega 2/frontend no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Configure um emulador ou dispositivo Android.
5. Execute o módulo app.
6. Para executar os testes unitários, abra o terminal na pasta src/Entrega 2/frontend e utilize:

./gradlew test

No Windows, caso o comando anterior não funcione, utilize:

.\gradlew.bat test

Os testes instrumentados exigem um emulador ou dispositivo Android conectado.

Após a execução, os resultados podem ser consultados no painel de testes do Android Studio ou em:

app/build/reports/tests

REFERÊNCIAS DE IMPLEMENTAÇÃO

Curso:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/model/Curso.java

Atividade:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/model/Atividade.java

CursoRepository:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/repository/CursoRepository.java

AlunoRepository:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/repository/AlunoRepository.java

InscricaoRepository:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/repository/InscricaoRepository.java

InscricaoService:
https://github.com/2026-2-NCC3/Projeto1/blob/main/src/Entrega%202/frontend/app/src/main/java/com/example/app_proxima_etapa/service/InscricaoService.java
