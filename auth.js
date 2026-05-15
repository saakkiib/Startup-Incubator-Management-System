// auth.js - Unified Authentication Logic

function switchTab(tab) {
    const loginForm = document.getElementById('form-login');
    const registerForm = document.getElementById('form-register');
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');

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

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorMsg = document.getElementById('auth-error');

    try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const user = await res.json();
            localStorage.setItem('user', JSON.stringify(user));
            showToast('Login successful!', 'success');
            setTimeout(() => window.location.href = 'dashboard.html', 1000);
        } else {
            errorMsg.innerText = 'Invalid email or password';
        }
    } catch (err) {
        errorMsg.innerText = 'Connection to server failed';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const role = document.getElementById('reg-role').value;
    const errorMsg = document.getElementById('auth-error');

    try {
        const res = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role })
        });

        if (res.ok) {
            const user = await res.json();
            localStorage.setItem('user', JSON.stringify(user));
            showToast('Account created successfully!', 'success');
            setTimeout(() => window.location.href = 'dashboard.html', 1000);
        } else {
            errorMsg.innerText = 'Registration failed. Email might be taken.';
        }
    } catch (err) {
        errorMsg.innerText = 'Connection to server failed';
    }
}

// Check for URL params to switch tab
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    if (params.get('mode') === 'register') {
        switchTab('register');
    }
});
