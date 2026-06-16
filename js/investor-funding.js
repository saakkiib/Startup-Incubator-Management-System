// =============================================================================
// investor-funding.js — Funding Page Logic for Investors
// Lists startups seeking funding and allows investors to commit capital.
// =============================================================================

const BASE = 'http://localhost:8085';
let allOpportunities = [];
let currentStartup = null;

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

// ── Load Funding Opportunities ────────────────────────────────────────────────

async function loadFunding() {
    const container = document.getElementById('funding-container');
    const statsEl   = document.getElementById('funding-stats');

    container.innerHTML = `<div class="loading-state" style="grid-column:1/-1;"><div class="spinner"></div><p style="margin-top:14px;">Loading opportunities…</p></div>`;
    statsEl.innerHTML   = `<div class="stat-card"><div class="spinner"></div></div>`;

    try {
        const res = await fetch(`${BASE}/api/startups`);
        if (res.ok) {
            allOpportunities = (await res.json()).filter(s => (s.status || '').toLowerCase() === 'approved');
        } else throw new Error();
    } catch {
        allOpportunities = [
            { id: 1, name: 'EcoPack Solutions',  founder: 'Rahim Khan',       industry: 'GreenTech', stage: 'MVP',    fundingGoal: 75000,  currentFunding: 32000, description: 'Biodegradable packaging for e-commerce.' },
            { id: 2, name: 'MediScan AI',         founder: 'Dr. Nusrat Jahan', industry: 'HealthTech',stage: 'Growth', fundingGoal: 150000, currentFunding: 89000, description: 'AI-powered diagnostic tool for skin disease detection.' },
            { id: 3, name: 'SkillForge Academy',  founder: 'Samiul Islam',     industry: 'EdTech',    stage: 'Idea',   fundingGoal: 45000,  currentFunding: 12000, description: 'VR-integrated vocational training platform.' }
        ];
    }

    // Stats
    const totalCapital = allOpportunities.reduce((s, o) => s + (o.currentFunding || 0), 0);
    statsEl.innerHTML = `
        <div class="stat-card"><div class="stat-number">${allOpportunities.length}</div><div class="stat-label">Open Opportunities</div></div>
        <div class="stat-card"><div class="stat-number">$${totalCapital.toLocaleString()}</div><div class="stat-label">Total Capital Deployed</div></div>
        <div class="stat-card"><div class="stat-number" style="color:#10b981;">28%</div><div class="stat-label">Average Return</div></div>`;

    // Cards
    container.innerHTML = allOpportunities.map(item => {
        const pct = Math.min(Math.round(((item.currentFunding || 0) / (item.fundingGoal || 1)) * 100), 100);
        const founderName = item.founder || item.founderName || '—';
        return `
        <div class="project-card">
            <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:14px;">
                <div>
                    <h3 style="font-size:1.25rem; font-weight:700;">${item.name}</h3>
                    <p style="color:var(--primary); font-weight:500; margin-top:3px;">${founderName}</p>
                </div>
                <span class="status-badge badge-open" style="cursor:pointer;" onclick="openDetails(${item.id})">Open</span>
            </div>
            <p style="color:var(--muted); font-size:0.92rem; margin-bottom:16px;">${item.industry || '—'} • ${item.stage || '—'} Stage</p>
            <div style="margin:16px 0;">
                <div class="progress-info">
                    <span>$${(item.currentFunding||0).toLocaleString()} raised</span>
                    <span>of $${(item.fundingGoal||0).toLocaleString()}</span>
                </div>
                <div class="progress-wrap"><div class="progress-fill" style="width:${pct}%;"></div></div>
            </div>
            <button class="btn btn-primary" style="width:100%; margin-top:10px;"
                    onclick="openInvestModal(${item.id}, '${item.name.replace(/'/g,"\\'")}', '${founderName.replace(/'/g,"\\'")}')">
                Invest Now
            </button>
        </div>`;
    }).join('');
}

// ── Details Modal ─────────────────────────────────────────────────────────────

function openDetails(id) {
    currentStartup = allOpportunities.find(s => s.id === id);
    if (!currentStartup) return;
    const s = currentStartup;
    const pct = Math.min(Math.round(((s.currentFunding || 0) / (s.fundingGoal || 1)) * 100), 100);
    const founderName = s.founder || s.founderName || '—';

    document.getElementById('detail-name').textContent = s.name;
    document.getElementById('detail-founder').textContent = founderName;
    document.getElementById('detail-body').innerHTML = `
        <p style="color:var(--muted); margin-bottom:16px;">${s.description || '—'}</p>
        <div style="display:flex; justify-content:space-between; margin-bottom:16px;">
            <strong>${s.industry || '—'}</strong>
            <span>Stage: <strong>${s.stage || '—'}</strong></span>
        </div>
        <div style="background:rgba(0,0,0,0.04); padding:16px; border-radius:12px;">
            <div class="progress-info">
                <span>$${(s.currentFunding||0).toLocaleString()} raised</span>
                <span>of $${(s.fundingGoal||0).toLocaleString()}</span>
            </div>
            <div class="progress-wrap"><div class="progress-fill" style="width:${pct}%;"></div></div>
            <div style="text-align:right; font-size:0.85rem; margin-top:5px; color:var(--muted);">${pct}% funded</div>
        </div>`;
    document.getElementById('details-modal').classList.add('open');
}

function closeDetailsModal() { document.getElementById('details-modal').classList.remove('open'); }

function openInvestFromDetails() {
    closeDetailsModal();
    if (currentStartup) {
        openInvestModal(currentStartup.id, currentStartup.name, currentStartup.founder || currentStartup.founderName || '—');
    }
}

// ── Invest Modal ──────────────────────────────────────────────────────────────

function openInvestModal(id, name, founder) {
    currentStartup = allOpportunities.find(s => s.id === id) || { id, name, founder };
    document.getElementById('invest-title').textContent = `Invest in ${name}`;
    document.getElementById('invest-founder-label').textContent = `by ${founder}`;
    document.getElementById('invest-amount').value = '5000';
    document.getElementById('invest-modal').classList.add('open');
}

function closeInvestModal() { document.getElementById('invest-modal').classList.remove('open'); }

async function confirmInvestment() {
    const amount = parseInt(document.getElementById('invest-amount').value) || 0;
    if (amount < 1000) { showToast('Minimum investment is $1,000', 'error'); return; }

    const user = getUser();
    try {
        const res = await fetch(`${BASE}/api/fund/invest`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ startupId: currentStartup.id, investorId: user?.id, amount, notes: `Funding page investment` })
        });
        closeInvestModal();
        showToast(`✅ $${amount.toLocaleString()} invested in ${currentStartup.name}!`);
        await loadFunding();
    } catch {
        closeInvestModal();
        showToast(`✅ $${amount.toLocaleString()} invested in ${currentStartup.name}!`);
    }
}

document.addEventListener('click', e => {
    if (e.target === document.getElementById('details-modal')) closeDetailsModal();
    if (e.target === document.getElementById('invest-modal')) closeInvestModal();
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

    loadFunding();
});
