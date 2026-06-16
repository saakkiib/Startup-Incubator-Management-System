// =============================================================================
// investor-history.js — Investment History Logic
// Fetches all past and active investments for the logged-in investor.
// =============================================================================

const BASE = 'http://localhost:8085';
let allInvestments = [];

function getUser() { try { return JSON.parse(localStorage.getItem('user')); } catch { return null; } }
function logout() { localStorage.removeItem('user'); window.location.href = 'auth.html'; }
function toggleTheme() {
    document.documentElement.classList.toggle('dark-mode');
    const isDark = document.documentElement.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    document.getElementById('theme-toggle').textContent = isDark ? '🌙' : '☀️';
}
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 400); }, 3500);
}

// ── Load History ──────────────────────────────────────────────────────────────

async function loadHistory() {
    const tbody    = document.getElementById('history-body');
    const summaryEl= document.getElementById('summary-cards');

    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:60px;"><div class="spinner" style="margin:0 auto;"></div><p style="margin-top:14px; color:var(--muted);">Loading history…</p></td></tr>`;

    const user = getUser();

    try {
        const res = await fetch(`${BASE}/api/fund/investor/${user.id}`);
        if (res.ok) {
            const data = await res.json();
            allInvestments = data.map(d => ({
                id:       d.id,
                startup:  d.startupName || 'Startup',
                amount:   d.amount || 0,
                date:     d.investedAt ? new Date(d.investedAt).toLocaleDateString('en-CA') : '—',
                equity:   d.equity ? `${d.equity}%` : '—',
                status:   (d.status || 'active').toLowerCase(),
                value:    d.currentValue || d.amount || 0,
                startupId: d.startupId
            }));
        } else throw new Error();
    } catch {
        allInvestments = [
            { id: 101, startup: 'EcoPack Solutions',   amount: 25000, date: '2026-04-12', equity: '3.2%', status: 'active',    value: 31200 },
            { id: 102, startup: 'MediScan AI',         amount: 45000, date: '2026-03-28', equity: '4.8%', status: 'active',    value: 52800 },
            { id: 103, startup: 'SkillForge Academy',  amount: 12000, date: '2026-02-15', equity: '5.5%', status: 'completed', value: 16800 },
            { id: 104, startup: 'GreenCharge EV',      amount: 38000, date: '2025-12-05', equity: '2.9%', status: 'active',    value: 41500 }
        ];
    }

    // Compute summary
    const totalInvested = allInvestments.reduce((s, i) => s + i.amount, 0);
    const activeCount   = allInvestments.filter(i => i.status === 'active').length;
    const totalValue    = allInvestments.reduce((s, i) => s + i.value, 0);
    const returns       = totalValue - totalInvested;

    summaryEl.innerHTML = `
        <div class="stat-card"><div class="stat-number">$${totalInvested.toLocaleString()}</div><div class="stat-label">Total Invested</div></div>
        <div class="stat-card"><div class="stat-number">${activeCount}</div><div class="stat-label">Active Investments</div></div>
        <div class="stat-card"><div class="stat-number" style="color:#10b981;">+$${returns.toLocaleString()}</div><div class="stat-label">Total Returns</div></div>
        <div class="stat-card"><div class="stat-number">$${totalValue.toLocaleString()}</div><div class="stat-label">Portfolio Value</div></div>`;

    renderHistory(allInvestments);
}

function renderHistory(data) {
    const tbody = document.getElementById('history-body');
    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:60px; color:var(--muted);">No investments found.</td></tr>`;
        return;
    }
    tbody.innerHTML = data.map(item => `
        <tr>
            <td><strong>${item.startup}</strong></td>
            <td style="font-weight:600;">$${item.amount.toLocaleString()}</td>
            <td style="color:var(--muted);">${item.date}</td>
            <td>${item.equity}</td>
            <td><span class="status-badge ${item.status === 'active' ? 'badge-active' : 'badge-completed'}">${item.status.toUpperCase()}</span></td>
            <td style="color:var(--primary); font-weight:600;">$${item.value.toLocaleString()}</td>
            <td>
                <button onclick="viewDetails(${item.id})" class="btn btn-ghost btn-sm" title="View Details">👁</button>
            </td>
        </tr>`).join('');
}

// ── Filter ────────────────────────────────────────────────────────────────────

function filterHistory() {
    const search = (document.getElementById('search-input').value || '').toLowerCase();
    const status = document.getElementById('status-filter').value;
    const filtered = allInvestments.filter(item => {
        const matchSearch = !search || item.startup.toLowerCase().includes(search);
        const matchStatus = !status || item.status === status;
        return matchSearch && matchStatus;
    });
    renderHistory(filtered);
}

// ── View Details ──────────────────────────────────────────────────────────────

function viewDetails(id) {
    const inv = allInvestments.find(i => i.id === id);
    if (!inv) return;
    const roi = (((inv.value - inv.amount) / inv.amount) * 100).toFixed(1);
    document.getElementById('modal-title').textContent = inv.startup;
    document.getElementById('modal-body').innerHTML = `
        <div style="display:flex; flex-direction:column; gap:12px;">
            <div><strong>Startup:</strong> ${inv.startup}</div>
            <div><strong>Amount Invested:</strong> $${inv.amount.toLocaleString()}</div>
            <div><strong>Date:</strong> ${inv.date}</div>
            <div><strong>Equity:</strong> ${inv.equity}</div>
            <div><strong>Status:</strong> <span class="status-badge ${inv.status === 'active' ? 'badge-active' : 'badge-completed'}">${inv.status.toUpperCase()}</span></div>
            <div><strong>Current Value:</strong> $${inv.value.toLocaleString()}</div>
            <div><strong>ROI:</strong> <span style="color:${roi > 0 ? '#10b981' : 'var(--danger)'};">${roi > 0 ? '+' : ''}${roi}%</span></div>
        </div>`;
    document.getElementById('investment-modal').classList.add('open');
}

function closeModal() { document.getElementById('investment-modal').classList.remove('open'); }

document.addEventListener('click', e => {
    if (e.target === document.getElementById('investment-modal')) closeModal();
});

// ── Init ──────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    if (localStorage.getItem('theme') === 'dark') {
        document.documentElement.classList.add('dark-mode');
        document.getElementById('theme-toggle').textContent = '🌙';
    }

    const user = getUser();
    if (!user || (user.role || '').toUpperCase() !== 'INVESTOR') {
        window.location.href = 'auth.html'; return;
    }
    const name = user.fullName || user.name || 'Investor';
    document.getElementById('nav-user-name').textContent = name;
    document.getElementById('nav-avatar').textContent = name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();

    loadHistory();
});
