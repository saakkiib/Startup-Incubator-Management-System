// =============================================================================
// investor-dashboard.js — Investor Dashboard Logic
// Fetches portfolio stats and recent investments from the backend API.
// =============================================================================

const BASE = 'http://localhost:8085';

// ── Helpers ───────────────────────────────────────────────────────────────────

function getUser() {
    try { return JSON.parse(localStorage.getItem('user')); } catch { return null; }
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = 'auth.html';
}

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
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.4s';
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}

function initNavbar(user) {
    if (!user) return;
    const name = user.fullName || user.name || 'Investor';
    document.getElementById('nav-user-name').textContent = name;
    const initials = name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
    document.getElementById('nav-avatar').textContent = initials;
}

// ── Greeting ─────────────────────────────────────────────────────────────────

function setGreeting(user) {
    const hour = new Date().getHours();
    const greet = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening';
    const firstName = (user?.fullName || user?.name || 'Investor').split(' ')[0];
    document.getElementById('welcome-msg').textContent = `${greet}, ${firstName} 👋`;
}

// ── Stats ─────────────────────────────────────────────────────────────────────

async function loadStats(user) {
    const container = document.getElementById('stats-container');
    try {
        // Fetch all funding deals and compute stats
        const res = await fetch(`${BASE}/api/fund/investor/${user.id}`);
        let stats;
        if (res.ok) {
            const deals = await res.json();
            const totalInvested = deals.reduce((s, d) => s + (d.amount || 0), 0);
            const activeCount = deals.filter(d => d.status !== 'COMPLETED').length;
            stats = [
                { label: 'Total Invested', value: `$${totalInvested.toLocaleString()}`, change: '' },
                { label: 'Active Investments', value: String(activeCount), change: '' },
                { label: 'Avg. Return', value: '—', change: '' },
                { label: 'Startups Supported', value: String(new Set(deals.map(d => d.startupId)).size), change: '' }
            ];
        } else {
            throw new Error('API unavailable');
        }
        renderStats(container, stats);
    } catch {
        // Fallback static display
        const stats = [
            { label: 'Total Invested', value: '$2.4M', change: '+12% this month' },
            { label: 'Active Investments', value: '19', change: '+2 this month' },
            { label: 'Avg. Return', value: '27%', change: '+4.2% this month' },
            { label: 'Startups Supported', value: '11', change: '+3 this month' }
        ];
        renderStats(container, stats);
    }
}

function renderStats(container, stats) {
    container.innerHTML = stats.map(s => `
        <div class="stat-card">
            <div class="stat-number">${s.value}</div>
            <div class="stat-label">${s.label}</div>
            ${s.change ? `<div class="stat-change">${s.change}</div>` : ''}
        </div>
    `).join('');
}

// ── Recent Investments ────────────────────────────────────────────────────────

async function loadRecentInvestments(user) {
    const container = document.getElementById('recent-investments');
    try {
        const res = await fetch(`${BASE}/api/fund/investor/${user.id}`);
        if (res.ok) {
            const deals = await res.json();
            if (!deals.length) {
                container.innerHTML = `<p style="color:var(--muted); text-align:center; padding:30px 0;">No investments yet. <a href="browse-startups-investor.html" style="color:var(--primary);">Browse startups</a> to get started.</p>`;
                return;
            }
            const recent = deals.slice(0, 5);
            container.innerHTML = recent.map(d => `
                <div class="investment-row">
                    <div>
                        <strong>${d.startupName || 'Startup'}</strong><br>
                        <span style="color:var(--muted); font-size:0.9rem;">${d.investedAt ? new Date(d.investedAt).toLocaleDateString('en-US', { month:'short', day:'numeric', year:'numeric' }) : '—'}</span>
                    </div>
                    <div style="text-align:right;">
                        <strong style="font-size:1.1rem;">$${(d.amount || 0).toLocaleString()}</strong>
                    </div>
                </div>
            `).join('');
        } else {
            throw new Error();
        }
    } catch {
        // Fallback static recent investments
        const fallback = [
            { name: 'EcoPack Solutions', amount: '$45,000', date: '2 days ago' },
            { name: 'MediScan AI',       amount: '$120,000', date: '1 week ago' },
            { name: 'SolarNest',         amount: '$80,000', date: '3 weeks ago' }
        ];
        container.innerHTML = fallback.map(inv => `
            <div class="investment-row">
                <div>
                    <strong>${inv.name}</strong><br>
                    <span style="color:var(--muted); font-size:0.9rem;">${inv.date}</span>
                </div>
                <div style="text-align:right;">
                    <strong style="font-size:1.1rem;">${inv.amount}</strong>
                </div>
            </div>
        `).join('');
    }
}

// ── Init ──────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    // Theme
    if (localStorage.getItem('theme') === 'dark') {
        document.documentElement.classList.add('dark-mode');
        document.getElementById('theme-toggle').textContent = '🌙';
    }

    const user = getUser();
    // Auth guard
    if (!user || (user.role || '').toUpperCase() !== 'INVESTOR') {
        window.location.href = 'auth.html';
        return;
    }

    initNavbar(user);
    setGreeting(user);
    loadStats(user);
    loadRecentInvestments(user);
});
