require('dotenv').config();
const express = require('express');
const { Pool } = require('pg');

const app = express();
app.use(express.json());
function verificarChave(req, res, next) {
  const chaveRecebida = req.headers['x-admin-key'];

  if (!process.env.ADMIN_API_KEY || chaveRecebida !== process.env.ADMIN_API_KEY) {
    return res.status(401).json({ erro: 'Chave administrativa inválida.' });
  }

  next();
}


const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  // O Supabase exige conexão SSL.
  ssl: { rejectUnauthorized: false }
});


// Confere o formato do ID nas rotas que usam :id.
app.param('id', (req, res, next, id) => {
  const formatoUUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
  if (!formatoUUID.test(id)) {
    return res.status(400).json({ erro: 'ID inválido. Informe o UUID do curso.' });
  }
  next();
});

app.get('/', async (req, res) => {
  try {
    const resultado = await pool.query('SELECT NOW()');
    res.json({ sucesso: true, horaAtual: resultado.rows[0].now });
  } catch (erro) {
    console.error('Erro no banco:', erro.code || erro.name);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});


app.get('/courses', async (req, res) => {
  try {
    const resultado = await pool.query('SELECT * FROM courses ORDER BY course_date');
    res.json(resultado.rows);
  } catch (erro) {
    console.error('Erro no banco:', erro.code, erro.message);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});


app.get('/courses/:id', async (req, res) => {
  try {
    const resultado = await pool.query('SELECT * FROM courses WHERE id = $1', [req.params.id]);
    if (resultado.rows.length === 0) {
      return res.status(404).json({ erro: 'Curso não encontrado.' });
    }
    res.json(resultado.rows[0]);
  } catch (erro) {
    console.error('Erro no banco:', erro.code || erro.name);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});


app.post('/courses', verificarChave, async (req, res) => {
  try {
    const { title, description, location, course_date, category } = req.body || {};
    // A mesma validação é usada ao cadastrar e ao editar.
    if (typeof title !== 'string' || !title.trim()) {
      return res.status(400).json({ erro: 'Por favor, informe o título do curso.' });
    }
    if (typeof course_date !== 'string' || !course_date.trim() || Number.isNaN(Date.parse(course_date))) {
      return res.status(400).json({ erro: 'Por favor, informe uma data válida para o curso.' });
    }
    const resultado = await pool.query(
      'INSERT INTO courses (title, description, location, course_date, category) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [title.trim(), description, location, course_date, category]
    );
    res.status(201).json(resultado.rows[0]);
  } catch (erro) {
    console.error('Erro no banco:', erro.code || erro.name);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});


app.put('/courses/:id', verificarChave, async (req, res) => {
  try {
    const { title, description, location, course_date, category } = req.body || {};
    // A mesma validação é usada ao cadastrar e ao editar.
    if (typeof title !== 'string' || !title.trim()) {
      return res.status(400).json({ erro: 'Por favor, informe o título do curso.' });
    }
    if (typeof course_date !== 'string' || !course_date.trim() || Number.isNaN(Date.parse(course_date))) {
      return res.status(400).json({ erro: 'Por favor, informe uma data válida para o curso.' });
    }
    const resultado = await pool.query(
      'UPDATE courses SET title = $1, description = $2, location = $3, course_date = $4, category = $5, updated_at = NOW() WHERE id = $6 RETURNING *',
      [title.trim(), description, location, course_date, category, req.params.id]
    );
    if (resultado.rows.length === 0) {
      return res.status(404).json({ erro: 'Curso não encontrado.' });
    }
    res.json(resultado.rows[0]);
  } catch (erro) {
    console.error('Erro no banco:', erro.code || erro.name);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});


app.delete('/courses/:id', verificarChave, async (req, res) => {
  try {
    const resultado = await pool.query('DELETE FROM courses WHERE id = $1', [req.params.id]);
    if (resultado.rowCount === 0) {
      return res.status(404).json({ erro: 'Curso não encontrado.' });
    }
    res.json({ mensagem: 'Curso removido.' });
  } catch (erro) {
    console.error('Erro no banco:', erro.code, erro.message);
    if (['22007', '22008', '22P02', '23502'].includes(erro.code)) {
      return res.status(400).json({ erro: 'Confira os dados enviados, especialmente a data.' });
    }
    res.status(500).json({ erro: 'Não foi possível concluir a operação.' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor rodando em http://localhost:${PORT}`);
});