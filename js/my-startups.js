// =============================================================================
// my-startups.js — Entrepreneur: My Startups List
// Fetches all startups belonging to the logged-in founder and renders them.
// =============================================================================

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  const container = document.getElementById('startups-list');

  try {
    const res = await fetch(`http://localhost:8085/api/startups/founder/${user.id}`);
    if (!res.ok) throw new Error('Failed to fetch startups');

    const startups = await res.json();

    if (startups.length === 0) {
      container.innerHTML = `
        <div style="text-align:center; padding: 60px 20px; color: #64748b;">
          <div style="font-size: 3rem; margin-bottom: 16px;">🚀</div>
          <h3 style="margin-bottom: 10px;">No startups yet</h3>
          <p>Submit your startup idea to get started!</p>
          <button onclick="window.location.href='submit-startup.html'"
            style="margin-top: 20px; background: var(--primary); color: white; border: none;
                   padding: 14px 28px; border-radius: 12px; cursor: pointer; font-weight: 600;">
            + Submit Startup
          </button>
        </div>`;
      return;
    }

    container.innerHTML = startups.map(startup => {
      const st = startup.status || 'pending';
      const statusClass = 'status-' + st;
      const statusLabel = capitalize(st);
      const progress    = startup.progress || 0;
      const goal        = startup.fundingGoal   || 0;
      const current     = startup.currentFunding || 0;
      const pct         = goal > 0 ? Math.min(100, Math.round((current / goal) * 100)) : 0;

      return `
        <div class="startup-card">
          <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:10px;">
            <div>
              <h3 style="font-size:1.3rem; margin-bottom:6px;">${escapeHtml(startup.name)}</h3>
              <p style="color:#64748b; margin-bottom:10px;">${escapeHtml(startup.industry || '')} · ${escapeHtml(startup.stage || '')}</p>
            </div>
            <span class="status-badge ${statusClass}">${statusLabel}</span>
          </div>

          <p style="margin-bottom:16px; line-height:1.7;">${escapeHtml(startup.description || '')}</p>

          <!-- Progress -->
          <div style="margin-bottom: 14px;">
            <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
              <span style="font-size:0.9rem; color:#64748b;">Overall Progress</span>
              <span style="font-weight:700; color:var(--primary);">${progress}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" style="width:${progress}%;"></div>
            </div>
          </div>

          <!-- Funding -->
          <div style="margin-bottom:18px;">
            <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
              <span style="font-size:0.9rem; color:#64748b;">Funding Raised</span>
              <span style="font-weight:700; color:var(--primary);">$${current.toLocaleString()} / $${goal.toLocaleString()}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" style="width:${pct}%; background:#10b981;"></div>
            </div>
          </div>

          <div style="display:flex; gap:10px; flex-wrap:wrap;">
            <button class="action-btn" onclick="window.location.href='startup-details.html?id=${startup.id}'">
              View Details
            </button>
            <button class="action-btn" style="background:rgba(20,184,166,0.1);color:var(--primary);"
              onclick="window.location.href='upload-pitch.html?id=${startup.id}'">
              📤 Upload Pitch
            </button>
            <button class="action-btn" style="background:rgba(239,68,68,0.1);color:#ef4444;"
              onclick="deleteStartup(${startup.id}, '${escapeHtml(startup.name)}')">
              🗑 Delete
            </button>
          </div>
        </div>`;
    }).join('');

  } catch (err) {
    console.error('Error loading startups:', err);
    container.innerHTML = `<p style="color:#ef4444; text-align:center;">Failed to load startups. Is the server running?</p>`;
  }
});

async function deleteStartup(id, name) {
  if (!confirm(`Are you sure you want to delete "${name}"?\n\nThis action cannot be undone.`)) return;

  try {
    const res = await fetch(`http://localhost:8085/api/startups/${id}`, {
      method: 'DELETE'
    });
    if (!res.ok) throw new Error('Failed to delete startup');

    showToast(`"${name}" has been deleted.`, 'success');
    setTimeout(() => window.location.reload(), 1500);
  } catch (err) {
    console.error('Error deleting startup:', err);
    showToast('Failed to delete startup. Is the server running?', 'error');
  }
}

function showToast(msg, type) {
  const existing = document.querySelector('.toast-notification');
  if (existing) existing.remove();

  const toast = document.createElement('div');
  toast.className = 'toast-notification';
  toast.style.cssText = `
    position:fixed; top:20px; right:20px; z-index:9999;
    padding:16px 24px; border-radius:12px; font-weight:600;
    background:${type === 'success' ? 'rgba(16,185,129,0.95)' : 'rgba(239,68,68,0.95)'};
    color:white; box-shadow:0 8px 30px rgba(0,0,0,0.2);
    animation:slideIn 0.3s ease;
    font-size:0.95rem;
  `;
  toast.textContent = msg;
  document.body.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}
