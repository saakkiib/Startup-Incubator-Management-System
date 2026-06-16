// =============================================================================
// theme.js — Global Theme & UI Utilities
// Loaded first (in <head>) so theme is applied before the page renders,
// preventing the white-flash on dark mode reload.
// =============================================================================

(function () {
    const DEFAULT_THEME = 'light';

    // ─── Theme Toggle ──────────────────────────────────────────────────────────

    /**
     * Toggle between light and dark mode.
     * Saves the preference to localStorage so it persists across page reloads.
     */
    window.toggleTheme = function () {
        const html = document.documentElement;
        html.classList.toggle('dark-mode');
        const isDark = html.classList.contains('dark-mode');
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
        updateToggleIcons(isDark);
    };

    /**
     * Update all theme toggle buttons to show the correct icon.
     * ☀️ = currently in dark mode (click to go light)
     * 🌙 = currently in light mode (click to go dark)
     */
    function updateToggleIcons(isDark) {
        const btns = document.querySelectorAll('#theme-toggle');
        btns.forEach(btn => btn.innerText = isDark ? '🌙' : '☀️');
    }

    /**
     * Apply the saved theme preference from localStorage on page load.
     * Called immediately to avoid any flash of the wrong theme.
     */
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

    // ─── Global Toast Notification System ─────────────────────────────────────

    /**
     * Show a temporary toast notification at the bottom of the screen.
     * Used throughout the app for success/error feedback after actions.
     *
     * @param {string} message - The message to display
     * @param {string} type    - 'success' | 'error' | 'warning'
     */
    window.showToast = function (message, type = 'success') {
        // Create the toast container if it doesn't exist yet
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        // Pick an icon based on the notification type
        const icon = type === 'success' ? '✅' : (type === 'error' ? '❌' : '⚠️');
        toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
        container.appendChild(toast);

        // Auto-remove the toast after 4 seconds (with a fade-out animation)
        setTimeout(() => {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    };

    // ─── Skeleton Loader Helper ────────────────────────────────────────────────

    /**
     * Generate a skeleton placeholder HTML element.
     * Used while data is loading to show a "ghost" UI instead of a blank screen.
     *
     * @param {string} width  - CSS width (e.g. '100%', '200px')
     * @param {string} height - CSS height (e.g. '20px', '100px')
     */
    window.getSkeletonHTML = function (width = '100%', height = '20px') {
        return `<div class="skeleton" style="width: ${width}; height: ${height}; margin-bottom: 10px;"></div>`;
    };

    // ─── Initialize on Load ────────────────────────────────────────────────────

    // Apply theme as early as possible — before DOMContentLoaded if we can
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', applyTheme);
    } else {
        applyTheme();
    }
})();
