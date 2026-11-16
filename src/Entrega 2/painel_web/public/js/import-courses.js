export async function runCourseImport(button, request, refresh, notify) {
  if (button.disabled) return;
  const original = button.innerHTML;
  button.disabled = true;
  button.textContent = 'Consultando Render... Pode levar até 60 segundos.';
  try {
    const result = await request();
    notify(`Importação concluída: ${result.added} novo(s), ${result.updated} atualizado(s).`);
    await refresh();
  } finally {
    button.disabled = false;
    button.innerHTML = original;
  }
}
