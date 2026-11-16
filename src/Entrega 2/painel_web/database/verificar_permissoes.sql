SELECT column_name, data_type, is_nullable FROM information_schema.columns
WHERE table_schema='public' AND table_name='painel_web_dados' ORDER BY ordinal_position;
SELECT relrowsecurity AS rls_ativo FROM pg_class
WHERE oid=to_regclass('public.painel_web_dados');
SELECT grantee, privilege_type FROM information_schema.role_table_grants
WHERE table_schema='public' AND table_name='painel_web_dados';
SELECT policyname, roles, cmd FROM pg_policies
WHERE schemaname='public' AND tablename='painel_web_dados';
