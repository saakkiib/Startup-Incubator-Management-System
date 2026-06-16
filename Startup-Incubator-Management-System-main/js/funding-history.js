// =============================================================================
// funding-history.js — Entrepreneur: View Funding History
// Fetches all funding records for this founder and renders with filter.
// =============================================================================

let allFundings = [];

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  try {
    const res = await fetch(`http://localhost:8085/api/fund/founder/${user.id}`);
    if (!res.ok) throw new Error('Failed to fetch funding');

    allFundings = await res.json();

    const startupNames = {};
    allFundings.forEach(f => {
      if (f.startup) startupNames[f.startup.id] = f.startup.name;
    });
    const filterEl = document.getElementById('filter-startup');
    Object.entries(startupNames).forEach(([id, name]) => {
      const opt = document.createElement('option');
      opt.value = id;
      opt.textContent = name;
      filterEl.appendChild(opt);
    });

    renderFunding();

  } catch (err) {
    console.error(err);
    document.getElementById('funding-list').innerHTML =
      `<p style="color:#ef4444; text-align:center;">Failed to load funding history. Is the server running?</p>`;
  }
});

function getInvestorName(f) {
  if (f.investor?.fullName) return f.investor.fullName;
  if (f.investor?.name)     return f.investor.name;
  if (f.investorName)       return f.investorName;
  return 'Anonymous Investor';
}

function renderFunding() {
  const filter = document.getElementById('filter-startup').value;
  const list   = filter
    ? allFundings.filter(f => String(f.startup?.id) === filter)
    : allFundings;

  const total     = list.reduce((a, f) => a + parseFloat(f.amount || 0), 0);
  const investors = new Set(list.map(f => {
    if (f.investor?.fullName) return f.investor.fullName;
    if (f.investor?.name)     return f.investor.name;
    if (f.investorName)       return f.investorName;
    return 'Anonymous';
  }));
  const largest   = list.length > 0
    ? list.reduce((max, f) => parseFloat(f.amount || 0) > parseFloat(max.amount || 0) ? f : max, list[0])
    : null;

  document.getElementById('total-raised').textContent    = `$${total.toLocaleString()}`;
  document.getElementById('total-investors').textContent = `From ${investors.size} investor${investors.size !== 1 ? 's' : ''}`;
  document.getElementById('total-txn').textContent       = list.length;

  if (largest) {
    document.getElementById('largest-investment').textContent = `$${parseFloat(largest.amount || 0).toLocaleString()}`;
    document.getElementById('largest-investor').textContent   = getInvestorName(largest);
  } else {
    document.getElementById('largest-investment').textContent = '$0';
    document.getElementById('largest-investor').textContent   = '—';
  }

  const container = document.getElementById('funding-list');

  if (list.length === 0) {
    container.innerHTML = `
      <div style="text-align:center; padding:60px 20px; color:#64748b;">
        <div style="font-size:3rem; margin-bottom:12px;">💸</div>
        <p>No funding records found.</p>
      </div>`;
    return;
  }

  container.innerHTML = list.map(f => {
    const date = f.fundedAt ? new Date(f.fundedAt).toLocaleDateString('en-US',
      { year:'numeric', month:'short', day:'numeric' }) : '—';
    const investor = getInvestorName(f);
    const startup  = f.startup?.name || '—';
    const noteText = f.terms || f.note || '';
    return `
      <div class="funding-card">
        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:10px;">
          <div>
            <div class="amount">$${parseFloat(f.amount || 0).toLocaleString()}</div>
            <div style="margin-top:6px; color:#64748b; font-size:0.9rem;">
              From <strong>${escapeHtml(investor)}</strong> · Startup: <strong>${escapeHtml(startup)}</strong>
            </div>
          </div>
          <div style="text-align:right;">
            <span class="status-badge status-approved">Received</span>
            <div style="font-size:0.85rem; color:#64748b; margin-top:6px;">📅 ${date}</div>
          </div>
        </div>
        ${noteText ? `<p style="margin-top:12px; color:#475569; font-style:italic;">"${escapeHtml(noteText)}"</p>` : ''}
      </div>`;
  }).join('');
}

function escapeHtml(str) {
  const d = document.createElement('div');
  d.appendChild(document.createTextNode(str));
  return d.innerHTML;
}
