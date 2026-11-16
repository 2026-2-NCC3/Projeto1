BEGIN;
CREATE TABLE IF NOT EXISTS public.app_encontros (
    id uuid PRIMARY KEY,
    course_id uuid NOT NULL REFERENCES public.courses(id) ON DELETE RESTRICT,
    titulo text NOT NULL CHECK (length(trim(titulo)) BETWEEN 1 AND 150),
    data timestamptz NOT NULL,
    status text NOT NULL DEFAULT 'agendado' CHECK (status IN ('agendado', 'aberto', 'encerrado'))
);
CREATE INDEX IF NOT EXISTS app_encontros_curso ON public.app_encontros(course_id);
CREATE TABLE IF NOT EXISTS public.app_presencas_encontros (
    id uuid PRIMARY KEY,
    encontro_id uuid NOT NULL REFERENCES public.app_encontros(id) ON DELETE RESTRICT,
    enrollment_id uuid NOT NULL REFERENCES public.enrollments(id) ON DELETE RESTRICT,
    data_hora timestamptz NOT NULL DEFAULT now(),
    UNIQUE (encontro_id, enrollment_id)
);
CREATE INDEX IF NOT EXISTS app_presencas_encontros_inscricao ON public.app_presencas_encontros(enrollment_id);
ALTER TABLE public.app_encontros ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.app_presencas_encontros ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON public.app_encontros, public.app_presencas_encontros FROM PUBLIC, anon, authenticated;
COMMIT;
