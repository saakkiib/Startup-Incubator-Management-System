// =============================================================================
// investor-notifications.js — Notifications Page for Investors
// Fetches notifications from the backend and allows marking as read.
// =============================================================================

const BASE = 'http://localhost:8085';
let notifications = [];

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

// ── Load Notifications ────────────────────────────────────────────────────────

async function loadNotifications() {
    const user = getUser();
    try {
        const res = await fetch(`${BASE}/api/notifications/user/${user.id}`);
        if (res.ok) {
            notifications = await res.json();
        } else throw new Error();
    } catch {
        notifications = [
            { id: 1, title: 'Investment Opportunity',       message: "Your investment in 'EcoPack Solutions' has been approved. $15,000 transferred.",           time: '2 hours ago',  type: 'funding',   read: false, fullDetails: 'Investment request for EcoPack Solutions successfully processed.' },
            { id: 2, title: 'New Startup Match',            message: 'A new AI-powered HealthTech startup matches your investment criteria.',                    time: '5 hours ago',  type: 'startup',   read: false, fullDetails: 'Startup: MediScan AI • Industry: HealthTech • Match Score: 94%' },
            { id: 3, title: 'Mentor Feedback Received',     message: "Mentor reviewed 'AI Health Diagnostics' — Overall Score: 89/100.",                       time: 'Yesterday',    type: 'evaluation',read: true,  fullDetails: 'Technical: 92 | Market: 85 | Financial: 88' },
            { id: 4, title: 'Funding Milestone Achieved',   message: "'GreenVolt Energy' reached 60% of their funding goal.",                                  time: '2 days ago',   type: 'funding',   read: true,  fullDetails: 'Current Funding: $72,000 out of $120,000 target.' },
            { id: 5, title: 'Portfolio Update',             message: "Your investment in 'SolarNest' has shown 18% growth this month.",                        time: '3 days ago',   type: 'portfolio', read: false, fullDetails: 'Positive performance update from SolarNest. Equity value increased.' },
            { id: 6, title: 'System Alert',                 message: 'Your profile verification is pending. Complete KYC to unlock more features.',            time: '1 week ago',   type: 'system',    read: true,  fullDetails: 'Please complete your KYC documents to continue investing without restrictions.' },
            { id: 7, title: 'New Investor Opportunity',     message: "Exclusive round opened for 'Quantum Logistics'.",                                        time: '1 week ago',   type: 'startup',   read: true,  fullDetails: 'Seed Round • $2.5M • Limited slots for early investors.' },
            { id: 8, title: 'Dividend Received',            message: "You received $2,800 dividend from 'FinFlow Payments'.",                                  time: '4 days ago',   type: 'funding',   read: false, fullDetails: 'Dividend payment processed successfully.' },
            { id: 9, title: 'Startup Exited',               message: "'VitaHealth' has successfully exited. Great return!",                                   time: '2 weeks ago',  type: 'portfolio', read: true,  fullDetails: 'Your investment returned 3.2×. Congratulations!' },
            { id: 10, title: 'Meeting Reminder',            message: "Investor meetup with 'AquaPure Tech' tomorrow at 11 AM.",                               time: 'Yesterday',    type: 'system',    read: false, fullDetails: 'Zoom link has been sent to your email.' }
        ];
    }
    renderNotifications();
}

function typeIcon(type) {
    return type === 'funding' ? '💰' : type === 'evaluation' ? '📝' : type === 'startup' ? '🚀' : type === 'portfolio' ? '📊' : '🔔';
}

// ── Render ────────────────────────────────────────────────────────────────────

function renderNotifications() {
    const container = document.getElementById('notifications-list');
    if (!notifications.length) {
        container.innerHTML = `<p style="text-align:center; padding:60px; color:var(--muted);">No notifications yet.</p>`;
        return;
    }
    container.innerHTML = notifications.map(n => `
        <div class="notif-card ${n.read ? '' : 'unread'}" data-id="${n.id}">
            <div class="notif-icon">${typeIcon(n.type)}</div>
            <div class="notif-content">
                <h4>${n.title}</h4>
                <p>${n.message}</p>
                <div class="notif-time">${n.time || ''}</div>
            </div>
            <div style="display:flex; flex-direction:column; gap:8px; align-items:flex-end; flex-shrink:0;">
                ${!n.read ? `<button class="btn btn-primary btn-sm" onclick="markAsRead(${n.id}); event.stopPropagation();">Mark Read</button>` : ''}
                <button class="btn btn-ghost btn-sm" onclick="viewNotification(${n.id}); event.stopPropagation();">View</button>
            </div>
        </div>`).join('');
}

// ── Mark as Read ──────────────────────────────────────────────────────────────

async function markAsRead(id) {
    notifications = notifications.map(n => n.id === id ? { ...n, read: true } : n);
    renderNotifications();
    showToast('Marked as read');

    // Try backend
    try {
        await fetch(`${BASE}/api/notifications/${id}/read`, { method: 'PUT' });
    } catch {}
}

async function markAllAsRead() {
    const user = getUser();
    notifications.forEach(n => n.read = true);
    renderNotifications();
    showToast('All notifications marked as read');

    try {
        await fetch(`${BASE}/api/notifications/user/${user.id}/read-all`, { method: 'PUT' });
    } catch {}
}

// ── View Modal ────────────────────────────────────────────────────────────────

function viewNotification(id) {
    const n = notifications.find(x => x.id === id);
    if (!n) return;
    document.getElementById('modal-title').textContent = n.title;
    document.getElementById('modal-body').innerHTML = `
        <p><strong>${n.message}</strong></p>
        <p style="margin-top:15px;">${n.fullDetails || ''}</p>
        <p style="margin-top:18px; font-size:0.88rem; color:var(--muted);">${n.time || ''}</p>`;
    document.getElementById('notification-modal').classList.add('open');

    // Auto-mark as read
    if (!n.read) markAsRead(id);
}

function closeModal() { document.getElementById('notification-modal').classList.remove('open'); }

document.addEventListener('click', e => {
    if (e.target === document.getElementById('notification-modal')) closeModal();
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

    loadNotifications();
});
