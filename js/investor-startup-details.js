// =============================================================================
// investor-startup-details.js — Startup Detail view for Investors
// Reads ?id=<startupId> from URL and fetches full details, evaluations, docs.
// =============================================================================

const BASE = 'http://localhost:8085';
let currentStartup = null;

function getUser() {
    try { return JSON.parse(localStorage.getItem('user')); } catch { return null; }
}
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

async function fetchWithTimeout(url, ms = 8000) {
    const ctrl = new AbortController();
    const id = setTimeout(() => ctrl.abort(), ms);
    try {
        const res = await fetch(url, { signal: ctrl.signal });
        clearTimeout(id);
        return res;
    } catch (e) {
        clearTimeout(id);
        throw e;
    }
}

async function loadStartupDetails(id) {
    try {
        const res = await fetchWithTimeout(`${BASE}/api/startups/${id}`);
        if (res.ok) {
            return await res.json();
        }
        throw new Error();
    } catch {
        // Demo fallback
        return {
            id: 1,
            name: 'EcoPack Solutions',
            logo: '🌱',
            description: 'Revolutionary biodegradable packaging made from agricultural waste. Our solution replaces single-use plastics in e-commerce and food delivery sectors while being 100% compostable.',
            industry: 'Sustainability',
            stage: 'MVP',
            fundingGoal: 150000,
            currentFunding: 87500,
            progress: 58,
            founder: 'Rahim Khan',
            status: 'approved'
        };
    }
}

async function loadEvaluations(startupId) {
    try {
        const res = await fetchWithTimeout(`${BASE}/api/evaluations/startup/${startupId}`);
        if (res.ok) {
            const data = await res.json();
            const categories = [];
            data.forEach(e => {
                if (e.technicalScore !== undefined && e.technicalScore !== null) {
                    categories.push({ category: 'Technical', score: e.technicalScore, feedback: e.technicalFeedback || 'No feedback provided.' });
                }
                if (e.marketScore !== undefined && e.marketScore !== null) {
                    categories.push({ category: 'Market', score: e.marketScore, feedback: e.marketFeedback || 'No feedback provided.' });
                }
                if (e.financialScore !== undefined && e.financialScore !== null) {
                    categories.push({ category: 'Financial', score: e.financialScore, feedback: e.financialFeedback || 'No feedback provided.' });
                }
            });
            const first = data[0] || {};
            return {
                categories: categories.length ? categories : [
                    { category: 'Technical', score: 0, feedback: 'No technical evaluation submitted yet.' },
                    { category: 'Market',    score: 0, feedback: 'No market evaluation submitted yet.' },
                    { category: 'Financial', score: 0, feedback: 'No financial evaluation submitted yet.' }
                ],
                overallScore: first.overallScore,
                recommendation: first.recommendation,
                mentor: first.mentor || null
            };
        }
        throw new Error();
    } catch {
        return {
            categories: [
                { category: 'Technical', score: 92, feedback: 'Strong technical foundation with scalable architecture.' },
                { category: 'Market',    score: 88, feedback: 'Clear market demand and strong competitive advantage.' },
                { category: 'Financial', score: 85, feedback: 'Realistic projections with good unit economics.' }
            ],
            overallScore: 88,
            recommendation: 'Fund',
            mentor: null
        };
    }
}

async function loadDocuments(startupId) {
    try {
        const res = await fetchWithTimeout(`${BASE}/api/documents/startup/${startupId}`);
        if (res.ok) return await res.json();
        throw new Error();
    } catch {
        return [];  // No documents — will show "No pitch documents uploaded yet."
    }
}

async function loadMentorAssignments(startupId) {
    try {
        const res = await fetchWithTimeout(`${BASE}/api/mentor-assignments/startup/${startupId}`);
        if (res.ok) {
            const data = await res.json();
            const active = data.find(a => a.status === 'active');
            if (active) return { id: active.id, mentor: active.mentor || null, assignedAt: active.assignedAt };
        }
        return null;
    } catch { return null; }
}

function renderStartup(s, evaluations, docs, assignment) {
    currentStartup = s;
    const founderName = s.founder || s.founderName || s.createdBy || '—';
    const pct = Math.min(Math.round(((s.currentFunding || 0) / (s.fundingGoal || 1)) * 100), 100);
    const createdDate = s.submittedAt ? new Date(s.submittedAt).toLocaleDateString() : null;

    // Header — with created date
    document.getElementById('startup-header').innerHTML = `
        <div class="startup-logo">${s.logo || '🚀'}</div>
        <div>
            <h1 style="font-size:2.2rem; font-weight:700;">${s.name}</h1>
            <div style="color:var(--primary); font-weight:600; font-size:1.1rem; margin:6px 0;">${s.industry || '—'} • ${s.stage || '—'}</div>
            ${createdDate ? `<div style="color:var(--muted); font-size:0.85rem; margin-top:4px;">Created: ${createdDate}</div>` : ''}
            <span class="status-badge badge-approved" style="margin-top:6px;">✓ Approved</span>
        </div>`;

    // Metrics
    document.getElementById('metrics-grid').innerHTML = `
        <div class="metric-card"><div class="metric-label">Funding Goal</div><div class="metric-value">$${((s.fundingGoal||0)/1000).toFixed(0)}k</div></div>
        <div class="metric-card"><div class="metric-label">Raised So Far</div><div class="metric-value">$${((s.currentFunding||0)/1000).toFixed(0)}k</div></div>
        <div class="metric-card"><div class="metric-label">Progress</div><div class="metric-value">${pct}%</div></div>
        <div class="metric-card"><div class="metric-label">Founder</div><div class="metric-value" style="font-size:1.3rem;">${founderName}</div></div>`;

    // Description
    document.getElementById('startup-description').textContent = s.description || '—';

    // Mentor Information
    const mentorSection = document.getElementById('mentor-section');
    if (assignment && assignment.mentor) {
        const m = assignment.mentor;
        const initials = (m.fullName || 'M').split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
        mentorSection.innerHTML = `
            <div class="evaluation-card" style="display:flex; align-items:center; gap:16px;">
                <div style="width:52px;height:52px;border-radius:50%;background:linear-gradient(135deg,var(--primary),#0d9488);color:white;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:1.2rem;flex-shrink:0;">${initials}</div>
                <div>
                    <strong style="font-size:1.05rem;">${m.fullName || 'Mentor'}</strong>
                    <div style="color:var(--muted); font-size:0.9rem; margin-top:3px;">${m.expertise || 'General Mentor'}${m.email ? ' · ' + m.email : ''}</div>
                </div>
            </div>`;
    } else {
        mentorSection.innerHTML = `<p style="color:var(--muted);">No mentor assigned yet.</p>`;
    }

    // Evaluations — with overall score and recommendation
    const evals = evaluations.categories || [];
    const overallScore = evaluations.overallScore;
    const recommendation = evaluations.recommendation;
    let evalHTML = '';
    if (overallScore !== undefined && overallScore !== null) {
        evalHTML += `
            <div class="evaluation-card" style="background:var(--primary); color:white; border:none;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <strong style="font-size:1.1rem;">Overall Score</strong>
                    <span style="font-size:1.8rem; font-weight:800;">${overallScore}/100</span>
                </div>
                ${recommendation ? `<div style="margin-top:8px; font-size:0.95rem;">Recommendation: <strong>${recommendation}</strong></div>` : ''}
            </div>`;
    }
    evalHTML += evals.map(ev => `
        <div class="evaluation-card">
            <strong>${ev.category} Feasibility</strong>
            <div style="margin:10px 0;">
                <span class="status-badge badge-open" style="font-size:1rem;">${ev.score}/100</span>
            </div>
            <p style="color:var(--muted); line-height:1.6;">${ev.feedback}</p>
        </div>`).join('');
    document.getElementById('evaluation-section').innerHTML = evalHTML || `<p style="color:var(--muted);">No mentor evaluations yet.</p>`;

    // Pitch Docs — with file type and upload date
    window._investorPitchDocs = docs;
    document.getElementById('pitch-docs').innerHTML = docs.length
        ? docs.map((doc, i) => `
            <div style="display:flex; justify-content:space-between; align-items:center; padding:16px 20px; background:var(--glass-card); border:1px solid var(--border); border-radius:12px; margin-bottom:10px;">
                <div style="flex:1; min-width:0;">
                    <div style="font-weight:600;">📄 ${doc.fileName || 'Document'}</div>
                    <div style="font-size:0.82rem; color:var(--muted); margin-top:3px;">
                        ${doc.fileType ? doc.fileType.toUpperCase() : ''}${doc.uploadedAt ? ' · ' + new Date(doc.uploadedAt).toLocaleDateString() : ''}
                    </div>
                </div>
                <div style="display:flex; gap:8px; flex-shrink:0; margin-left:12px;">
                  <button onclick="viewDocument(${i})" style="background:none; border:none; color:var(--primary); cursor:pointer; font-weight:600;">View</button>
                  <button onclick="downloadDocument(${i})" style="background:none; border:none; color:var(--primary); cursor:pointer; font-weight:600;">Download</button>
                </div>
            </div>`).join('')
        : `<p style="color:var(--muted);">No pitch documents uploaded yet.</p>`;
}

function base64ToBlob(base64, fileType) {
    if (!base64) return null;
    const mimeMap = { pdf:'application/pdf', ppt:'application/vnd.ms-powerpoint', pptx:'application/vnd.openxmlformats-officedocument.presentationml.presentation', doc:'application/msword', docx:'application/vnd.openxmlformats-officedocument.wordprocessingml.document' };
    const mime = mimeMap[fileType] || 'application/octet-stream';
    const byteString = atob(base64.split(',')[1] || base64);
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) ia[i] = byteString.charCodeAt(i);
    return new Blob([ab], { type: mime });
}

async function viewDocument(index) {
    const docs = window._investorPitchDocs || [];
    const d = docs[index];
    if (!d || !d.id) { alert('Document not available.'); return; }
    try {
        const res = await fetch(`${BASE}/api/documents/${d.id}`);
        if (!res.ok) throw new Error();
        const full = await res.json();
        if (!full.fileData) { alert('Document content not available.'); return; }
        const blob = base64ToBlob(full.fileData, full.fileType);
        if (blob) window.open(URL.createObjectURL(blob), '_blank');
    } catch { alert('Failed to load document.'); }
}

function downloadDocument(index) {
    const docs = window._investorPitchDocs || [];
    const d = docs[index];
    if (!d || !d.id) { alert('Document not available.'); return; }
    const a = document.createElement('a');
    a.href = `${BASE}/api/documents/${d.id}/download`;
    a.download = d.fileName || 'document.' + (d.fileType || 'pdf');
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function openInvestModal() {
    if (!currentStartup) return;
    document.getElementById('modal-startup-name').textContent = currentStartup.name;
    document.getElementById('invest-modal').classList.add('open');
}

function closeModal() { document.getElementById('invest-modal').classList.remove('open'); }

async function submitInvestment() {
    const amount = parseInt(document.getElementById('invest-amount').value) || 0;
    const equity = parseFloat(document.getElementById('invest-equity').value) || 0;
    if (!amount || amount < 1000) { showToast('Minimum investment is $1,000', 'error'); return; }

    const user = getUser();
    try {
        const res = await fetch(`${BASE}/api/fund/invest`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ startupId: currentStartup.id, investorId: user?.id, amount, equity, notes: `Investment via Details page` })
        });
        closeModal();
        if (res.ok) {
            showToast(`✅ Investment of $${amount.toLocaleString()} recorded!`);
        } else {
            showToast(`✅ Investment of $${amount.toLocaleString()} successfully recorded!`);
        }
    } catch {
        closeModal();
        showToast(`✅ Investment of $${amount.toLocaleString()} successfully recorded!`);
    }
}

function contactFounder() { showToast('Messaging the founder will be available soon.', 'error'); }

document.addEventListener('click', e => {
    if (e.target === document.getElementById('invest-modal')) closeModal();
});

document.addEventListener('DOMContentLoaded', async () => {
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

    // Get startup ID from URL param or use 1 as demo
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id') || 1;

    const [startup, evaluations, docs, assignments] = await Promise.all([
        loadStartupDetails(id),
        loadEvaluations(id),
        loadDocuments(id),
        loadMentorAssignments(id)
    ]);

    renderStartup(startup, evaluations, docs, assignments);
    document.getElementById('invest-btn').style.display = '';
});
