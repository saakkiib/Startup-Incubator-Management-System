document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  const params    = new URLSearchParams(window.location.search);
  const startupId = params.get('id');
  const container = document.getElementById('startup-details-content');

  if (!startupId) {
    container.innerHTML = `<p style="color:#ef4444;">No startup ID provided. <a href="my-startups.html">Go back</a>.</p>`;
    return;
  }

  try {
    const res = await fetch(`http://localhost:8085/api/startups/${startupId}`);
    if (!res.ok) throw new Error('Startup not found');
    const s = await res.json();

    const statusClass = `status-${(s.status || 'pending').toLowerCase()}`;
    const progress    = s.progress || 0;
    const goal        = s.fundingGoal    || 0;
    const current     = s.currentFunding || 0;
    const pct         = goal > 0 ? Math.min(100, Math.round((current / goal) * 100)) : 0;

    let fundingHTML = '<p style="color:#64748b;">No funding records yet.</p>';
    try {
      const fRes = await fetch(`http://localhost:8085/api/fund/startup/${startupId}`);
      if (fRes.ok) {
        const fundings = await fRes.json();
        if (fundings.length > 0) {
          fundingHTML = fundings.map(f => `
            <div class="list-item">
              <div>
                <strong>$${(f.amount || 0).toLocaleString()}</strong>
                <div style="font-size:0.85rem;color:#64748b;">
                  ${f.investor ? f.investor.fullName || 'Anonymous' : 'Anonymous'} ·
                  ${f.fundedAt ? new Date(f.fundedAt).toLocaleDateString() : ''}
                </div>
              </div>
              <span class="status-badge status-approved">Received</span>
            </div>`).join('');
        }
      }
    } catch (_) {}

    let mentorHTML = '<p style="color:#64748b;">No mentor assigned yet.</p>';
    let mentorData = null;
    try {
      const mRes = await fetch(`http://localhost:8085/api/mentor-assignments/startup/${startupId}`);
      if (mRes.ok) {
        const assignments = await mRes.json();
        const active = assignments.filter(a => a.status === 'active');
        if (active.length > 0) {
          mentorData = active[0].mentor;
          mentorHTML = `
            <div class="mentor-card" style="cursor:pointer;" onclick="showMentorModal(${mentorData.id})">
              <div style="display:flex; align-items:center; gap:14px;">
                <div style="width:50px;height:50px;border-radius:50%;
                     background:linear-gradient(135deg,var(--primary),#0d9488);
                     color:white;display:flex;align-items:center;justify-content:center;
                     font-weight:700;font-size:1.2rem;flex-shrink:0;">
                  ${getInitials(mentorData.fullName || 'Mentor')}
                </div>
                <div>
                  <strong style="font-size:1.05rem;">${escapeHtml(mentorData.fullName || 'Mentor')}</strong>
                  <div style="font-size:0.85rem;color:#64748b;">${escapeHtml(mentorData.email || '')} ${mentorData.phone ? '· ' + escapeHtml(mentorData.phone) : ''}</div>
                  <div style="font-size:0.85rem;color:#64748b;margin-top:2px;">Click to view full profile →</div>
                </div>
              </div>
            </div>`;
        }
      }
    } catch (_) {}

    let pitchDocsHTML = '<p style="color:#64748b;">No pitch documents uploaded yet.</p>';
    try {
      const pRes = await fetch(`http://localhost:8085/api/documents/startup/${startupId}`);
      if (pRes.ok) {
        const docs = await pRes.json();
        if (docs.length > 0) {
          pitchDocsHTML = docs.map((d, i) => `
            <div style="display:flex; justify-content:space-between; align-items:center; padding:14px 18px; background:var(--bg2,#f8fafc); border:1px solid var(--border); border-radius:10px; margin-bottom:8px;">
              <div style="flex:1; min-width:0;">
                <strong style="font-size:0.95rem;">📄 ${escapeHtml(d.fileName || 'Document')}</strong>
                <div style="font-size:0.85rem;color:#64748b;margin-top:2px;">
                  ${d.fileType ? d.fileType.toUpperCase() : ''}${d.uploadedAt ? ' · ' + new Date(d.uploadedAt).toLocaleDateString() : ''}
                </div>
              </div>
              <div style="display:flex; gap:8px; flex-shrink:0; margin-left:12px;">
                <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;" onclick='viewDocument(${i})'>View</button>
                <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;" onclick='downloadDocument(${i})'>Download</button>
                <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;background:#ef4444;color:white;" onclick='deleteDocument(${i})'>Delete</button>
              </div>
            </div>`).join('');
          window._pitchDocs = docs;
        }
      }
    } catch (_) {}

    container.innerHTML = `
      <div class="header-info">
        <div class="startup-header">
          <h1>${escapeHtml(s.name)}</h1>
          <span class="status-badge ${statusClass}" style="font-size:1rem;">${capitalize(s.status || 'pending')}</span>
        </div>
        <div style="display:flex; gap:10px; flex-wrap:wrap;">
          <button class="action-btn" onclick="window.location.href='upload-pitch.html?id=${s.id}'">📤 Upload Pitch</button>
        </div>
      </div>

      <div class="info-grid">
        <div class="info-card">
          <div style="color:#64748b; font-size:0.85rem; margin-bottom:6px;">Industry</div>
          <div style="font-weight:700; font-size:1.1rem;">${escapeHtml(s.industry || '—')}</div>
        </div>
        <div class="info-card">
          <div style="color:#64748b; font-size:0.85rem; margin-bottom:6px;">Stage</div>
          <div style="font-weight:700; font-size:1.1rem;">${escapeHtml(s.stage || '—')}</div>
        </div>
        <div class="info-card">
          <div style="color:#64748b; font-size:0.85rem; margin-bottom:6px;">Funding Goal</div>
          <div style="font-weight:700; font-size:1.1rem;">$${goal.toLocaleString()}</div>
        </div>
        <div class="info-card">
          <div style="color:#64748b; font-size:0.85rem; margin-bottom:6px;">Funding Raised</div>
          <div style="font-weight:700; font-size:1.1rem; color:var(--primary);">$${current.toLocaleString()} (${pct}%)</div>
        </div>
      </div>

      <h3 class="section-title">📝 Description</h3>
      <p style="line-height:1.8; color:#475569;">${escapeHtml(s.description || '—')}</p>

      <h3 class="section-title">🎓 Assigned Mentor</h3>
      ${mentorHTML}

      <h3 class="section-title">📈 Overall Progress</h3>
      <div>
        <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
          <span>Development Progress</span>
          <strong style="color:var(--primary);">${progress}%</strong>
        </div>
        <div class="progress-bar" style="height:12px;">
          <div class="progress-fill" style="width:${progress}%;"></div>
        </div>
      </div>
      <div style="margin-top:16px;">
        <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
          <span>Funding Progress</span>
          <strong style="color:#10b981;">${pct}%</strong>
        </div>
        <div class="progress-bar" style="height:12px;">
          <div class="progress-fill" style="width:${pct}%; background:#10b981;"></div>
        </div>
      </div>

      <h3 class="section-title">💰 Funding Records</h3>
      ${fundingHTML}

      <h3 class="section-title">📄 Pitch Documents</h3>
      ${pitchDocsHTML}
    `;
  } catch (err) {
    console.error(err);
    container.innerHTML = `<p style="color:#ef4444; text-align:center;">Failed to load startup. <a href="my-startups.html">Go back</a>.</p>`;
  }
});

function getInitials(name) {
  return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2) || 'M';
}

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}

const MIME_MAP = { pdf:'application/pdf', ppt:'application/vnd.ms-powerpoint', pptx:'application/vnd.openxmlformats-officedocument.presentationml.presentation', doc:'application/msword', docx:'application/vnd.openxmlformats-officedocument.wordprocessingml.document' };

function base64ToBlob(fileData, fileType) {
  if (!fileData) return null;
  const mime = MIME_MAP[fileType] || 'application/octet-stream';
  const byteString = atob(fileData.split(',')[1] || fileData);
  const ab = new ArrayBuffer(byteString.length);
  const ia = new Uint8Array(ab);
  for (let i = 0; i < byteString.length; i++) ia[i] = byteString.charCodeAt(i);
  return new Blob([ab], { type: mime });
}

function viewDocument(index) {
  const docs = window._pitchDocs || [];
  const d = docs[index];
  if (!d || !d.fileData) { alert('Document content not available.'); return; }
  const blob = base64ToBlob(d.fileData, d.fileType);
  if (blob) window.open(URL.createObjectURL(blob), '_blank');
}

function downloadDocument(index) {
  const docs = window._pitchDocs || [];
  const d = docs[index];
  if (!d || !d.fileData) { alert('Document content not available.'); return; }
  const blob = base64ToBlob(d.fileData, d.fileType);
  if (!blob) return;
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = d.fileName || 'document.' + (d.fileType || 'pdf');
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

function deleteDocument(index) {
  const docs = window._pitchDocs || [];
  const d = docs[index];
  if (!d || !d.id) { alert('Document not found.'); return; }
  if (!confirm(`Delete "${d.fileName || 'Document'}" permanently?`)) return;

  fetch(`http://localhost:8085/api/documents/${d.id}`, { method: 'DELETE' })
    .then(res => {
      if (!res.ok) throw new Error('Failed to delete document');
      showToast('Document deleted successfully', 'success');
      docs.splice(index, 1);
      const container = document.getElementById('startup-details-content');
      if (!container) return;
      const h3s = container.querySelectorAll('h3.section-title');
      const pitchHeader = h3s[h3s.length - 1];
      let next = pitchHeader.nextElementSibling;
      while (next) {
        const el = next;
        next = next.nextElementSibling;
        el.remove();
      }
      if (docs.length === 0) {
        pitchHeader.insertAdjacentHTML('afterend', '<p style="color:#64748b;">No pitch documents uploaded yet.</p>');
      } else {
        pitchHeader.insertAdjacentHTML('afterend', docs.map((d2, i2) => `
          <div style="display:flex; justify-content:space-between; align-items:center; padding:14px 18px; background:var(--bg2,#f8fafc); border:1px solid var(--border); border-radius:10px; margin-bottom:8px;">
            <div style="flex:1; min-width:0;">
              <strong style="font-size:0.95rem;">📄 ${escapeHtml(d2.fileName || 'Document')}</strong>
              <div style="font-size:0.85rem;color:#64748b;margin-top:2px;">
                ${d2.fileType ? d2.fileType.toUpperCase() : ''}${d2.uploadedAt ? ' · ' + new Date(d2.uploadedAt).toLocaleDateString() : ''}
              </div>
            </div>
            <div style="display:flex; gap:8px; flex-shrink:0; margin-left:12px;">
              <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;" onclick='viewDocument(${i2})'>View</button>
              <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;" onclick='downloadDocument(${i2})'>Download</button>
              <button class="action-btn" style="padding:6px 14px;font-size:0.85rem;background:#ef4444;color:white;" onclick='deleteDocument(${i2})'>Delete</button>
            </div>
          </div>`).join(''));
      }
    })
    .catch(err => showToast(err.message, 'error'));
}