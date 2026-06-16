// =============================================================================
// mentor.js — Mentor Module API Layer
// All fetch() calls for Mentor dashboard and sub-pages.
// Requires: auth-guard.js loaded first, user session in localStorage.
// =============================================================================

const MENTOR_API = 'http://localhost:8085';

// ── Shared Mentor Sidebar HTML ────────────────────────────────────────────────
window.getMentorSidebar = function(activePage) {
    const pages = [
        { label: '📊 Dashboard',             href: 'dashboard-mentor.html' },
        { label: '📋 Assigned Startups',     href: 'mentor-assigned.html' },
        { label: '🔍 Startup Details',       href: 'mentor-startup-details.html' },
        { label: '📝 Evaluate Startup',      href: 'mentor-evaluate.html' },
        { label: '📑 Submitted Evaluations', href: 'mentor-submitted-evaluations.html' },
        { label: '🔔 Notifications',         href: 'mentor-notifications.html' },
        { label: '👤 Profile',               href: 'mentor-profile.html' },
    ];
    return pages.map(p => `
        <li class="${p.href === activePage ? 'active' : ''}"
            onclick="window.location.href='${p.href}'">
            ${p.label}
        </li>`).join('') +
        `<li onclick="handleLogout()" style="color:var(--error); margin-top:10px;">🚪 Logout</li>`;
};

// ── Mentor Dashboard Stats ────────────────────────────────────────────────────

window.loadMentorStats = async function(mentorId) {
    try {
        const [assignments, evaluations, notifications] = await Promise.all([
            fetch(`${MENTOR_API}/api/mentor-assignments/mentor/${mentorId}`).then(r => r.json()),
            fetch(`${MENTOR_API}/api/evaluations/mentor/${mentorId}`).then(r => r.json()),
            fetch(`${MENTOR_API}/api/notifications/user/${mentorId}`).then(r => r.json()),
        ]);
        const unread = notifications.filter(n => !n.read).length;
        return { assignments, evaluations, notifications, unread };
    } catch (e) {
        console.error('Failed to load mentor stats:', e);
        return null;
    }
};

// ── Assigned Startups ─────────────────────────────────────────────────────────

window.fetchMentorAssignments = async function(mentorId) {
    const res = await fetch(`${MENTOR_API}/api/mentor-assignments/mentor/${mentorId}`);
    if (!res.ok) throw new Error('Failed to load assigned startups');
    return res.json();
};

// ── Startup Details ───────────────────────────────────────────────────────────

window.fetchStartupDetails = async function(startupId) {
    const res = await fetch(`${MENTOR_API}/api/startups/${startupId}`);
    if (!res.ok) throw new Error('Startup not found');
    return res.json();
};

window.fetchStartupEvaluations = async function(startupId) {
    const res = await fetch(`${MENTOR_API}/api/evaluations/startup/${startupId}`);
    if (!res.ok) throw new Error('Failed to load evaluations');
    return res.json();
};

// ── Submit Evaluation ─────────────────────────────────────────────────────────

window.submitEvaluation = async function(payload) {
    const res = await fetch(`${MENTOR_API}/api/evaluations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error('Failed to submit evaluation');
    return res.json();
};

// ── Submitted Evaluations ─────────────────────────────────────────────────────

window.fetchMentorEvaluations = async function(mentorId) {
    const res = await fetch(`${MENTOR_API}/api/evaluations/mentor/${mentorId}`);
    if (!res.ok) throw new Error('Failed to load evaluations');
    return res.json();
};

// ── Notifications ─────────────────────────────────────────────────────────────

window.fetchMentorNotifications = async function(userId) {
    const res = await fetch(`${MENTOR_API}/api/notifications/user/${userId}`);
    if (!res.ok) throw new Error('Failed to load notifications');
    return res.json();
};

window.markNotifRead = async function(id) {
    await fetch(`${MENTOR_API}/api/notifications/${id}/read`, { method: 'PUT' });
};

// ── Utilities ─────────────────────────────────────────────────────────────────

window.mentorFormatDate = function(dateStr) {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
};

window.mentorGetStatusBadge = function(status) {
    const map = {
        pending:  'status-pending',
        approved: 'status-approved',
        rejected: 'status-rejected',
        active:   'status-approved',
    };
    const cls = map[(status || '').toLowerCase()] || 'status-pending';
    return `<span class="status-badge ${cls}">${status || 'unknown'}</span>`;
};

window.mentorRenderSkeletonRows = function(cols, count = 3) {
    const cells = Array(cols).fill('<td><div class="skeleton" style="height:18px;border-radius:6px;"></div></td>').join('');
    return Array(count).fill(`<tr>${cells}</tr>`).join('');
};

// Save selected startup to sessionStorage for cross-page use
window.selectStartup = function(startupId, startupName) {
    sessionStorage.setItem('selectedStartupId', startupId);
    sessionStorage.setItem('selectedStartupName', startupName);
};

window.getSelectedStartup = function() {
    return {
        id:   sessionStorage.getItem('selectedStartupId'),
        name: sessionStorage.getItem('selectedStartupName'),
    };
};
