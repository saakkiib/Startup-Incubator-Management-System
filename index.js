// index.js - Landing Page Logic & Animations

document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(localStorage.getItem('user'));
    const dropdown = document.getElementById('dropdown');
    
    // Auth State Management
    if (user) {
        document.getElementById('notif-container').style.display = 'block';
        document.getElementById('nav-login').style.display = 'none';
        document.getElementById('nav-signup').style.display = 'none';
        document.getElementById('nav-profile').style.display = 'flex';
        document.getElementById('nav-dash').style.display = 'flex';
        document.getElementById('nav-logout').style.display = 'flex';
        document.getElementById('profile-emoji').innerText = '✅';
        
        // Fetch real notification count
        fetch(`http://localhost:8080/api/notifications/user/${user.id}`)
            .then(res => res.json())
            .then(data => {
                document.getElementById('notif-count').innerText = data.filter(n => !n.isRead).length;
            })
            .catch(err => console.error('Notif error:', err));
    }

    // Dropdown toggle
    let dropdownTimeout;
    window.toggleDropdown = (e) => {
        if (e) e.stopPropagation();
        const isOpen = dropdown.style.display === 'block';
        dropdown.style.display = isOpen ? 'none' : 'block';

        // Auto-hide after 5 seconds
        clearTimeout(dropdownTimeout);
        if (!isOpen) {
            dropdownTimeout = setTimeout(() => {
                dropdown.style.display = 'none';
            }, 5000);
        }
    };

    // Click outside to close
    document.addEventListener('click', (e) => {
        // Profile dropdown
        if (!dropdown.contains(e.target) && !document.getElementById('profile-icon-wrapper').contains(e.target)) {
            dropdown.style.display = 'none';
            clearTimeout(dropdownTimeout);
        }
        // Notification dropdown
        const notifDropdown = document.getElementById('notif-dropdown');
        const notifContainer = document.getElementById('notif-container');
        if (notifDropdown && !notifDropdown.contains(e.target) && !notifContainer.contains(e.target)) {
            notifDropdown.style.display = 'none';
            clearTimeout(notifDropdownTimeout);
        }
    });

    window.handleLogout = () => {
        localStorage.removeItem('user');
        window.location.reload();
    };

    // Notifications toggle
    let notifDropdownTimeout;
    window.toggleNotifications = (e) => {
        if (e) e.stopPropagation();
        const notifDropdown = document.getElementById('notif-dropdown');
        const notifList = document.getElementById('notif-list');
        const isOpen = notifDropdown.style.display === 'block';
        
        notifDropdown.style.display = isOpen ? 'none' : 'block';

        // Auto-hide after 3 seconds
        clearTimeout(notifDropdownTimeout);
        if (!isOpen) {
            notifDropdownTimeout = setTimeout(() => {
                notifDropdown.style.display = 'none';
            }, 3000);

            // Load notifications
            if (user) {
                fetch(`http://localhost:8080/api/notifications/user/${user.id}`)
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
                notifList.innerHTML = '<p style="text-align:center; font-size:0.9rem;">Please <a href="auth.html" style="color:var(--primary)">Login</a> to see notifications.</p>';
            }
        }
    };

    function showEmptyNotifs(container) {
        container.innerHTML = `
            <div style="text-align: center; padding: 20px 0;">
                <div style="font-size: 3rem; margin-bottom: 10px; opacity: 0.5;">✨</div>
                <h5 style="margin: 0; color: var(--primary);">All Caught Up!</h5>
                <p style="margin: 5px 0 0; font-size: 0.8rem; opacity: 0.6;">You have no new notifications at this time.</p>
            </div>
        `;
    }

    // --- Circular Animation Logic ---
    const cardsContainer = document.getElementById('dynamic-cards');
    const images = [
        'https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1551434678-e076c223a692?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1531482615713-2afd69097998?auto=format&fit=crop&w=300&q=80',
        'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80'
    ];

    // Create cards
    images.forEach(src => {
        const img = document.createElement('img');
        img.className = 'card';
        img.src = src;
        cardsContainer.appendChild(img);
    });

    const cards = document.querySelectorAll('.card');
    const radius = 250;
    let angle = 0;

    function animate() {
        angle += 0.002; // Rotation speed
        cards.forEach((card, index) => {
            const currentAngle = angle + (index * (2 * Math.PI / cards.length));
            const x = Math.cos(currentAngle) * radius + 300 - 90; // CenterX + offset - halfWidth
            const y = Math.sin(currentAngle) * radius + 300 - 65; // CenterY + offset - halfHeight
            
            card.style.left = `${x}px`;
            card.style.top = `${y}px`;
            
            // Subtle 3D effect
            const scale = 0.8 + (Math.sin(currentAngle) + 1) * 0.2;
            card.style.transform = `scale(${scale})`;
            card.style.opacity = 0.6 + (Math.sin(currentAngle) + 1) * 0.4;
            card.style.zIndex = Math.round((Math.sin(currentAngle) + 1) * 10);
        });
        requestAnimationFrame(animate);
    }

    animate();

    // --- Popup Management ---
    let popupTimeout;
    window.openPopup = (type, e) => {
        if (e) e.stopPropagation();
        const modal = document.getElementById('info-modal');
        const title = document.getElementById('modal-title');
        const body = document.getElementById('modal-body');

        if (type === 'investors') {
            title.innerText = "Our Investors";
            body.innerText = "We have a network of over 2,000 active investors ready to fund the next big idea. Connect with top-tier VCs, angel investors, and venture builders.";
        } else if (type === 'guidelines') {
            title.innerText = "Guidelines";
            body.innerText = "Our platform follows strict regulatory standards to ensure security for both startups and investors. Please review our documentation in the dashboard for full details.";
        }
        
        modal.classList.remove('hidden');

        // Auto-hide after 5 seconds
        clearTimeout(popupTimeout);
        popupTimeout = setTimeout(() => {
            modal.classList.add('hidden');
        }, 5000);
    };

    window.closePopup = () => {
        document.getElementById('info-modal').classList.add('hidden');
        clearTimeout(popupTimeout);
    };

    // Update global click listener for Modal
    document.addEventListener('click', (e) => {
        // Handle Modal click outside
        const modal = document.getElementById('info-modal');
        const modalContent = modal.querySelector('.modal-content');
        if (!modal.classList.contains('hidden') && !modalContent.contains(e.target)) {
            // Check if we didn't click the buttons that open it
            const isButton = e.target.tagName === 'BUTTON' && (e.target.innerText.includes('Investors') || e.target.innerText.includes('Guidelines'));
            if (!isButton) {
                modal.classList.add('hidden');
                clearTimeout(popupTimeout);
            }
        }
    });
});