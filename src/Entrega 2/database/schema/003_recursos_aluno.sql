BEGIN;
CREATE TABLE IF NOT EXISTS app_comunicados (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID REFERENCES users(id),
    titulo TEXT NOT NULL CHECK (length(trim(titulo)) BETWEEN 1 AND 150),
    texto TEXT NOT NULL CHECK (length(trim(texto)) BETWEEN 1 AND 4000),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS app_comunicados_usuario_idx ON app_comunicados(usuario_id, criado_em);
CREATE TABLE IF NOT EXISTS app_atendimento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES users(id),
    autor TEXT NOT NULL CHECK (autor IN ('aluno', 'equipe')),
    texto TEXT NOT NULL CHECK (length(trim(texto)) BETWEEN 1 AND 2000),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS app_atendimento_usuario_idx ON app_atendimento(usuario_id, criado_em);
CREATE TABLE IF NOT EXISTS app_certificados (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES users(id),
    curso_id UUID NOT NULL REFERENCES courses(id),
    documento_url TEXT NOT NULL CHECK (documento_url LIKE 'https://%'),
    emitido_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(usuario_id, curso_id)
);
CREATE TABLE IF NOT EXISTS app_questionarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo TEXT NOT NULL,
    perguntas JSONB NOT NULL,
    perfis JSONB NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true
);
CREATE TABLE IF NOT EXISTS app_resultados_perfil (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES users(id),
    questionario_id UUID NOT NULL REFERENCES app_questionarios(id),
    respostas JSONB NOT NULL,
    resultado JSONB NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS app_resultados_usuario_idx ON app_resultados_perfil(usuario_id, criado_em);
ALTER TABLE app_comunicados ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_atendimento ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_certificados ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_questionarios ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_resultados_perfil ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON app_comunicados, app_atendimento, app_certificados, app_questionarios, app_resultados_perfil FROM PUBLIC;
COMMIT;
