// =============================================================================
// auth.js — Authentication Logic (Login & Registration)
// Handles form submissions, API calls, and tab switching on auth.html
// =============================================================================

/**
 * Switch between the Login and Register tab views.
 * Shows the selected form and hides the other one.
 *
 * @param {string} tab - 'login' or 'register'
 */
function switchTab(tab) {
    const loginForm    = document.getElementById('form-login');
    const registerForm = document.getElementById('form-register');
    const tabLogin     = document.getElementById('tab-login');
    const tabRegister  = document.getElementById('tab-register');

    if (tab === 'login') {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
    } else {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        tabLogin.classList.remove('active');
        tabRegister.classList.add('active');
    }
}

// ─── Login Handler ─────────────────────────────────────────────────────────────

/**
 * Handle the login form submission.
 * Sends credentials to the backend and saves the returned user to localStorage.
 * Redirects to dashboard on success.
 *
 * @param {Event} e - The form submit event
 */
async function handleLogin(e) {
    e.preventDefault(); // Prevent default page refresh
    const email    = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorMsg = document.getElementById('auth-error');

    try {
        const res = await fetch('http://localhost:8085/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const user = await res.json();
            localStorage.setItem('user', JSON.stringify(user)); // Persist user session
            showToast('Login successful!', 'success');
            setTimeout(() => {
                const roleRoutes = {
                    ADMIN   : 'dashboard-admin.html',
                    MENTOR  : 'dashboard-mentor.html',
                    STUDENT : 'dashboard-entrepreneur.html',
                    INVESTOR: 'dashboard-investor.html'
                };
                const dest = roleRoutes[(user.role || '').toUpperCase()] || 'index.html';
                window.location.href = dest;
            }, 1000);
        } else {
            errorMsg.innerText = 'Invalid email or password';
        }
    } catch (err) {
        errorMsg.innerText = 'Connection to server failed';
    }
}

// ─── Register Handler ──────────────────────────────────────────────────────────

async function handleRegister(e) {
    e.preventDefault();
    const name     = document.getElementById('reg-name').value;
    const email    = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const role     = document.getElementById('reg-role').value;
    const errorMsg = document.getElementById('auth-error');

    if (!email.endsWith('@gmail.com')) {
        errorMsg.innerText = 'Registration is only allowed for @gmail.com addresses.';
        return;
    }

    try {
        const res = await fetch('http://localhost:8085/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role })
        });

        if (res.ok) {
            const user = await res.json();
            localStorage.setItem('user', JSON.stringify(user));
            showToast('Account created successfully!', 'success');
            setTimeout(() => {
                const roleRoutes = {
                    ADMIN   : 'dashboard-admin.html',
                    MENTOR  : 'dashboard-mentor.html',
                    STUDENT : 'dashboard-entrepreneur.html',
                    INVESTOR: 'dashboard-investor.html'
                };
                const dest = roleRoutes[(user.role || '').toUpperCase()] || 'index.html';
                window.location.href = dest;
            }, 1000);
        } else {
            errorMsg.innerText = 'Registration failed. Email might already be taken.';
        }
    } catch (err) {
        errorMsg.innerText = 'Connection to server failed';
    }
}

// ─── URL Param: Auto-Switch Tab ────────────────────────────────────────────────

/**
 * If the URL contains ?mode=register (e.g. from the "Register as a Team" button),
 * automatically switch to the Register tab when the page loads.
 */
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    if (params.get('mode') === 'register') {
        switchTab('register');
    }
});
