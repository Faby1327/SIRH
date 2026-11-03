document.querySelectorAll('[data-table-search]').forEach((search) => {
  const table = search.closest('.c-table');
  if (!table) return;

  const filters = table.querySelectorAll('[data-table-filter]');
  const sortSelect = table.querySelector('[data-table-sort]');
  const rows = table.querySelectorAll('[data-table-row]');
  const tbody = table.querySelector('tbody, [role="rowgroup"]');
  const sortRows = () => {
    if (!sortSelect || !tbody || !sortSelect.value) return;
    const descending = sortSelect.value.endsWith('-desc');
    const field = sortSelect.value.replace(/-(asc|desc)$/, '');
    const sorted = Array.from(rows).sort((a, b) => {
      const left = (a.dataset[field] || '').toLocaleLowerCase('fr');
      const right = (b.dataset[field] || '').toLocaleLowerCase('fr');
      const leftNumber = Number(left.replace(',', '.'));
      const rightNumber = Number(right.replace(',', '.'));
      const comparison = Number.isNaN(leftNumber) || Number.isNaN(rightNumber)
        ? left.localeCompare(right, 'fr', {numeric: true})
        : leftNumber - rightNumber;
      return descending ? -comparison : comparison;
    });
    sorted.forEach((row) => tbody.appendChild(row));
  };
  const applyFilters = () => {
    const query = search.value.trim().toLocaleLowerCase('fr');
    rows.forEach((row) => {
      const matchesSearch = (row.dataset.search || row.textContent).toLocaleLowerCase('fr').includes(query);
      const matchesSelects = Array.from(filters).every((filter) => {
        const selected = filter.value;
        if (!selected) return true;
        const rowValue = row.dataset[filter.dataset.tableFilter] || '';
        if (selected === 'attente') return !['BROUILLON', 'VALIDEE', 'REFUSEE', 'BUDGET_INSUFFISANT', 'ANNULEE'].includes(rowValue);
        if (selected.includes(',')) return selected.split(',').includes(rowValue);
        return rowValue === selected;
      });
      row.hidden = !(matchesSearch && matchesSelects);
    });
    sortRows();
  };

  search.addEventListener('input', applyFilters);
  filters.forEach((filter) => filter.addEventListener('change', applyFilters));
  if (sortSelect) sortSelect.addEventListener('change', sortRows);
});
