BEGIN;
CREATE TABLE IF NOT EXISTS public.painel_web_dados (
  id text PRIMARY KEY CHECK (id = 'principal'),
  dados jsonb NOT NULL CHECK (jsonb_typeof(dados) = 'object'),
  atualizado_em timestamptz NOT NULL
);
ALTER TABLE public.painel_web_dados ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON TABLE public.painel_web_dados FROM PUBLIC, anon, authenticated;
GRANT SELECT, INSERT, UPDATE ON TABLE public.painel_web_dados TO service_role;
COMMIT;
