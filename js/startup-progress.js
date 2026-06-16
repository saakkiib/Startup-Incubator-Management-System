const STAGES = [
  { label: '💡 Idea Stage',         pct: 10, desc: 'Business concept defined and validated.' },
  { label: '📐 Prototype',          pct: 30, desc: 'Working prototype or proof of concept built.' },
  { label: '🧪 MVP',               pct: 50, desc: 'Minimum viable product launched for testing.' },
  { label: '📈 Market Validation',  pct: 70, desc: 'Product tested with real users and feedback collected.' },
  { label: '🚀 Growth Stage',       pct: 90, desc: 'Scaling operations and growing user base.' },
  { label: '🏆 Established',        pct: 100, desc: 'Revenue generating and operationally stable.' },
];

let allStartups = [];
let selectedStartup = null;
let currentProgress = 0;

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  try {
    const res = await fetch(`http://localhost:8085/api/startups/founder/${user.id}`);
    if (!res.ok) throw new Error();

    const startups = await res.json();
    allStartups = startups;

    if (startups.length === 0) {
      document.getElementById('list-view').style.display = 'none';
      document.getElementById('empty-state').style.display = 'block';
      return;
    }

    renderStartupList(startups);
  } catch (err) {
    document.getElementById('startup-list').innerHTML =
      '<p style="color:#ef4444; text-align:center;">Error loading startups. Is the server running?</p>';
  }
});

function renderStartupList(startups) {
  const container = document.getElementById('startup-list');
  container.innerHTML = startups.map(s => {
    const statusClass = `status-${(s.status || 'pending').toLowerCase()}`;
    const statusLabel = capitalize(s.status || 'pending');
    const reasonHtml = s.status === 'rejected' && s.rejectionReason
      ? `<p style="color:#ef4444; font-size:0.85rem; margin-top:6px;">Reason: ${escapeHtml(s.rejectionReason)}</p>`
      : '';

    return `
      <div class="startup-card" style="cursor:pointer;" onclick="showDetail(${s.id})">
        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:10px;">
          <div>
            <h3 style="font-size:1.3rem; margin-bottom:6px;">${escapeHtml(s.name)}</h3>
            <p style="color:#64748b; margin-bottom:10px;">${escapeHtml(s.industry || '')} · ${escapeHtml(s.stage || '')}</p>
          </div>
          <span class="status-badge ${statusClass}">${statusLabel}</span>
        </div>
        <p style="margin-bottom:10px; line-height:1.7;">${escapeHtml(s.description || '').substring(0, 150)}${(s.description || '').length > 150 ? '…' : ''}</p>
        ${s.status === 'approved' ? `
          <div style="margin-bottom:10px;">
            <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
              <span style="font-size:0.85rem; color:#64748b;">Progress</span>
              <span style="font-weight:700; color:var(--primary); font-size:0.9rem;">${s.progress || 0}%</span>
            </div>
            <div class="progress-bar" style="height:8px;">
              <div class="progress-fill" style="width:${s.progress || 0}%;"></div>
            </div>
          </div>
        ` : ''}
        ${reasonHtml}
      </div>`;
  }).join('');
}

function showDetail(id) {
  const startup = allStartups.find(s => s.id === id);
  if (!startup) return;

  selectedStartup = startup.id;

  document.getElementById('list-view').style.display = 'none';
  document.getElementById('detail-view').style.display = 'block';

  document.getElementById('detail-title').textContent = `📈 ${escapeHtml(startup.name)}`;
  document.getElementById('detail-subtitle').textContent =
    `${escapeHtml(startup.industry || '')} · ${escapeHtml(startup.stage || '')}`;

  document.getElementById('approved-section').style.display = 'none';
  document.getElementById('rejected-section').style.display = 'none';
  document.getElementById('pending-section').style.display = 'none';

  if (startup.status === 'approved') {
    document.getElementById('approved-section').style.display = 'block';
    currentProgress = startup.progress || 0;

    document.getElementById('current-pct').textContent = `${currentProgress}%`;
    document.getElementById('current-fill').style.width = `${currentProgress}%`;
    document.getElementById('progress-slider').value = currentProgress;
    document.getElementById('slider-display').textContent = `${currentProgress}%`;
    renderStages(currentProgress);
  } else if (startup.status === 'rejected') {
    document.getElementById('rejected-section').style.display = 'block';
    document.getElementById('rejection-reason').textContent =
      startup.rejectionReason || 'No reason provided.';
  } else {
    document.getElementById('pending-section').style.display = 'block';
  }
}

function showListView() {
  document.getElementById('detail-view').style.display = 'none';
  document.getElementById('list-view').style.display = 'block';
  selectedStartup = null;
}

function renderStages(pct) {
  const container = document.getElementById('stages-list');
  container.innerHTML = STAGES.map(stage => {
    const done    = pct >= stage.pct;
    const current = !done && pct >= stage.pct - 20;
    let cls = '';
    if (done)    cls = 'completed';
    if (current) cls = 'current';

    return `
      <div class="stage ${cls}" onclick="setProgressFromStage(${stage.pct})" style="cursor:pointer;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <strong>${stage.label}</strong>
            <p style="color:#64748b; font-size:0.9rem; margin-top:4px;">${stage.desc}</p>
          </div>
          <div style="display:flex; align-items:center; gap:10px; margin-left:20px;">
            <span style="font-weight:700; color:var(--primary);">${stage.pct}%</span>
            ${done ? '<span style="color:#10b981; font-size:1.3rem;">✅</span>' :
              current ? '<span style="color:#f59e0b; font-size:1.3rem;">⏳</span>' :
              '<span style="color:#cbd5e1; font-size:1.3rem;">○</span>'}
          </div>
        </div>
      </div>`;
  }).join('');
}

function setProgressFromStage(pct) {
  currentProgress = pct;
  document.getElementById('progress-slider').value       = pct;
  document.getElementById('slider-display').textContent  = `${pct}%`;
  renderStages(pct);
}

function updateSliderDisplay(val) {
  const v = parseInt(val);
  document.getElementById('slider-display').textContent = `${v}%`;
  currentProgress = v;
  renderStages(v);
}

async function saveProgress() {
  if (!selectedStartup) {
    showMsg('❌ Please select a startup first.', 'error');
    return;
  }

  const btn = document.getElementById('save-progress-btn');
  btn.disabled = true;
  btn.textContent = 'Saving…';

  try {
    const res = await fetch(`http://localhost:8085/api/startups/${selectedStartup}/progress`, {
      method : 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body   : JSON.stringify({ progress: currentProgress })
    });

    if (res.ok) {
      document.getElementById('current-pct').textContent  = `${currentProgress}%`;
      document.getElementById('current-fill').style.width = `${currentProgress}%`;

      const startup = allStartups.find(s => s.id === selectedStartup);
      if (startup) startup.progress = currentProgress;

      showMsg('✅ Progress updated successfully!', 'success');
    } else {
      showMsg('❌ Failed to save progress. Please try again.', 'error');
    }
  } catch (err) {
    console.error(err);
    showMsg('❌ Connection to server failed.', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = '💾 Save Progress';
  }
}

function showMsg(text, type) {
  const el  = document.getElementById('save-message');
  el.style.display    = 'block';
  el.style.padding    = '14px 18px';
  el.style.borderRadius = '12px';
  el.style.fontWeight   = '600';
  el.style.background = type === 'success' ? 'rgba(16,185,129,0.1)' : 'rgba(239,68,68,0.1)';
  el.style.color      = type === 'success' ? '#10b981' : '#ef4444';
  el.style.border     = `1px solid ${type === 'success' ? '#10b981' : '#ef4444'}`;
  el.textContent = text;
  setTimeout(() => { el.style.display = 'none'; }, 4000);
}

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function escapeHtml(str) {
  const d = document.createElement('div');
  d.appendChild(document.createTextNode(str));
  return d.innerHTML;
}