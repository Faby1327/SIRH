(() => {
  const tab = new URLSearchParams(window.location.search).get('onglet');
  if (tab === 'absence') {
    const absenceTab = document.getElementById('tab-absence');
    if (absenceTab) absenceTab.checked = true;
  }

  document.querySelectorAll('.formulaire-demande').forEach(form => {
    const start = form.querySelector('input[name="dateDebut"]');
    const end = form.querySelector('input[name="dateFin"]');
    if (!start || !end) return;
    const today = start.min || new Date().toISOString().slice(0, 10);
    start.min = today;
    end.min = today;
    const syncBounds = () => {
      end.min = start.value && start.value > today ? start.value : today;
      start.max = end.value || '';
    };
    start.addEventListener('change', syncBounds);
    end.addEventListener('change', syncBounds);
    syncBounds();
  });
})();
