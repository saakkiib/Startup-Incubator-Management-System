// =============================================================================
// request-funding.js — Entrepreneur: Submit a Funding Request
// Fetches approved startups, shows funding preview, and posts to /api/fund/invest
// =============================================================================

let startupsData = [];

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  const preselect = new URLSearchParams(window.location.search).get('id');
  const select    = document.getElementById('startup-select');

  try {
    const res = await fetch(`http://localhost:8085/api/startups/founder/${user.id}`);
    if (!res.ok) throw new Error();

    const all = await res.json();
    // Only approved startups can request funding
    startupsData = all.filter(s => (s.status || '').toLowerCase() === 'approved');

    if (startupsData.length === 0) {
      select.innerHTML = '<option value="">No approved startups yet</option>';
      showMsg('ℹ️ Only approved startups can receive funding. Submit a startup and wait for admin approval.', 'info');
      document.getElementById('submit-btn').disabled = true;
      return;
    }

    select.innerHTML = startupsData.map(s =>
      `<option value="${s.id}" ${String(s.id) === preselect ? 'selected' : ''}>${escapeHtml(s.name)}</option>`
    ).join('');

    onStartupChange();

  } catch (err) {
    select.innerHTML = '<option value="">Error loading startups</option>';
  }

  // Purpose change handler
  document.getElementById('purpose').addEventListener('change', onPurposeChange);

  // Form submit
  document.getElementById('funding-form').addEventListener('submit', handleSubmit);
});

function onPurposeChange() {
  const wrapper = document.getElementById('custom-purpose-wrapper');
  const purpose = document.getElementById('purpose').value;
  wrapper.style.display = purpose === 'Others' ? 'block' : 'none';
  if (purpose !== 'Others') document.getElementById('custom-purpose').value = '';
}

function onStartupChange() {
  const select  = document.getElementById('startup-select');
  const startup = startupsData.find(s => String(s.id) === select.value);
  const preview = document.getElementById('startup-preview');

  if (!startup) {
    preview.style.display = 'none';
    return;
  }

  const goal    = startup.fundingGoal    || 0;
  const raised  = startup.currentFunding || 0;
  const needed  = Math.max(0, goal - raised);
  const pct     = goal > 0 ? Math.min(100, Math.round((raised / goal) * 100)) : 0;

  document.getElementById('preview-goal').textContent   = `$${goal.toLocaleString()}`;
  document.getElementById('preview-raised').textContent = `$${raised.toLocaleString()}`;
  document.getElementById('preview-needed').textContent = `$${needed.toLocaleString()}`;
  document.getElementById('preview-bar').style.width    = `${pct}%`;

  preview.style.display = 'block';

  // Pre-fill amount with the needed amount
  if (needed > 0) document.getElementById('amount').value = needed;
}

async function handleSubmit(e) {
  e.preventDefault();

  const user      = getCurrentUser();
  const startupId = document.getElementById('startup-select').value;
  const amount    = parseFloat(document.getElementById('amount').value);
  const purpose   = document.getElementById('purpose').value;
  const note      = document.getElementById('note').value.trim();

  if (!startupId) { showMsg('❌ Please select a startup.', 'error'); return; }
  if (!amount || amount <= 0) { showMsg('❌ Please enter a valid amount.', 'error'); return; }

  const finalPurpose = purpose === 'Others'
    ? document.getElementById('custom-purpose').value.trim()
    : purpose;
  if (!finalPurpose) { showMsg('❌ Please specify the purpose of funds.', 'error'); return; }

  const btn = document.getElementById('submit-btn');
  btn.disabled    = true;
  btn.textContent = 'Submitting…';

  // Payload: we send as an investment deal tagged as a request
  // The backend /api/fund/invest expects: startupId, investorId, amount, notes
  // For a funding *request* (no investor yet), we submit with founderId as a placeholder
  const payload = {
    startupId : parseInt(startupId),
    investorId: user.id,         // placeholder — backend may use null or founder as requester
    amount    : amount,
    notes     : `[FUNDING REQUEST — ${finalPurpose}] ${note}`.trim()
  };

  try {
    const res = await fetch('http://localhost:8085/api/fund/invest', {
      method : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body   : JSON.stringify(payload)
    });

    if (res.ok) {
      showMsg('✅ Funding request submitted successfully! You will be notified by email.', 'success');
      document.getElementById('funding-form').reset();
      document.getElementById('startup-preview').style.display = 'none';
    } else {
      const msg = await res.text();
      showMsg(`❌ Submission failed: ${msg || 'Unknown error'}`, 'error');
    }
  } catch (err) {
    console.error(err);
    showMsg('❌ Connection to server failed. Is the backend running?', 'error');
  } finally {
    btn.disabled    = false;
    btn.textContent = '🚀 Submit Funding Request';
  }
}

function showMsg(text, type) {
  const el = document.getElementById('form-message');
  const colorMap = {
    success: { bg: 'rgba(16,185,129,0.1)', color: '#065f46', border: '#10b981' },
    error  : { bg: 'rgba(239,68,68,0.1)',  color: '#7f1d1d', border: '#ef4444' },
    info   : { bg: 'rgba(20,184,166,0.08)', color: '#134e4a', border: 'rgba(20,184,166,0.3)' }
  };
  const c = colorMap[type] || colorMap.info;
  el.style.display     = 'block';
  el.style.padding     = '14px 18px';
  el.style.borderRadius = '12px';
  el.style.fontWeight   = '600';
  el.style.background  = c.bg;
  el.style.color       = c.color;
  el.style.border      = `1px solid ${c.border}`;
  el.textContent = text;
}

function escapeHtml(str) {
  const d = document.createElement('div');
  d.appendChild(document.createTextNode(str));
  return d.innerHTML;
}
