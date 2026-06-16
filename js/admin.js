// =============================================================================
// admin.js — Admin Module API Layer
// All fetch() calls for Admin dashboard and sub-pages.
// Requires: auth-guard.js loaded first, user session in localStorage.
// =============================================================================

const API = 'http://localhost:8085';

// ── Shared Admin Sidebar HTML ─────────────────────────────────────────────────
window.getAdminSidebar = function(activePage) {
    const pages = [
        { label: '📊 Dashboard',          href: 'dashboard-admin.html' },
        { label: '👥 Manage Users',        href: 'admin-manage-users.html' },
        { label: '🚀 Manage Startups',     href: 'admin-manage-startups.html' },
        { label: '👨‍🏫 Assign Mentors',     href: 'admin-assign-mentors.html' },
        { label: '📝 View Evaluations',    href: 'admin-view-evaluations.html' },
        { label: '💰 Funding Records',     href: 'admin-funding-records.html' },
        { label: '🔔 Notifications',       href: 'admin-notifications.html' },
        { label: '👤 Profile',             href: 'profile.html' },
    ];
    return pages.map(p => `
        <li class="${p.href === activePage ? 'active' : ''}"
            onclick="window.location.href='${p.href}'">
            ${p.label}
        </li>`).join('') +
        `<li onclick="handleLogout()" style="color:var(--error); margin-top:10px;">🚪 Logout</li>`;
};

// ── Dashboard Stats ───────────────────────────────────────────────────────────

window.loadAdminStats = async function() {
    try {
        const [startups, users, funding, evaluations] = await Promise.all([
            fetch(`${API}/api/startups`).then(r => r.json()),
            fetch(`${API}/api/users`).then(r => r.json()),
            fetch(`${API}/api/fund`).then(r => r.json()),
            fetch(`${API}/api/evaluations`).then(r => r.json()),
        ]);

        const pending  = startups.filter(s => s.status === 'pending').length;
        const mentors  = users.filter(u => u.role === 'MENTOR').length;
        const totalFund = funding.reduce((sum, f) => sum + (parseFloat(f.amount) || 0), 0);

        return { startups, users, funding, evaluations, pending, mentors, totalFund };
    } catch (e) {
        console.error('Failed to load admin stats:', e);
        return null;
    }
};

// ── Users ─────────────────────────────────────────────────────────────────────

window.fetchAllUsers = async function() {
    const res = await fetch(`${API}/api/users`);
    if (!res.ok) throw new Error('Failed to load users');
    return res.json();
};

window.fetchUsersByRole = async function(role) {
    const res = await fetch(`${API}/api/users/role/${role}`);
    if (!res.ok) throw new Error(`Failed to load ${role} users`);
    return res.json();
};

window.deactivateUser = async function(id) {
    const res = await fetch(`${API}/api/users/${id}/deactivate`, { method: 'PUT' });
    if (!res.ok) throw new Error('Failed to deactivate user');
    return res.json();
};

window.reactivateUser = async function(id) {
    const res = await fetch(`${API}/api/users/${id}/reactivate`, { method: 'PUT' });
    if (!res.ok) throw new Error('Failed to reactivate user');
    return res.json();
};

// ── Startups ──────────────────────────────────────────────────────────────────

window.fetchAllStartups = async function() {
    const res = await fetch(`${API}/api/startups`);
    if (!res.ok) throw new Error('Failed to load startups');
    return res.json();
};

window.approveStartup = async function(id) {
    const res = await fetch(`${API}/api/startups/${id}/approve`, { method: 'PUT' });
    if (!res.ok) throw new Error('Failed to approve startup');
    return res.json();
};

window.rejectStartup = async function(id, reason = '') {
    const res = await fetch(`${API}/api/startups/${id}/reject`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ reason })
    });
    if (!res.ok) throw new Error('Failed to reject startup');
    return res.json();
};

// ── Mentor Assignments ────────────────────────────────────────────────────────

window.fetchAllAssignments = async function() {
    const res = await fetch(`${API}/api/mentor-assignments`);
    if (!res.ok) throw new Error('Failed to load assignments');
    return res.json();
};

window.assignMentorToStartup = async function(startupId, mentorId, adminId, notes = '') {
    const res = await fetch(`${API}/api/mentor-assignments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ startupId, mentorId, adminId, notes })
    });
    if (!res.ok) throw new Error('Failed to assign mentor');
    return res.json();
};

// ── Evaluations ───────────────────────────────────────────────────────────────

window.fetchAllEvaluations = async function() {
    const res = await fetch(`${API}/api/evaluations`);
    if (!res.ok) throw new Error('Failed to load evaluations');
    return res.json();
};

// ── Funding ───────────────────────────────────────────────────────────────────

window.fetchAllFunding = async function() {
    const res = await fetch(`${API}/api/fund`);
    if (!res.ok) throw new Error('Failed to load funding records');
    return res.json();
};

// ── Notifications ─────────────────────────────────────────────────────────────

window.fetchNotifications = async function(userId) {
    const res = await fetch(`${API}/api/notifications/user/${userId}`);
    if (!res.ok) throw new Error('Failed to load notifications');
    return res.json();
};

window.markNotificationRead = async function(id) {
    await fetch(`${API}/api/notifications/${id}/read`, { method: 'PUT' });
};

// ── Utility Helpers ───────────────────────────────────────────────────────────

window.formatCurrency = function(amount) {
    if (!amount) return '$0';
    return '$' + parseFloat(amount).toLocaleString('en-US', { minimumFractionDigits: 0 });
};

window.formatDate = function(dateStr) {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
};

window.getStatusBadge = function(status) {
    const map = {
        pending:   'status-pending',
        approved:  'status-approved',
        rejected:  'status-rejected',
        active:    'status-approved',
        completed: 'status-approved',
        cancelled: 'status-rejected',
    };
    const cls = map[(status || '').toLowerCase()] || 'status-pending';
    return `<span class="status-badge ${cls}">${status || 'unknown'}</span>`;
};

window.getRoleBadge = function(role) {
    const r = (role || '').toLowerCase();
    return `<span class="role-badge role-${r}">${r}</span>`;
};

window.renderSkeletonRows = function(cols, count = 4) {
    const cells = Array(cols).fill('<td><div class="skeleton" style="height:20px;border-radius:6px;"></div></td>').join('');
    return Array(count).fill(`<tr>${cells}</tr>`).join('');
};
