# API de Cursos — Próxima Etapa

Esta API foi desenvolvida para a primeira entrega do Projeto Interdisciplinar do 3º semestre de Ciência da Computação da FECAP.

Nesta etapa, a API permite consultar e administrar os cursos disponibilizados no aplicativo Próxima Etapa. Os dados são armazenados em um banco PostgreSQL hospedado no Supabase.

## Funcionalidades atuais

- verificar a conexão com o banco;
- listar os cursos por data;
- consultar um curso pelo identificador;
- cadastrar cursos;
- editar cursos;
- excluir cursos;
- proteger as operações administrativas com uma chave.

## Tecnologias

- Node.js;
- Express;
- PostgreSQL;
- Supabase;
- biblioteca `pg`;
- biblioteca `dotenv`.

## Configuração local

Instale as dependências:

```bash
npm install
```

Crie um arquivo chamado `.env` dentro da pasta `backend`, usando o `.env.example` como referência:

```env
DATABASE_URL=CONEXAO_DO_SUPABASE
ADMIN_API_KEY=CHAVE_ADMINISTRATIVA
PORT=3000
```

O arquivo `.env` contém informações privadas e não deve ser publicado.

## Execução

Inicie a API:

```bash
npm start
```

Por padrão, ela ficará disponível em:

```text
http://localhost:3000
```

Para verificar a sintaxe do servidor:

```bash
npm test
```

## Rotas disponíveis

| Método | Rota | Função | Chave administrativa |
|---|---|---|---|
| GET | `/` | Verifica a conexão com o banco | Não |
| GET | `/courses` | Lista os cursos por data | Não |
| GET | `/courses/:id` | Consulta um curso pelo UUID | Não |
| POST | `/courses` | Cadastra um curso | Sim |
| PUT | `/courses/:id` | Atualiza um curso | Sim |
| DELETE | `/courses/:id` | Exclui um curso | Sim |

As rotas administrativas exigem o cabeçalho:

```text
x-admin-key: SUA_CHAVE_ADMINISTRATIVA
```

## Exemplo de consulta

Requisição:

```http
GET /courses
```

Resposta:

```json
[
  {
    "id": "a3441667-c7b7-4ab1-b7d6-a3de93edfe61",
    "title": "Introdução à Programação",
    "description": "Curso introdutório para estudantes",
    "location": "São Paulo - SP",
    "course_date": "2026-10-05T17:00:00.000Z",
    "category": "tecnologia",
    "has_certificate": true
  }
]
```

## Exemplo de cadastro

Requisição:

```http
POST /courses
Content-Type: application/json
x-admin-key: SUA_CHAVE_ADMINISTRATIVA
```

Corpo da requisição:

```json
{
  "title": "Introdução à Programação",
  "description": "Curso introdutório para estudantes",
  "location": "São Paulo - SP",
  "course_date": "2026-10-05T14:00:00-03:00",
  "category": "tecnologia"
}
```

Quando o cadastro é realizado, a API responde com o curso criado e o código HTTP `201`.

## Códigos de resposta

- `200`: operação concluída;
- `201`: curso cadastrado;
- `400`: dados ou UUID inválidos;
- `401`: chave administrativa inválida;
- `404`: curso não encontrado;
- `500`: falha interna ou falha de comunicação com o banco.

## Banco de dados

Os arquivos do banco estão separados em:

- `../database/schema/001_create_courses.sql`: estrutura da tabela;
- `../database/seeds/001_courses_ficticios.sql`: dados fictícios usados nos testes.

Nenhuma senha ou informação pessoal real deve ser armazenada nesses arquivos.