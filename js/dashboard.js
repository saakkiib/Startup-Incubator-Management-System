// =============================================================================
// dashboard.js — Role-Based Dashboard Logic
// Dynamically builds sidebar navigation and loads content based on user role.
// =============================================================================

document.addEventListener('DOMContentLoaded', () => {

    // ─── Auth Guard ────────────────────────────────────────────────────────────

    const user = JSON.parse(localStorage.getItem('user'));

    // Redirect to login if no session found
    if (!user) {
        window.location.href = 'auth.html';
        return;
    }

    // ─── Initialize Header ─────────────────────────────────────────────────────

    document.getElementById('nav-user-name').innerText = user.fullName || user.username;
    document.getElementById('nav-avatar').innerText    = (user.fullName || user.username).substring(0, 2).toUpperCase();
    document.getElementById('role-title').innerText    = user.role.toLowerCase() + ' panel';

    const sidebarNav     = document.getElementById('sidebar-nav');
    const headerActions  = document.getElementById('header-actions');
    const dynamicContent = document.getElementById('dynamic-content');

    // ─── Role-Based UI Setup ───────────────────────────────────────────────────

    /**
     * Build the sidebar and load the default view based on the user's role.
     */
    function setupRoleUI(role) {
        sidebarNav.innerHTML = '';

        if (role === 'STUDENT') {
            addNavItem('📊 My Startups',   () => loadStartups());
            addNavItem('📁 Documents',     () => loadDocs());
            addNavItem('🔔 Notifications', () => loadNotifs());
            headerActions.innerHTML = `<button class="btn" onclick="openCreateModal()">+ New Startup</button>`;
            loadStartups();

        } else if (role === 'MENTOR') {
            addNavItem('🎯 Assignments',   () => loadAssignments());
            addNavItem('📝 Evaluations',   () => loadEvaluations());
            addNavItem('🔔 Notifications', () => loadNotifs());
            loadAssignments();

        } else if (role === 'INVESTOR') {
            addNavItem('🔍 Explore',       () => loadExplore());
            addNavItem('💰 My Portfolio',  () => loadPortfolio());
            addNavItem('🔔 Notifications', () => loadNotifs());
            loadExplore();

        } else if (role === 'ADMIN') {
            addNavItem('🛠️ Management',    () => loadAdminPanel());
            addNavItem('✅ Approvals',     () => loadApprovals());
            loadAdminPanel();
        }
    }

    // ─── Sidebar Item Builder ──────────────────────────────────────────────────

    /**
     * Create a clickable sidebar nav item that highlights when active.
     */
    function addNavItem(text, callback) {
        const li = document.createElement('li');
        li.innerText = text;
        li.onclick = () => {
            document.querySelectorAll('#sidebar-nav li').forEach(el => el.classList.remove('active'));
            li.classList.add('active');
            callback();
        };
        sidebarNav.appendChild(li);
    }

    // ─── Content Loaders ───────────────────────────────────────────────────────

    /** Load all startups from the API and render them as cards. */
    async function loadStartups() {
        document.getElementById('view-title').innerText = 'My Startups';
        dynamicContent.innerHTML = getSkeletonHTML('100%', '100px').repeat(3);
        try {
            const res  = await fetch('http://localhost:8080/api/startups');
            const data = await res.json();
            dynamicContent.innerHTML = data.map(s => `
                <div class="project-card">
                    <div style="display:flex; justify-content:space-between;">
                        <h3>${s.name}</h3>
                        <span class="status-badge status-${s.status}">${s.status}</span>
                    </div>
                    <p class="text-muted">${s.description}</p>
                    <div style="margin-top:15px; font-size:0.8rem; font-weight:700;">
                        <span>Sector: ${s.industry}</span> | <span>Stage: ${s.stage}</span>
                    </div>
                </div>
            `).join('') || '<p class="text-muted">No startups yet. Click "+ New Startup" to begin.</p>';
        } catch (err) {
            dynamicContent.innerHTML = '<p class="error-msg">Failed to load startups. Is the backend running?</p>';
        }
    }

    /** Load all notifications for the current user. */
    async function loadNotifs() {
        document.getElementById('view-title').innerText = 'Notifications';
        try {
            const res  = await fetch(`http://localhost:8080/api/notifications/user/${user.id}`);
            const data = await res.json();
            dynamicContent.innerHTML = data.map(n => `
                <div class="project-card" style="border-left: 4px solid var(--primary);">
                    <h4 style="margin-bottom:5px;">${n.title}</h4>
                    <p style="font-size:0.9rem;">${n.message}</p>
                    <small class="text-muted">${new Date(n.createdAt).toLocaleString()}</small>
                </div>
            `).join('') || '<p class="text-muted">No notifications yet.</p>';
        } catch (err) {
            dynamicContent.innerHTML = '<p class="error-msg">Failed to load notifications.</p>';
        }
    }

    // Placeholder views — to be fully implemented in later sprints
    function loadDocs()        { dynamicContent.innerHTML = '<h3>Pitch Documents</h3><p>Upload and manage your pitch decks here.</p>'; }
    function loadAssignments() { dynamicContent.innerHTML = '<h3>My Assignments</h3><p>View your assigned startups here.</p>'; }
    function loadEvaluations() { dynamicContent.innerHTML = '<h3>Evaluations</h3><p>Submit evaluations for assigned startups.</p>'; }
    function loadExplore()     { dynamicContent.innerHTML = '<h3>Explore Startups</h3><p>Discover high-potential startups to fund.</p>'; }
    function loadPortfolio()   { dynamicContent.innerHTML = '<h3>My Portfolio</h3><p>Track your active investments.</p>'; }
    function loadAdminPanel()  { dynamicContent.innerHTML = '<h3>Admin Panel</h3><p>Manage users, startups, and settings.</p>'; }
    function loadApprovals()   { dynamicContent.innerHTML = '<h3>Pending Approvals</h3><p>Review and approve startup submissions.</p>'; }

    // ─── Navbar Helpers ────────────────────────────────────────────────────────

    window.toggleDropdown = () => {
        const dropdown = document.getElementById('dropdown');
        dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
    };

    window.handleLogout = () => {
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    };

    // ─── Init ──────────────────────────────────────────────────────────────────
    setupRoleUI(user.role);
});
