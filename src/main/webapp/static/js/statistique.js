const contextPath = document.body.getAttribute('data-context-path') || '';
const currency = new Intl.NumberFormat('fr-FR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
});

function money(value) {
    return currency.format(Number(value || 0)) + ' Ar';
}

function endpoint(path) {
    return `${contextPath}/statistiques${path}`;
}

async function fetchJson(path, params = {}) {
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${endpoint(path)}${query ? `?${query}` : ''}`, { headers: { Accept: 'application/json' } });
    if (!response.ok) {
        throw new Error('Impossible de charger les données');
    }
    return response.json();
}

function buildChart(data) {
    const canvas = document.getElementById('chartStat');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (!data.length) {
        ctx.fillStyle = '#666';
        ctx.font = '16px Arial';
        ctx.fillText('Aucune donnée disponible', 20, 40);
        return;
    }

    const labels = data.map(item => item.periode || '');
    const revenues = data.map(item => Number(item.chiffreAffaire || 0));
    const profits = data.map(item => Number(item.benefice || 0));
    const maxValue = Math.max(1, ...revenues, ...profits);

    const padding = { top: 20, right: 20, bottom: 40, left: 50 };
    const chartWidth = canvas.width - padding.left - padding.right;
    const chartHeight = canvas.height - padding.top - padding.bottom;

    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
        const y = padding.top + (chartHeight * i) / 4;
        ctx.beginPath();
        ctx.moveTo(padding.left, y);
        ctx.lineTo(canvas.width - padding.right, y);
        ctx.stroke();
    }

    const drawSeries = (values, color) => {
        ctx.strokeStyle = color;
        ctx.lineWidth = 2;
        ctx.beginPath();
        values.forEach((value, index) => {
            const x = padding.left + (chartWidth * index) / Math.max(1, values.length - 1);
            const y = padding.top + chartHeight - (value / maxValue) * chartHeight;
            if (index === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
        });
        ctx.stroke();
        values.forEach((value, index) => {
            const x = padding.left + (chartWidth * index) / Math.max(1, values.length - 1);
            const y = padding.top + chartHeight - (value / maxValue) * chartHeight;
            ctx.fillStyle = color;
            ctx.beginPath();
            ctx.arc(x, y, 4, 0, Math.PI * 2);
            ctx.fill();
        });
    };

    drawSeries(revenues, '#16a34a');
    drawSeries(profits, '#2563eb');

    ctx.fillStyle = '#374151';
    ctx.font = '12px Arial';
    labels.forEach((label, index) => {
        const x = padding.left + (chartWidth * index) / Math.max(1, labels.length - 1);
        ctx.fillText(label, x - 18, canvas.height - 10);
    });
}

function renderZones(rows) {
    const tbody = document.getElementById('tableZone');
    if (!tbody) return;
    tbody.innerHTML = rows.length ? rows.map(row => `
        <tr>
            <td>${row.zone || '-'}</td>
            <td>${money(row.chiffreAffaire || 0)}</td>
        </tr>
    `).join('') : '<tr><td colspan="2" class="text-center">Aucune donnée</td></tr>';
}

async function loadStats() {
    const dateDebut = document.getElementById('dateDebut').value;
    const dateFin = document.getElementById('dateFin').value;
    const zone = document.getElementById('zone').value;
    const periode = document.getElementById('periode').value;

    try {
        const [ca, benefice, graph] = await Promise.all([
            fetchJson('/chiffreAffaire', { dateDebut, dateFin, zone, periode }),
            fetchJson('/benefice', { dateDebut, dateFin, zone, periode }),
            fetchJson('/graphique', { dateDebut, dateFin, zone, periode })
        ]);

        const dataGraph = Array.isArray(graph) ? graph : [];

        document.getElementById('caGlobal').textContent = money(ca);
        document.getElementById('benefice').textContent = money(benefice);
        document.getElementById('sessions').textContent = dataGraph.length;
        buildChart(dataGraph);
        renderZones([]);
    } catch (error) {
        console.error(error);
        document.getElementById('caGlobal').textContent = money(0);
        document.getElementById('benefice').textContent = money(0);
        document.getElementById('sessions').textContent = '0';
        buildChart([]);
        renderZones([]);
    }
}

document.getElementById('btnFiltrer')?.addEventListener('click', loadStats);
window.addEventListener('DOMContentLoaded', loadStats);
