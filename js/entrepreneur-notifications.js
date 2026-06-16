// =============================================================================
// entrepreneur-notifications.js — Entrepreneur: Notifications Page
// Fetches notifications for the current user and renders them.
// Supports mark-one-read and mark-all-read.
// =============================================================================

let notifications = [];

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);
  await loadNotifications(user.id);
});

async function loadNotifications(userId) {
  const container = document.getElementById('notifications-list');
  try {
    const res = await fetch(`http://localhost:8085/api/notifications/user/${userId}`);
    if (!res.ok) throw new Error('Failed to fetch notifications');

    notifications = await res.json();
    renderNotifications();
  } catch (err) {
    console.error(err);
    container.innerHTML = `<p style="color:#ef4444; text-align:center;">Failed to load notifications.</p>`;
  }
}

function renderNotifications() {
  const container = document.getElementById('notifications-list');

  if (notifications.length === 0) {
    container.innerHTML = `
      <div style="text-align:center; padding:60px 20px; color:#64748b;">
        <div style="font-size:3rem; margin-bottom:12px;">🔔</div>
        <p>No notifications yet. You're all caught up!</p>
      </div>`;
    return;
  }

  const iconMap = {
    STARTUP_APPROVED : '✅',
    STARTUP_REJECTED : '❌',
    FUNDING_RECEIVED : '💰',
    MENTOR_ASSIGNED  : '🎓',
    EVALUATION_DONE  : '📊',
    GENERAL          : '📢'
  };

  container.innerHTML = notifications.map(n => {
    const icon = iconMap[n.type] || '🔔';
    const date = n.createdAt
      ? new Date(n.createdAt).toLocaleDateString('en-US', { year:'numeric', month:'short', day:'numeric', hour:'2-digit', minute:'2-digit' })
      : '';
    const unreadClass = !n.read ? 'unread' : '';

    return `
      <div class="notif-item ${unreadClass}" id="notif-${n.id}">
        <div class="notif-icon">${icon}</div>
        <div class="notif-content">
          <h4>${escapeHtml(n.title || 'Notification')}</h4>
          <p style="color:#64748b; margin-bottom:6px;">${escapeHtml(n.message || '')}</p>
          <div class="notif-time">📅 ${date}</div>
        </div>
        ${!n.read ? `<button class="mark-read" onclick="markRead(${n.id})">Mark Read</button>` : ''}
      </div>`;
  }).join('');
}

async function markRead(notifId) {
  try {
    const res = await fetch(`http://localhost:8085/api/notifications/${notifId}/read`, { method: 'PUT' });
    if (res.ok) {
      notifications = notifications.map(n => n.id === notifId ? { ...n, read: true } : n);
      renderNotifications();
    }
  } catch (err) {
    console.error('Mark read error:', err);
  }
}

async function markAllRead() {
  const user = getCurrentUser();
  if (!user) return;

  try {
    const res = await fetch(`http://localhost:8085/api/notifications/user/${user.id}/read-all`, { method: 'PUT' });
    if (res.ok) {
      notifications = notifications.map(n => ({ ...n, read: true }));
      renderNotifications();
    }
  } catch (err) {
    console.error('Mark all read error:', err);
  }
}

function escapeHtml(str) {
  const d = document.createElement('div');
  d.appendChild(document.createTextNode(str));
  return d.innerHTML;
}
