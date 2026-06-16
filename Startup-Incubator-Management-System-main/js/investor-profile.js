// =============================================================================
// investor-profile.js — Investor Profile Page Logic
// Loads profile from backend, supports edit mode and photo upload.
// =============================================================================

const BASE = 'http://localhost:8085';
let currentPhotoDataUrl = null;

function getUser() { try { return JSON.parse(localStorage.getItem('user')); } catch { return null; } }
function saveUser(u) { localStorage.setItem('user', JSON.stringify(u)); }
function logout() { localStorage.removeItem('user'); window.location.href = 'auth.html'; }
function toggleTheme() {
    document.documentElement.classList.toggle('dark-mode');
    const isDark = document.documentElement.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    document.getElementById('theme-toggle').textContent = isDark ? '🌙' : '☀️';
}
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 400); }, 3500);
}

function setAvatarPlaceholder(imgEl, name) {
    const initials = (name || 'IN').split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
    imgEl.style.display = 'none';
    const sibling = imgEl.parentElement.querySelector('.avatar-placeholder');
    if (!sibling) {
        const div = document.createElement('div');
        div.className = 'profile-photo avatar-placeholder';
        div.style.cssText = 'display:flex; align-items:center; justify-content:center; font-size:3rem; font-weight:700; color:white;';
        div.textContent = initials;
        imgEl.parentElement.insertBefore(div, imgEl);
    }
}

function updateNavAvatar(photoUrl) {
    const navAvatar = document.getElementById('nav-avatar');
    if (!navAvatar) return;
    if (photoUrl) {
        navAvatar.innerHTML = `<img src="${photoUrl}" style="width:100%;height:100%;object-fit:cover;border-radius:50%;">`;
        navAvatar.style.padding = '0';
        navAvatar.style.overflow = 'hidden';
    }
}

async function loadProfile() {
    const user = getUser();
    let profile = user;

    try {
        const res = await fetch(`${BASE}/api/users/${user.id}`);
        if (res.ok) {
            profile = await res.json();
            saveUser({ ...user, ...profile });
        }
    } catch {}

    const name      = profile.fullName || profile.name || 'Investor';
    const email     = profile.email || '—';
    const phone     = profile.phone || '—';
    const age       = profile.age != null ? profile.age : '—';
    const sex       = profile.sex || '—';
    const nid       = profile.nidNo || '—';
    const degree    = profile.degree || '—';
    const education = profile.education || '—';
    const address   = profile.address || '—';
    const expertise = profile.expertise || '—';
    const bio       = profile.bio || '—';
    const focus     = profile.investmentFocus || '—';
    const photo     = profile.photo || null;

    // ── Edit request status ──
    const status = (profile.editRequestStatus || 'none').toLowerCase();
    const hasData = !!(profile.age || profile.phone || profile.education || profile.bio || profile.expertise);
    let canEdit = false;

    if (!hasData) {
        canEdit = true;
    } else if (status === 'approved') {
        canEdit = true;
    }

    const editBtn = document.getElementById('edit-profile-btn');
    const requestBtn = document.getElementById('request-edit-btn');
    const lockBanner = document.getElementById('lock-banner');
    const approvedBanner = document.getElementById('approved-banner');

    lockBanner.style.display = 'none';
    approvedBanner.style.display = 'none';

    if (canEdit) {
        editBtn.style.display = 'inline-block';
        requestBtn.style.display = 'none';
        if (hasData && status === 'approved') {
            approvedBanner.style.display = 'block';
        }
    } else {
        editBtn.style.display = 'none';
        requestBtn.style.display = 'inline-block';
        if (status === 'pending') {
            requestBtn.disabled = true;
            requestBtn.textContent = '⏳ Edit Request Pending...';
            lockBanner.style.display = 'block';
        } else {
            requestBtn.disabled = false;
            requestBtn.textContent = '🔓 Request to Edit Profile';
        }
    }

    document.getElementById('display-name').textContent  = name;
    document.getElementById('display-email').textContent = email;
    document.getElementById('display-phone').textContent = phone;
    document.getElementById('display-age').textContent   = String(age);
    document.getElementById('display-sex').textContent   = sex;
    document.getElementById('display-nid').textContent   = nid;
    document.getElementById('display-degree').textContent = degree;
    document.getElementById('display-education').textContent = education;
    document.getElementById('display-address').textContent = address;
    document.getElementById('display-expertise').textContent = expertise;
    document.getElementById('display-bio').textContent   = bio;
    document.getElementById('display-focus').textContent = focus;
    document.getElementById('nav-user-name').textContent = name;
    document.getElementById('nav-avatar').textContent    = name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();

    const profileImg = document.getElementById('profile-img');
    if (photo) {
        profileImg.src = photo;
        updateNavAvatar(photo);
    } else {
        setAvatarPlaceholder(profileImg, name);
    }

    loadInvestorStats(user);

    document.getElementById('edit-name').value       = name;
    document.getElementById('edit-email').value      = email;
    document.getElementById('edit-phone').value      = phone;
    document.getElementById('edit-age').value         = age !== '—' ? age : '';
    document.getElementById('edit-sex').value         = sex !== '—' ? sex : '';
    document.getElementById('edit-nid').value         = nid !== '—' ? nid : '';
    document.getElementById('edit-degree').value      = degree !== '—' ? degree : '';
    document.getElementById('edit-education').value   = education !== '—' ? education : '';
    document.getElementById('edit-address').value     = address !== '—' ? address : '';
    document.getElementById('edit-expertise').value   = expertise !== '—' ? expertise : '';
    document.getElementById('edit-bio').value          = bio !== '—' ? bio : '';
    document.getElementById('edit-focus').value       = focus !== '—' ? focus : '';
    document.getElementById('edit-name-header').textContent = name;

    const editImg = document.getElementById('edit-profile-img');
    if (photo) { editImg.src = photo; } else { setAvatarPlaceholder(editImg, name); }
}

async function loadInvestorStats(user) {
    try {
        const res = await fetch(`${BASE}/api/fund/investor/${user.id}`);
        if (res.ok) {
            const deals = await res.json();
            const total = deals.reduce((s, d) => s + (d.amount || 0), 0);
            const active = deals.filter(d => (d.status || '').toLowerCase() !== 'completed').length;
            const startups = new Set(deals.map(d => d.startupId)).size;
            document.getElementById('stat-invested').textContent  = `$${total.toLocaleString()}`;
            document.getElementById('stat-active').textContent    = String(active);
            document.getElementById('stat-return').textContent    = '—';
            document.getElementById('stat-startups').textContent  = String(startups);
            return;
        }
    } catch {}
    document.getElementById('stat-invested').textContent  = '$2.4M';
    document.getElementById('stat-active').textContent    = '19';
    document.getElementById('stat-return').textContent    = '27%';
    document.getElementById('stat-startups').textContent  = '11';
}

function triggerPhotoUpload() { document.getElementById('photo-upload').click(); }

function handlePhotoUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = ev => {
        currentPhotoDataUrl = ev.target.result;
        const profileImg = document.getElementById('profile-img');
        profileImg.src = currentPhotoDataUrl;
        profileImg.style.display = '';
        const placeholder = profileImg.parentElement.querySelector('.avatar-placeholder');
        if (placeholder) placeholder.remove();

        const editImg = document.getElementById('edit-profile-img');
        editImg.src = currentPhotoDataUrl;
        editImg.style.display = '';

        updateNavAvatar(currentPhotoDataUrl);
    };
    reader.readAsDataURL(file);
}

function showEditMode() {
    const user = getUser();
    if (!user) return;
    // Check if editing is allowed via edit request status stored in profile
    document.getElementById('view-mode').style.display = 'none';
    document.getElementById('edit-mode').style.display = '';
}

function hideEditMode() {
    document.getElementById('view-mode').style.display = '';
    document.getElementById('edit-mode').style.display = 'none';
}

async function requestEdit() {
    const user = getUser();
    if (!user) return;
    const btn = document.getElementById('request-edit-btn');
    btn.disabled = true;
    btn.textContent = 'Sending request...';
    try {
        const res = await fetch(`${BASE}/api/users/${user.id}/request-edit`, { method: 'PUT' });
        if (res.ok) {
            btn.textContent = '⏳ Edit Request Pending...';
            document.getElementById('lock-banner').style.display = 'block';
            showToast('Edit request sent to admin!');
        } else {
            btn.disabled = false;
            btn.textContent = '🔓 Request to Edit Profile';
            showToast('Failed to send request', 'error');
        }
    } catch (err) {
        console.error(err);
        btn.disabled = false;
        btn.textContent = '🔓 Request to Edit Profile';
        showToast('Connection failed', 'error');
    }
}

async function saveProfile() {
    const saveBtn = document.getElementById('investor-save-btn');
    saveBtn.disabled = true;
    saveBtn.textContent = '⏳ Saving...';

    const user = getUser();
    if (!user) { showToast('Session expired. Please login again.', 'error'); saveBtn.disabled = false; saveBtn.textContent = 'Save Changes'; return; }

    const newName       = document.getElementById('edit-name').value.trim();
    const newPhone      = document.getElementById('edit-phone').value.trim();
    const newAge        = parseInt(document.getElementById('edit-age').value) || null;
    const newSex        = document.getElementById('edit-sex').value;
    const newNid        = document.getElementById('edit-nid').value.trim();
    const newDegree     = document.getElementById('edit-degree').value.trim();
    const newEducation  = document.getElementById('edit-education').value.trim();
    const newAddress    = document.getElementById('edit-address').value.trim();
    const newExpertise  = document.getElementById('edit-expertise').value.trim();
    const newBio        = document.getElementById('edit-bio').value.trim();
    const newFocus      = document.getElementById('edit-focus').value.trim();

    if (!newName) { showToast('Name cannot be empty', 'error'); saveBtn.disabled = false; saveBtn.textContent = 'Save Changes'; return; }

    const payload = {
        fullName: newName,
        phone: newPhone,
        age: newAge,
        sex: newSex,
        nidNo: newNid,
        degree: newDegree,
        education: newEducation,
        address: newAddress,
        expertise: newExpertise,
        bio: newBio,
        investmentFocus: newFocus,
        ...(currentPhotoDataUrl ? { photo: currentPhotoDataUrl } : {})
    };

    try {
        const res = await fetch(`${BASE}/api/users/${user.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) {
            const errText = await res.text();
            throw new Error(errText || 'Save failed');
        }
        const updated = await res.json();
        saveUser({ ...user, ...updated });
    } catch (err) {
        console.error('Save error:', err);
        showToast('Failed to save: ' + err.message, 'error');
        saveBtn.disabled = false;
        saveBtn.textContent = 'Save Changes';
        return;
    }

    document.getElementById('display-name').textContent  = newName;
    document.getElementById('display-phone').textContent = newPhone || '—';
    document.getElementById('display-age').textContent   = newAge != null ? String(newAge) : '—';
    document.getElementById('display-sex').textContent   = newSex || '—';
    document.getElementById('display-nid').textContent   = newNid || '—';
    document.getElementById('display-degree').textContent = newDegree || '—';
    document.getElementById('display-education').textContent = newEducation || '—';
    document.getElementById('display-address').textContent = newAddress || '—';
    document.getElementById('display-expertise').textContent = newExpertise || '—';
    document.getElementById('display-bio').textContent   = newBio || '—';
    document.getElementById('display-focus').textContent = newFocus;
    document.getElementById('nav-user-name').textContent = newName;
    document.getElementById('edit-name-header').textContent = newName;

    const finalPhoto = currentPhotoDataUrl || getUser()?.photo || null;
    if (finalPhoto) {
        updateNavAvatar(finalPhoto);
    } else {
        document.getElementById('nav-avatar').textContent = newName.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
    }

    if (currentPhotoDataUrl) {
        const profileImg = document.getElementById('profile-img');
        profileImg.src = currentPhotoDataUrl;
        profileImg.style.display = '';
        const placeholder = profileImg.parentElement.querySelector('.avatar-placeholder');
        if (placeholder) placeholder.remove();
        updateNavAvatar(currentPhotoDataUrl);
    }

    hideEditMode();
    showToast('✅ Profile updated! It has been locked. To edit again, request admin approval.');
    saveBtn.disabled = false;
    saveBtn.textContent = 'Save Changes';
    setTimeout(() => window.location.reload(), 1000);
}

document.addEventListener('DOMContentLoaded', () => {
    if (localStorage.getItem('theme') === 'dark') {
        document.documentElement.classList.add('dark-mode');
        document.getElementById('theme-toggle').textContent = '🌙';
    }

    const user = getUser();
    if (!user || (user.role || '').toUpperCase() !== 'INVESTOR') {
        window.location.href = 'auth.html'; return;
    }

    document.getElementById('investor-save-btn').onclick = saveProfile;
    loadProfile();
});
