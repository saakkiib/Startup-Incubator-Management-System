// =============================================================================
// auth-guard.js — Role-Based Access Control
// Call requireRole('ADMIN') or requireRole('MENTOR') at the top of each page.
// Reads session from localStorage, redirects to auth.html if invalid.
// =============================================================================

/**
 * Enforce that only users with the given role can access the current page.
 * @param {string} requiredRole - 'ADMIN' | 'MENTOR' | 'STUDENT' | 'INVESTOR'
 * @returns {object|null} The user object if access is granted, or null.
 */
window.requireRole = function(requiredRole) {
    const raw = localStorage.getItem('user');
    if (!raw) {
        window.location.href = 'auth.html';
        return null;
    }
    try {
        const user = JSON.parse(raw);
        if (!user || !user.role) {
            window.location.href = 'auth.html';
            return null;
        }
        if (user.role.toUpperCase() !== requiredRole.toUpperCase()) {
            // Wrong role — redirect to their own dashboard
            const roleMap = {
                ADMIN: 'dashboard-admin.html',
                MENTOR: 'dashboard-mentor.html',
                STUDENT: 'dashboard-entrepreneur.html',
                INVESTOR: 'dashboard-investor.html'
            };
            window.location.href = roleMap[user.role.toUpperCase()] || 'index.html';
            return null;
        }
        return user;
    } catch (e) {
        localStorage.removeItem('user');
        window.location.href = 'auth.html';
        return null;
    }
};

/**
 * Get the current logged-in user from localStorage.
 * Returns null if not logged in.
 */
window.getCurrentUser = function() {
    try {
        return JSON.parse(localStorage.getItem('user'));
    } catch (e) {
        return null;
    }
};

/**
 * Populate the navbar avatar and name from the current user session.
 * @param {object} user - The user object from localStorage
 */
window.populateNavbar = function(user) {
    const nameEl   = document.getElementById('nav-user-name');
    const avatarEl = document.getElementById('nav-avatar');
    if (nameEl)   nameEl.textContent   = user.fullName || user.username || 'User';
    if (avatarEl) {
        if (user.photo) {
            avatarEl.innerHTML = `<img src="${user.photo}" style="width:100%; height:100%; object-fit:cover; border-radius:50%;" />`;
            avatarEl.style.padding = '0';
            avatarEl.style.overflow = 'hidden';
        } else {
            avatarEl.textContent = (user.fullName || user.username || 'U').substring(0, 2).toUpperCase();
        }
    }
    initNotificationBadge(user);
};

let notifBadgeInterval = null;

function initNotificationBadge(user) {
    const right = document.querySelector('.navbar .right');
    if (!right) return;

    if (document.getElementById('notif-bell-wrapper')) return;
    if (notifBadgeInterval) clearInterval(notifBadgeInterval);

    const rolePage = {
        STUDENT: 'notifications.html',
        MENTOR: 'mentor-notifications.html',
        ADMIN: 'admin-notifications.html',
        INVESTOR: 'notifications.html'
    };
    const notifPage = rolePage[user.role] || 'notifications.html';

    const bell = document.createElement('div');
    bell.id = 'notif-bell-wrapper';
    bell.style.cssText = 'position:relative;cursor:pointer;font-size:1.3rem;margin-right:8px;';
    bell.onclick = () => window.location.href = notifPage;
    bell.innerHTML = `<span style="font-size:1.3rem;">🔔</span><span id="notif-badge" style="display:none;position:absolute;top:-6px;right:-8px;background:#ef4444;color:white;font-size:0.7rem;font-weight:700;min-width:18px;height:18px;border-radius:9px;align-items:center;justify-content:center;padding:0 4px;box-shadow:0 2px 6px rgba(239,68,68,0.4);border:2px solid var(--bg,white);">0</span>`;

    const themeBtn = right.querySelector('#theme-toggle');
    if (themeBtn) {
        right.insertBefore(bell, themeBtn.nextSibling);
    } else {
        right.insertBefore(bell, right.firstChild);
    }

    fetchNotifCount(user.id);
    notifBadgeInterval = setInterval(() => fetchNotifCount(user.id), 60000);

    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) fetchNotifCount(user.id);
    });
}

async function fetchNotifCount(userId) {
    try {
        const res = await fetch(`http://localhost:8085/api/notifications/user/${userId}`);
        if (!res.ok) return;
        const notifs = await res.json();
        const unread = notifs.filter(n => !n.read).length;
        const badge = document.getElementById('notif-badge');
        if (!badge) return;
        if (unread > 0) {
            badge.textContent = unread > 99 ? '99+' : unread;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    } catch (_) {}
}

/**
 * Handle logout — clear session and redirect to home.
 */
window.handleLogout = function() {
    if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    }
};
