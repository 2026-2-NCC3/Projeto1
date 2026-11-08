const { calcularResultado } = require('./perfil');

module.exports = function registrarRecursos(app, pool, autenticar, administrar) {
    const uuid = valor => typeof valor === 'string' && /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(valor);
    const textoValido = (valor, maximo) => typeof valor === 'string' && valor.trim().length > 0 && valor.length <= maximo;
    const tratar = executar => async (req, res) => {
        try {
            await executar(req, res);
        } catch (erro) {
            console.error('Recursos do aluno:', erro.code || erro.name);
            res.status(500).json({ erro: 'Não foi possível concluir. Tente novamente.' });
        }
    };

    app.get('/me/comunicados', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            `SELECT id, titulo, texto, criado_em FROM app_comunicados
             WHERE usuario_id IS NULL OR usuario_id = $1 ORDER BY criado_em DESC LIMIT 100`,
            [req.usuario.id]
        );
        res.json(rows);
    }));

    app.get('/me/novidades', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            `SELECT id::text AS id, titulo, texto, criado_em FROM app_comunicados
             WHERE usuario_id IS NULL OR usuario_id = $1
             UNION ALL
             SELECT 'curso-' || id::text, title,
                    'Curso ou palestra disponível. Consulte os detalhes e a agenda.', updated_at
             FROM courses WHERE is_active = true
             UNION ALL
             SELECT 'certificado-' || a.id::text, 'Certificado disponível', c.title, a.emitido_em
             FROM app_certificados a JOIN courses c ON c.id = a.curso_id WHERE a.usuario_id = $1
             ORDER BY criado_em DESC LIMIT 100`, [req.usuario.id]
        );
        res.json(rows);
    }));

    app.get('/me/certificados', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            `SELECT a.id, c.title AS titulo, a.documento_url, a.emitido_em
             FROM app_certificados a JOIN courses c ON c.id = a.curso_id
             WHERE a.usuario_id = $1 ORDER BY a.emitido_em DESC`, [req.usuario.id]
        );
        res.json(rows);
    }));

    app.get('/me/atendimento', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            `SELECT id, autor, texto, criado_em FROM (
                SELECT id, autor, texto, criado_em FROM app_atendimento
                WHERE usuario_id = $1 ORDER BY criado_em DESC LIMIT 100
             ) conversa ORDER BY criado_em`, [req.usuario.id]
        );
        res.json(rows);
    }));

    app.post('/me/atendimento', autenticar, tratar(async (req, res) => {
        if (!textoValido(req.body?.texto, 2000)) {
            return res.status(400).json({ erro: 'Escreva de 1 a 2000 caracteres.' });
        }
        const { rows } = await pool.query(
            `INSERT INTO app_atendimento (usuario_id, autor, texto)
             VALUES ($1, 'aluno', $2) RETURNING id`, [req.usuario.id, req.body.texto.trim()]
        );
        res.status(201).json(rows[0]);
    }));

    app.get('/me/teste-perfil', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            'SELECT id, titulo, perguntas FROM app_questionarios WHERE ativo = true ORDER BY id LIMIT 1'
        );
        if (!rows.length) return res.status(404).json({ erro: 'Nenhum questionário disponível.' });
        const teste = rows[0];
        teste.perguntas = teste.perguntas.map(pergunta => ({
            texto: pergunta.texto,
            alternativas: pergunta.alternativas.map(item => ({ texto: item.texto }))
        }));
        res.json(teste);
    }));

    app.post('/me/teste-perfil', autenticar, tratar(async (req, res) => {
        if (!uuid(req.body?.questionario_id)) {
            return res.status(400).json({ erro: 'Questionário inválido.' });
        }
        const { rows } = await pool.query(
            'SELECT * FROM app_questionarios WHERE id = $1 AND ativo = true', [req.body.questionario_id]
        );
        if (!rows.length) return res.status(404).json({ erro: 'Questionário indisponível.' });
        let resultado;
        try {
            resultado = calcularResultado(rows[0], req.body.respostas);
        } catch (erro) {
            return res.status(400).json({ erro: erro.message });
        }
        await pool.query(
            `INSERT INTO app_resultados_perfil (usuario_id, questionario_id, respostas, resultado)
             VALUES ($1, $2, $3, $4)`,
            [req.usuario.id, req.body.questionario_id, JSON.stringify(req.body.respostas), JSON.stringify(resultado)]
        );
        res.status(201).json(resultado);
    }));

    app.get('/me/resultado-perfil', autenticar, tratar(async (req, res) => {
        const { rows } = await pool.query(
            `SELECT resultado, criado_em FROM app_resultados_perfil
             WHERE usuario_id = $1 ORDER BY criado_em DESC LIMIT 1`, [req.usuario.id]
        );
        res.json(rows[0] || { resultado: null });
    }));

    app.post('/admin/comunicados', administrar, tratar(async (req, res) => {
        const { titulo, texto, usuario_id } = req.body || {};
        if (!textoValido(titulo, 150) || !textoValido(texto, 4000) || (usuario_id != null && !uuid(usuario_id))) {
            return res.status(400).json({ erro: 'Confira título, mensagem e destinatário.' });
        }
        const { rows } = await pool.query(
            `INSERT INTO app_comunicados (usuario_id, titulo, texto) VALUES ($1, $2, $3) RETURNING id`,
            [usuario_id || null, titulo.trim(), texto.trim()]
        );
        res.status(201).json(rows[0]);
    }));

    app.get('/admin/atendimento', administrar, tratar(async (req, res) => {
        if (!uuid(req.query.usuario_id)) return res.status(400).json({ erro: 'Informe o usuário.' });
        const { rows } = await pool.query(
            `SELECT id, autor, texto, criado_em FROM app_atendimento
             WHERE usuario_id = $1 ORDER BY criado_em DESC LIMIT 100`, [req.query.usuario_id]
        );
        res.json(rows);
    }));

    app.post('/admin/atendimento', administrar, tratar(async (req, res) => {
        if (!uuid(req.body?.usuario_id) || !textoValido(req.body?.texto, 2000)) {
            return res.status(400).json({ erro: 'Informe o usuário e uma mensagem de até 2000 caracteres.' });
        }
        const { rows } = await pool.query(
            `INSERT INTO app_atendimento (usuario_id, autor, texto) VALUES ($1, 'equipe', $2) RETURNING id`,
            [req.body.usuario_id, req.body.texto.trim()]
        );
        res.status(201).json(rows[0]);
    }));

    app.post('/admin/certificados', administrar, tratar(async (req, res) => {
        const { usuario_id, curso_id, documento_url } = req.body || {};
        let url;
        try { url = new URL(documento_url); } catch { url = null; }
        if (!uuid(usuario_id) || !uuid(curso_id) || !url || url.protocol !== 'https:') {
            return res.status(400).json({ erro: 'Confira o aluno, curso e endereço HTTPS do documento.' });
        }
        const { rows } = await pool.query(
            `INSERT INTO app_certificados (usuario_id, curso_id, documento_url)
             SELECT $1, c.id, $3 FROM courses c
             WHERE c.id = $2 AND c.has_certificate = true AND c.course_date < now()
               AND EXISTS (SELECT 1 FROM enrollments e JOIN presencas p ON p.enrollment_id = e.id
                           WHERE e.user_id = $1 AND e.course_id = c.id)
             ON CONFLICT (usuario_id, curso_id) DO UPDATE SET documento_url = EXCLUDED.documento_url
             RETURNING id`, [usuario_id, curso_id, documento_url]
        );
        if (!rows.length) return res.status(422).json({ erro: 'Confira os critérios de conclusão e presença do aluno.' });
        res.status(201).json(rows[0]);
    }));
};
