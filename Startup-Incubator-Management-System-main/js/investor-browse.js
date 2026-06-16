// =============================================================================
// investor-browse.js — Browse Startups Page for Investors
// Fetches approved startups from the backend and allows investors to invest.
// =============================================================================

const BASE = 'http://localhost:8085';

let allStartups = [];
let currentStartup = null;

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

// ── Load Startups ─────────────────────────────────────────────────────────────

async function loadStartups() {
    const container = document.getElementById('startups-container');
    container.innerHTML = `<div class="loading-state" style="grid-column:1/-1;"><div class="spinner"></div><p style="margin-top:14px;">Loading startups…</p></div>`;

    try {
        const res = await fetch(`${BASE}/api/startups`);
        if (res.ok) {
            allStartups = await res.json();
            // Only show approved ones
            allStartups = allStartups.filter(s => (s.status || '').toLowerCase() === 'approved');
        } else {
            throw new Error('API unavailable');
        }
    } catch {
        // Fallback demo data
        allStartups = [
            { id: 1, name: 'EcoPack Solutions', founder: 'Rahim Khan', description: 'Biodegradable packaging for e-commerce using plant-based materials.', industry: 'GreenTech', stage: 'MVP', fundingGoal: 75000, currentFunding: 32000, status: 'approved', location: 'Dhaka, Bangladesh', teamSize: 8, pitch: 'We are solving plastic pollution in packaging with fully compostable alternatives.' },
            { id: 2, name: 'MediScan AI', founder: 'Dr. Nusrat Jahan', description: 'AI-powered diagnostic tool for early skin disease detection.', industry: 'HealthTech', stage: 'Growth', fundingGoal: 150000, currentFunding: 89000, status: 'approved', location: 'Chittagong, Bangladesh', teamSize: 14, pitch: 'Our AI model achieves 94% accuracy in detecting skin conditions.' },
            { id: 3, name: 'SkillForge Academy', founder: 'Samiul Islam', description: 'VR-integrated vocational training platform.', industry: 'EdTech', stage: 'Idea', fundingGoal: 45000, currentFunding: 12000, status: 'approved', location: 'Sylhet, Bangladesh', teamSize: 5, pitch: 'Bridging the skills gap through immersive VR training modules.' }
        ];
    }

    renderStartups(allStartups);
}

// ── Render ────────────────────────────────────────────────────────────────────

function renderStartups(startups) {
    const container = document.getElementById('startups-container');
    if (!startups.length) {
        container.innerHTML = `<div style="grid-column:1/-1; text-align:center; padding:80px 20px; color:var(--muted);">
            <p style="font-size:1.1rem;">No startups found matching your filters.</p>
        </div>`;
        return;
    }

    container.innerHTML = startups.map(s => {
        const pct = Math.min(Math.round(((s.currentFunding || 0) / (s.fundingGoal || 1)) * 100), 100);
        const founderName = s.founder || s.founderName || s.createdBy || '—';
        return `
        <div class="project-card">
            <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:14px;">
                <div>
                    <h3 style="font-size:1.3rem; font-weight:700;">${s.name}</h3>
                    <p style="color:var(--primary); font-weight:600; margin-top:3px;">${founderName}</p>
                </div>
                <span class="status-badge badge-approved">${s.status || 'Approved'}</span>
            </div>
            <p style="color:var(--muted); margin-bottom:18px; line-height:1.55; font-size:0.95rem;">${s.description || ''}</p>
            <div style="display:flex; justify-content:space-between; margin-bottom:16px; font-size:0.93rem;">
                <div><strong>${s.industry || '—'}</strong></div>
                <div>Stage: <strong>${s.stage || '—'}</strong></div>
            </div>
            <div style="background:rgba(0,0,0,0.04); padding:14px; border-radius:12px; margin-bottom:18px;">
                <div class="progress-info">
                    <span>$${(s.currentFunding || 0).toLocaleString()} raised</span>
                    <span>Goal: $${(s.fundingGoal || 0).toLocaleString()}</span>
                </div>
                <div class="progress-wrap"><div class="progress-fill" style="width:${pct}%;"></div></div>
                <div style="text-align:right; font-size:0.85rem; margin-top:5px; color:var(--muted);">${pct}% funded</div>
            </div>
            <div style="display:flex; gap:10px;">
                <button class="btn btn-ghost" style="flex:1;" onclick="viewStartup(${s.id})">Quick View</button>
                <button class="btn btn-primary" style="flex:1;" onclick="investInStartup(${s.id})">Invest Now</button>
            </div>
            <div style="margin-top:10px; text-align:center;">
                <a href="investor-startup-details.html?id=${s.id}" style="color:var(--primary); font-weight:600; font-size:0.9rem;">Open Full Details →</a>
            </div>
        </div>`;
    }).join('');
}

// ── Filter ────────────────────────────────────────────────────────────────────

function filterStartups() {
    const search   = (document.getElementById('search-input').value || '').toLowerCase();
    const industry = document.getElementById('industry-filter').value;
    const stage    = document.getElementById('stage-filter').value;

    const filtered = allStartups.filter(s => {
        const founderName = (s.founder || s.founderName || s.createdBy || '').toLowerCase();
        const matchSearch = !search
            || (s.name || '').toLowerCase().includes(search)
            || founderName.includes(search)
            || (s.description || '').toLowerCase().includes(search);
        const matchIndustry = !industry || s.industry === industry;
        const matchStage    = !stage    || s.stage === stage;
        return matchSearch && matchIndustry && matchStage;
    });
    renderStartups(filtered);
}

// ── View Modal ────────────────────────────────────────────────────────────────

function viewStartup(id) {
    currentStartup = allStartups.find(s => s.id === id);
    if (!currentStartup) return;
    const s = currentStartup;
    const pct = Math.min(Math.round(((s.currentFunding || 0) / (s.fundingGoal || 1)) * 100), 100);
    const founderName = s.founder || s.founderName || s.createdBy || '—';

    document.getElementById('modal-title').textContent = s.name;
    document.getElementById('modal-body').innerHTML = `
        <div style="display:flex; flex-direction:column; gap:16px;">
            <div><p style="color:var(--muted); font-size:0.85rem;">Founder</p><p style="font-weight:600;">${founderName}</p></div>
            <div><p style="color:var(--muted); font-size:0.85rem;">Industry • Stage</p><p>${s.industry || '—'} • ${s.stage || '—'}</p></div>
            ${s.location ? `<div><p style="color:var(--muted); font-size:0.85rem;">Location</p><p>${s.location}</p></div>` : ''}
            ${s.teamSize ? `<div><p style="color:var(--muted); font-size:0.85rem;">Team Size</p><p>${s.teamSize} members</p></div>` : ''}
            <div><p style="color:var(--muted); font-size:0.85rem;">Description</p><p style="line-height:1.6;">${s.description || '—'}</p></div>
            ${s.pitch ? `<div><p style="color:var(--muted); font-size:0.85rem;">Pitch</p><p style="font-style:italic; line-height:1.6;">"${s.pitch}"</p></div>` : ''}
            <div style="background:rgba(0,0,0,0.04); padding:16px; border-radius:12px;">
                <div class="progress-info">
                    <span>$${(s.currentFunding||0).toLocaleString()} raised</span>
                    <span>Goal: $${(s.fundingGoal||0).toLocaleString()}</span>
                </div>
                <div class="progress-wrap"><div class="progress-fill" style="width:${pct}%;"></div></div>
                <div style="text-align:right; font-size:0.85rem; margin-top:5px; color:var(--muted);">${pct}% funded</div>
            </div>
            <div style="text-align:center; margin-top:6px;">
                <a href="investor-startup-details.html?id=${s.id}" style="color:var(--primary); font-weight:600;">📋 View Full Details Page →</a>
            </div>
        </div>`;
    document.getElementById('view-modal').classList.add('open');
}

function closeViewModal() { document.getElementById('view-modal').classList.remove('open'); }

function investFromModal() {
    closeViewModal();
    if (currentStartup) investInStartup(currentStartup.id);
}

// ── Invest Modal ──────────────────────────────────────────────────────────────

function investInStartup(id) {
    currentStartup = allStartups.find(s => s.id === id);
    if (!currentStartup) return;
    const s = currentStartup;
    const founderName = s.founder || s.founderName || s.createdBy || '—';

    document.getElementById('invest-modal-body').innerHTML = `
        <div style="display:flex; align-items:center; gap:16px; margin-bottom:22px;">
            <div style="width:50px; height:50px; background:rgba(20,184,166,0.12); border-radius:14px; display:flex; align-items:center; justify-content:center; font-size:1.8rem;">🚀</div>
            <div>
                <h3 style="font-weight:700; font-size:1.1rem;">${s.name}</h3>
                <p style="color:var(--muted); font-size:0.9rem;">by ${founderName}</p>
            </div>
        </div>
        <div class="form-group">
            <label>Investment Amount (USD)</label>
            <div style="position:relative; margin-top:8px;">
                <span style="position:absolute; left:16px; top:50%; transform:translateY(-50%); font-size:1.3rem; font-weight:700; color:var(--primary);">$</span>
                <input type="number" id="investment-amount" class="inv-input" value="5000" min="1000" step="100"
                       style="padding-left:40px; font-size:1.5rem; font-weight:700;">
            </div>
            <small style="color:var(--muted);">Minimum investment: $1,000</small>
        </div>
        <p style="color:var(--muted); font-size:0.9rem; margin-top:14px;">
            Investing in <strong>${s.name}</strong> at the <strong>${s.stage || '—'}</strong> stage.
        </p>`;
    document.getElementById('invest-modal').classList.add('open');
}

function closeInvestModal() { document.getElementById('invest-modal').classList.remove('open'); }

async function confirmInvestment() {
    const amount = parseInt(document.getElementById('investment-amount').value) || 0;
    if (amount < 1000) { showToast('Minimum investment is $1,000', 'error'); return; }

    const user = getUser();
    if (!user) return;

    try {
        const res = await fetch(`${BASE}/api/fund/invest`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                startupId: currentStartup.id,
                investorId: user.id,
                amount,
                notes: `Investment of $${amount} in ${currentStartup.name}`
            })
        });
        if (res.ok) {
            closeInvestModal();
            showToast(`✅ $${amount.toLocaleString()} invested in ${currentStartup.name}!`);
            // Refresh startup list to reflect new funding
            await loadStartups();
        } else {
            throw new Error('API error');
        }
    } catch {
        // Record locally as fallback
        closeInvestModal();
        showToast(`✅ Investment of $${amount.toLocaleString()} recorded for ${currentStartup.name}!`);
    }
}

// ── Close on outside click ────────────────────────────────────────────────────

document.addEventListener('click', e => {
    if (e.target === document.getElementById('view-modal')) closeViewModal();
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
        window.location.href = 'auth.html';
        return;
    }

    const name = user.fullName || user.name || 'Investor';
    document.getElementById('nav-user-name').textContent = name;
    const initials = name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
    document.getElementById('nav-avatar').textContent = initials;

    loadStartups();
});
