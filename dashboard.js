let currentUser = null;
const DESIGN_PREVIEW = new URLSearchParams(window.location.search).get('preview') === '1';
const AUTH_DISABLED = typeof isAuthDisabled === 'function' && isAuthDisabled();

function toggleDropdown() {
  document.getElementById("dropdown").classList.toggle("show");
}

async function checkAuth() {
  if (DESIGN_PREVIEW || AUTH_DISABLED) {
    const expectedRole = document.body.dataset.expectedRole || 'fresher';
    if (typeof getMockUserByRole === 'function') {
      currentUser = getMockUserByRole(expectedRole);
    } else {
      currentUser = { name: 'Preview User', role: expectedRole, photo: '' };
    }
    initDashboard();
    return;
  }
  try {
    const res = await fetch('api/auth.php?action=me');
    const data = await res.json();
    if (data.status === 'success') {
      currentUser = data.user;
      const expectedRole = document.body.dataset.expectedRole;
      if (expectedRole && currentUser.role !== expectedRole) {
        window.location.href = getDashboardPathByRole(currentUser.role);
        return;
      }
      initDashboard();
    } else {
      window.location.href = 'auth.html';
    }
  } catch (e) {
    window.location.href = 'auth.html';
  }
}

async function handleLogout() {
  await fetch('api/auth.php?action=logout');
  window.location.href = 'index.html';
}

function initDashboard() {
  document.getElementById('nav-user-name').innerText = currentUser.name;
  const navImg = document.getElementById('nav-profile-img');
  if (currentUser.photo) {
    navImg.src = currentUser.photo;
    navImg.style.display = 'block';
    const placeholder = document.querySelector('.profile span');
    if (placeholder) placeholder.style.display = 'none';
  } else {
    navImg.style.display = 'none';
  }
  document.getElementById('role-title').innerText = currentUser.role + ' Panel';

  const nav = document.getElementById('sidebar-nav');
  nav.innerHTML = '';

  if (currentUser.role === 'fresher') {
    nav.innerHTML = `<li class="active" onclick="renderProjects(this)">My Ideas</li>
                     <li onclick="openModal()">Submit New Idea</li>
                     <li onclick="renderActivity(this)">Recent Activity</li>`;
  } else if (currentUser.role === 'admin') {
    nav.innerHTML = `<li class="active" onclick="renderProjects(this)">Manage Projects</li>
                     <li onclick="renderEditRequests(this)">Profile Edit Requests</li>
                     <li onclick="renderActivity(this)">System Logs</li>`;
  } else {
    nav.innerHTML = `<li class="active" onclick="renderProjects(this)">Explore Projects</li>
                     <li onclick="renderActivity(this)">My Activity</li>`;
  }

  renderProjects();
}

async function fetchProjects() {
  if (DESIGN_PREVIEW) {
    return [
      {
        id: 1,
        title: 'Preview Startup',
        status: 'approved',
        author_name: 'Demo Founder',
        description: 'This is a static preview project card for design view.',
        funding_goal: '50000.00',
        current_funding: '12000.00'
      }
    ];
  }
  const res = await fetch('api/projects.php?action=list');
  const data = await res.json();
  if (data.status !== 'success') {
    throw new Error(data.message || 'Failed to load projects');
  }
  return data.data || [];
}

function setActiveTab(el) {
    document.querySelectorAll('#sidebar-nav li').forEach(li => li.classList.remove('active'));
    el.classList.add('active');
}

async function renderActivity(el) {
    if (el) setActiveTab(el);
    const area = document.getElementById('content-area');
    area.innerHTML = '<h2>Recent Activity</h2>';
    
    // Mock activity for now as there's no activity table yet
    const activities = [
        { msg: "You logged in from a new device.", time: "2 hours ago" },
        { msg: "Project 'EcoDrive' was approved by admin.", time: "Yesterday" },
        { msg: "New investment of $1000 received for 'Solarify'.", time: "2 days ago" }
    ];

    let html = '<div style="display:flex; flex-direction:column; gap:15px; margin-top:20px;">';
    activities.forEach(a => {
        html += `
            <div class="glass" style="padding:15px; border-radius:12px; display:flex; justify-content:space-between; align-items:center;">
                <span>${a.msg}</span>
                <small class="text-muted">${a.time}</small>
            </div>
        `;
    });
    html += '</div>';
    area.innerHTML += html;
}

async function renderProjects(el) {
  if (el) setActiveTab(el);
  const area = document.getElementById('content-area');
  area.innerHTML = `
    <div style="display:flex; flex-direction:column; gap:15px;">
        ${getSkeletonHTML('60%', '40px')}
        ${getSkeletonHTML('100%', '150px')}
        ${getSkeletonHTML('100%', '150px')}
    </div>
  `;
  try {
    const projects = await fetchProjects();
    if (projects.length === 0) {
      area.innerHTML = '<h2>No projects found.</h2>';
      return;
    }

    const searchTerm = (document.getElementById('search-input')?.value || '').toLowerCase();
    const filtered = projects.filter(p => 
      p.title.toLowerCase().includes(searchTerm) || 
      p.author_name.toLowerCase().includes(searchTerm)
    );

    if (filtered.length === 0) {
      area.innerHTML = `<h2>No projects found${searchTerm ? ' for "' + searchTerm + '"' : ''}.</h2>`;
      return;
    }

    let html = `
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
        <h2>Projects</h2>
        <input type="text" id="search-input" placeholder="Search projects..." oninput="renderProjects()" value="${searchTerm}" style="padding:10px; border-radius:8px; border:1px solid rgba(255,255,255,0.1); background:rgba(0,0,0,0.2); color:white; width:250px;">
      </div>
    `;

    filtered.forEach((p) => {
      const progress = p.progress || 0;
      html += `
        <div class="project-card">
          <div style="display:flex; justify-content:space-between; align-items: center;">
            <h3>${p.title} <span class="status-badge status-${p.status}">${p.status}</span></h3>
            <small>By: ${p.author_name}</small>
          </div>
          <p style="color: #ccc; margin: 10px 0;">${p.description}</p>
          
          <div style="margin: 15px 0;">
            <div style="display:flex; justify-content:space-between; margin-bottom:5px; font-size:0.85rem;">
               <span>Project Progress</span>
               <span>${progress}%</span>
            </div>
            <div style="width:100%; height:6px; background:rgba(255,255,255,0.1); border-radius:10px;">
               <div style="width:${progress}%; height:100%; background:#00ffcc; border-radius:10px; box-shadow:0 0 8px #00ffcc;"></div>
            </div>
          </div>

          <p><strong>Goal:</strong> $${p.funding_goal} &nbsp;|&nbsp; <strong>Funded:</strong> $${p.current_funding}</p>
          <div style="margin-top:15px; display:flex; gap:10px; flex-wrap:wrap;">
            ${getActionButtons(p)}
          </div>
        </div>
      `;
    });

    area.innerHTML = html;
    // Keep focus on search input after re-render
    if (searchTerm) document.getElementById('search-input').focus();
  } catch (e) {
    area.innerHTML = `<h2>${e.message || 'Failed to load projects.'}</h2>`;
  }
}

function getActionButtons(p) {
  let buttons = '';
  if (currentUser.role === 'admin' && p.status === 'pending') {
    buttons += `<button class="action-btn invest" onclick="updateStatus(${p.id}, 'approved')">Approve</button>
                <button class="action-btn reject" onclick="updateStatus(${p.id}, 'rejected')">Reject</button>`;
  }
  if (currentUser.role === 'investor' && p.status === 'approved') {
    buttons += `<button class="action-btn invest" onclick="invest(${p.id})">Invest $1000</button>`;
  }
  if (currentUser.role === 'mentor' && p.status === 'approved') {
    buttons += `<button class="action-btn invest" onclick="mentor(${p.id})">Offer Mentorship</button>`;
  }
  
  // Progress update button for authors or mentors
  if ((currentUser.role === 'fresher' || currentUser.role === 'mentor' || currentUser.role === 'admin') && p.status === 'approved') {
      buttons += `<button class="action-btn" style="background:var(--border); color:var(--text);" onclick="promptProgress(${p.id}, ${p.progress || 0})">Update Progress</button>`;
  }
  
  return buttons;
}

async function promptProgress(id, current) {
    const val = prompt("Enter new progress percentage (0-100):", current);
    if (val === null) return;
    const progress = parseInt(val);
    if (isNaN(progress) || progress < 0 || progress > 100) {
        showToast("Invalid progress value.", "error");
        return;
    }
    const res = await fetch('api/projects.php?action=update_progress', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ project_id: id, progress })
    });
    const data = await res.json();
    if (data.status === 'success') {
        showToast(data.message);
        renderProjects();
    } else {
        showToast(data.message, "error");
    }
}

async function updateStatus(id, status) {
  const res = await fetch('api/projects.php?action=update_status', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ project_id: id, status })
  });
  const data = await res.json();
  if (data.status !== 'success') {
    showToast(data.message || 'Failed to update status.', "error");
    return;
  }
  showToast(`Project ${status} successfully.`);
  renderProjects();
}

async function invest(id) {
  const res = await fetch('api/projects.php?action=invest', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ project_id: id, amount: 1000 })
  });
  const data = await res.json();
  if (data.status !== 'success') {
    showToast(data.message || 'Investment failed.', "error");
    return;
  }
  showToast(data.message || 'Investment successful!');
  renderProjects();
}

async function mentor(id) {
  const res = await fetch('api/projects.php?action=mentor', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ project_id: id })
  });
  const data = await res.json();
  if (data.status === 'success') {
    showToast(data.message || 'Mentorship offered!');
  } else {
    showToast(data.message || 'Error occurred', "error");
  }
  renderProjects();
}

async function renderEditRequests(el) {
  if (el) setActiveTab(el);
  const area = document.getElementById('content-area');
  area.innerHTML = '<h2>Loading requests...</h2>';
  try {
    const res = await fetch('api/profile.php?action=list_requests');
    const data = await res.json();
    if (data.status !== 'success') {
      area.innerHTML = `<h2>${data.message || 'Failed to load requests.'}</h2>`;
      return;
    }
    const requests = data.data || [];
    if (requests.length === 0) {
      area.innerHTML = '<h2>No pending edit requests.</h2>';
      return;
    }
    let html = '<h2>Pending Profile Edit Requests</h2>';
    requests.forEach((r) => {
      html += `
        <div class="project-card">
          <h3>${r.name}</h3>
          <p style="color: #ccc;">${r.email} | ${r.role}</p>
          <div style="margin-top:15px;">
            <button class="action-btn invest" onclick="reviewRequest(${r.id}, 'approved')">Approve</button>
            <button class="action-btn reject" onclick="reviewRequest(${r.id}, 'none')">Reject</button>
          </div>
        </div>
      `;
    });
    area.innerHTML = html;
  } catch (e) {
    area.innerHTML = '<h2>Failed to load requests.</h2>';
  }
}

async function reviewRequest(userId, status) {
  const res = await fetch('api/profile.php?action=review_request', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ user_id: userId, status })
  });
  const data = await res.json();
  if (data.status !== 'success') {
    showToast(data.message || 'Failed to process request.', "error");
    return;
  }
  showToast("Request reviewed successfully.");
  renderEditRequests();
}

// Modal handling
function openModal() {
  document.getElementById('project-modal').classList.remove('hidden');
}

function closeModal() {
  document.getElementById('project-modal').classList.add('hidden');
}

async function submitProject(e) {
  e.preventDefault();
  const title = document.getElementById('proj-title').value;
  const description = document.getElementById('proj-desc').value;
  const funding_goal = Number(document.getElementById('proj-goal').value);
  if (funding_goal <= 0) {
    document.getElementById('proj-error').innerText = 'Funding goal must be greater than 0';
    return;
  }
  
  const res = await fetch('api/projects.php?action=add', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ title, description, funding_goal })
  });
  const data = await res.json();
  if(data.status === 'success') {
    closeModal();
    document.getElementById('form-project').reset();
    renderProjects();
  } else {
    document.getElementById('proj-error').innerText = data.message || 'Error submitting project';
  }
}

// Init
checkAuth();
