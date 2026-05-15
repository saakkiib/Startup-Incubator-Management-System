// theme.js - Global Theme & UI Management
(function() {
    const DEFAULT_THEME = 'light';

    // Theme Management
    window.toggleTheme = function() {
        const html = document.documentElement;
        html.classList.toggle('dark-mode');
        const isDark = html.classList.contains('dark-mode');
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
        updateToggleIcons(isDark);
    };

    function updateToggleIcons(isDark) {
        const btns = document.querySelectorAll('#theme-toggle');
        btns.forEach(btn => btn.innerText = isDark ? '🌙' : '☀️');
    }

    function applyTheme() {
        const theme = localStorage.getItem('theme') || DEFAULT_THEME;
        const isDark = theme === 'dark';
        if (isDark) {
            document.documentElement.classList.add('dark-mode');
        } else {
            document.documentElement.classList.remove('dark-mode');
        }
        updateToggleIcons(isDark);
    }

    // Global Toast System
    window.showToast = function(message, type = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        const icon = type === 'success' ? '✅' : (type === 'error' ? '❌' : '⚠️');
        
        toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    };

    // Global Skeleton Helper
    window.getSkeletonHTML = function(width = '100%', height = '20px') {
        return `<div class="skeleton" style="width: ${width}; height: ${height}; margin-bottom: 10px;"></div>`;
    };

    // Initialize
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', applyTheme);
    } else {
        applyTheme();
    }
})();
