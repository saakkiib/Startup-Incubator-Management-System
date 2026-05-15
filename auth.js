function switchTab(tab) {
  document.getElementById('auth-error').innerText = '';
  if (tab === 'login') {
    document.getElementById('form-login').classList.remove('hidden');
    document.getElementById('form-register').classList.add('hidden');
    document.getElementById('tab-login').classList.add('active');
    document.getElementById('tab-register').classList.remove('active');
  } else {
    document.getElementById('form-register').classList.remove('hidden');
    document.getElementById('form-login').classList.add('hidden');
    document.getElementById('tab-register').classList.add('active');
    document.getElementById('tab-login').classList.remove('active');
  }
}

function applyTabFromQuery() {
  const params = new URLSearchParams(window.location.search);
  const tab = params.get('tab');
  if (tab === 'register') {
    switchTab('register');
  } else {
    switchTab('login');
  }
}

function redirectToDashboardByRole(role) {
  window.location.href = getDashboardPathByRole(role);
}

function getReturnPath() {
  const params = new URLSearchParams(window.location.search);
  const returnTo = params.get('returnTo');
  if (!returnTo) return null;
  // Keep redirects local to this app pages only.
  if (
    returnTo === 'profile.html' ||
    returnTo === 'index.html' ||
    returnTo === 'dashboard.html' ||
    returnTo === 'dashboard-entrepreneur.html' ||
    returnTo === 'dashboard-investor.html' ||
    returnTo === 'dashboard-mentor.html' ||
    returnTo === 'dashboard-admin.html'
  ) {
    return returnTo;
  }
  return null;
}

function redirectAfterAuth(role) {
  console.log("Redirecting for role:", role);
  const returnPath = getReturnPath();
  if (returnPath) {
    console.log("Returning to path:", returnPath);
    window.location.href = returnPath;
    return;
  }
  
  const dashPath = getDashboardPathByRole(role);
  console.log("Dashboard path found:", dashPath);
  
  if (dashPath && dashPath !== 'auth.html') {
    window.location.href = dashPath;
  } else {
    console.log("No valid dashboard, sending home.");
    window.location.href = 'index.html';
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;
  
  try {
    const res = await fetch('api/auth.php?action=login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    
    if (data.status === 'success') {
      showToast(`Welcome back, ${data.user.name}!`);
      setTimeout(() => redirectAfterAuth(data.user.role), 800);
    } else {
      showToast(data.message || 'Login failed.', "error");
    }
  } catch(err) {
    showToast('Server error.', "error");
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const name = document.getElementById('reg-name').value;
  const email = document.getElementById('reg-email').value;
  const password = document.getElementById('reg-password').value;
  const role = document.getElementById('reg-role').value;
  
  try {
    const res = await fetch('api/auth.php?action=register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password, role })
    });
    const data = await res.json();
    
    if (data.status === 'success') {
      showToast("Registration successful!");
      setTimeout(() => redirectAfterAuth(data.user.role), 800);
    } else {
      showToast(data.message || 'Registration failed.', "error");
    }
  } catch(err) {
    showToast('Server error.', "error");
  }
}

// Check if already logged in
async function checkAuth() {
  try {
    const res = await fetch('api/auth.php?action=me');
    const data = await res.json();
    if (data.status === 'success') {
      redirectAfterAuth(data.user.role);
    }
  } catch(e) {
    console.error(e);
  }
}

applyTabFromQuery();
checkAuth();
