// =============================================================================
// index.js — Landing Page Logic & Animations
// Handles auth state display, notification badge, circular card animation,
// and info modals on index.html
// =============================================================================

document.addEventListener('DOMContentLoaded', () => {

    // Load the currently logged-in user from localStorage (null if not logged in)
    const stored = localStorage.getItem('user');
    const user = stored ? JSON.parse(stored) : null;
    const dropdown = document.getElementById('dropdown');

    // ─── Auth State: Update Navbar Based on Login Status ──────────────────────

    if (user) {
        // User is logged in — show relevant nav items and hide guest items
        document.getElementById('notif-container').style.display = 'block';
        document.getElementById('nav-login').style.display = 'none';
        document.getElementById('nav-signup').style.display = 'none';
        document.getElementById('nav-profile').style.display = 'flex';
        document.getElementById('nav-logout').style.display = 'flex';
        
        if (user.photo) {
            document.getElementById('profile-emoji').style.display = 'none';
            const profileImg = document.getElementById('profile-img');
            profileImg.src = user.photo;
            profileImg.style.display = 'block';
        } else {
            document.getElementById('profile-emoji').innerText = '✅';
        }
        // Fetch unread notification count and show it in the bell badge
        fetch(`http://localhost:8085/api/notifications/user/${user.id}`)
            .then(res => res.json())
            .then(data => {
                document.getElementById('notif-count').innerText = data.filter(n => !(n.read === true || n.isRead === true)).length;
            })
            .catch(err => console.error('Notification fetch error:', err));
    }

    // ─── Profile Dropdown ──────────────────────────────────────────────────────

    let dropdownTimeout;

    /**
     * Toggle the profile dropdown open/closed.
     * Auto-hides after 5 seconds if the user doesn't interact.
     */
    window.toggleDropdown = (e) => {
        if (e) e.stopPropagation(); // Prevent the click from bubbling to the document listener
        const isOpen = dropdown.style.display === 'block';
        dropdown.style.display = isOpen ? 'none' : 'block';

        clearTimeout(dropdownTimeout);
        if (!isOpen) {
            dropdownTimeout = setTimeout(() => {
                dropdown.style.display = 'none';
            }, 5000);
        }
    };

    // Close dropdowns when clicking anywhere outside of them
    document.addEventListener('click', (e) => {
        // Close profile dropdown
        if (!dropdown.contains(e.target) && !document.getElementById('profile-icon-wrapper').contains(e.target)) {
            dropdown.style.display = 'none';
            clearTimeout(dropdownTimeout);
        }
        // Close notification dropdown
        const notifDropdown = document.getElementById('notif-dropdown');
        const notifContainer = document.getElementById('notif-container');
        if (notifDropdown && !notifDropdown.contains(e.target) && !notifContainer.contains(e.target)) {
            notifDropdown.style.display = 'none';
            clearTimeout(notifDropdownTimeout);
        }
    });

    /** Remove user session and reload the page (effectively logs out) */
    window.handleLogout = () => {
        localStorage.removeItem('user');
        window.location.reload();
    };

    // ─── Notification Dropdown ─────────────────────────────────────────────────

    let notifDropdownTimeout;

    /**
     * Toggle the notification dropdown and load notifications from the API.
     * Auto-hides after 3 seconds.
     */
    window.toggleNotifications = (e) => {
        if (e) e.stopPropagation();
        const notifDropdown = document.getElementById('notif-dropdown');
        const notifList = document.getElementById('notif-list');
        const isOpen = notifDropdown.style.display === 'block';

        notifDropdown.style.display = isOpen ? 'none' : 'block';

        clearTimeout(notifDropdownTimeout);
        if (!isOpen) {
            notifDropdownTimeout = setTimeout(() => {
                notifDropdown.style.display = 'none';
            }, 3000);

            // Fetch and render notifications when the panel opens
            if (user) {
                fetch(`http://localhost:8085/api/notifications/user/${user.id}`)
                    .then(res => res.json())
                    .then(data => {
                        if (data.length > 0) {
                            notifList.innerHTML = data.map(n => `
                                <div style="padding: 10px 0; border-bottom: 1px solid var(--border);">
                                    <h5 style="margin:0; font-size:0.9rem;">${n.title}</h5>
                                    <p style="margin:2px 0; font-size:0.8rem; color:var(--text-muted);">${n.message}</p>
                                </div>
                            `).join('');
                        } else {
                            showEmptyNotifs(notifList);
                        }
                    })
                    .catch(() => showEmptyNotifs(notifList));
            } else {
                // Not logged in — prompt to login
                notifList.innerHTML = '<p style="text-align:center; font-size:0.9rem;">Please <a href="auth.html" style="color:var(--primary)">Login</a> to see notifications.</p>';
            }
        }
    };

    /** Render an empty state message inside the notification list */
    function showEmptyNotifs(container) {
        container.innerHTML = `
            <div style="text-align: center; padding: 20px 0;">
                <div style="font-size: 3rem; margin-bottom: 10px; opacity: 0.5;">✨</div>
                <h5 style="margin: 0; color: var(--primary);">All Caught Up!</h5>
                <p style="margin: 5px 0 0; font-size: 0.8rem; opacity: 0.6;">You have no new notifications at this time.</p>
            </div>
        `;
    }

    // ─── Circular Card Animation ───────────────────────────────────────────────

    const cardsContainer = document.getElementById('dynamic-cards');

    // Images used in the rotating card carousel
    const images = [
        'https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1551434678-e076c223a692?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1531482615713-2afd69097998?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80'
    ];

    // Dynamically create and append card elements
    images.forEach(src => {
        const img = document.createElement('img');
        img.className = 'card';
        img.src = src;
        cardsContainer.appendChild(img);
    });

    const cards = document.querySelectorAll('.card');
    const radius = 250; // Orbit radius in pixels
    let angle = 0;

    /**
     * Animation loop — repositions each card along a circular orbit every frame.
     * Cards also scale and fade based on their position (creating a 3D depth illusion).
     */
    function animate() {
        angle += 0.002; // Controls rotation speed (lower = slower)
        cards.forEach((card, index) => {
            const currentAngle = angle + (index * (2 * Math.PI / cards.length));
            const x = Math.cos(currentAngle) * radius + 300 - 90; // Horizontal position
            const y = Math.sin(currentAngle) * radius + 300 - 65; // Vertical position

            card.style.left = `${x}px`;
            card.style.top  = `${y}px`;

            // Cards in the "front" of the orbit appear larger and more opaque
            const scale   = 0.8 + (Math.sin(currentAngle) + 1) * 0.2;
            card.style.transform = `scale(${scale})`;
            card.style.opacity   = 0.6 + (Math.sin(currentAngle) + 1) * 0.4;
            card.style.zIndex    = Math.round((Math.sin(currentAngle) + 1) * 10);
        });
        requestAnimationFrame(animate);
    }

    animate();

    // ─── Info Popup Modals ─────────────────────────────────────────────────────

    let popupTimeout;

    /**
     * Open the info modal with content for the given type ('investors' or 'guidelines').
     * Auto-closes after 5 seconds.
     *
     * @param {string} type - The type of modal content to show
     * @param {Event}  e    - The click event (used to stop propagation)
     */
    window.openPopup = (type, e) => {
        if (e) e.stopPropagation();
        const modal = document.getElementById('info-modal');
        const title = document.getElementById('modal-title');
        const body  = document.getElementById('modal-body');

        if (type === 'investors') {
            title.innerText = "Our Investors";
            body.innerText  = "We have a network of over 2,000 active investors ready to fund the next big idea. Connect with top-tier VCs, angel investors, and venture builders.";
        } else if (type === 'guidelines') {
            title.innerText = "Guidelines";
            body.innerText  = "Our platform follows strict regulatory standards to ensure security for both startups and investors. Please review our documentation in the dashboard for full details.";
        }

        modal.classList.remove('hidden');

        // Auto-hide after 5 seconds
        clearTimeout(popupTimeout);
        popupTimeout = setTimeout(() => modal.classList.add('hidden'), 5000);
    };

    /** Manually close the info modal */
    window.closePopup = () => {
        document.getElementById('info-modal').classList.add('hidden');
        clearTimeout(popupTimeout);
    };

    // Close modal when clicking outside the modal content box
    document.addEventListener('click', (e) => {
        const modal = document.getElementById('info-modal');
        const modalContent = modal.querySelector('.modal-content');
        if (!modal.classList.contains('hidden') && !modalContent.contains(e.target)) {
            // Don't close if the click was on one of the buttons that opened it
            const isButton = e.target.tagName === 'BUTTON' &&
                (e.target.innerText.includes('Investors') || e.target.innerText.includes('Guidelines'));
            if (!isButton) {
                modal.classList.add('hidden');
                clearTimeout(popupTimeout);
            }
        }
    });

});