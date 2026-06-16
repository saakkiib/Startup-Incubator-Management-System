let profileData = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;
  populateNavbar(user);
  document.getElementById('save-btn').addEventListener('click', saveProfile);
  document.getElementById('request-edit-btn').addEventListener('click', requestEdit);
  document.getElementById('sidebar-nav').innerHTML = getMentorSidebar('mentor-profile.html');
  await loadProfile(user);
  await loadStats(user);
  initDropdown();
});

function toggleTheme() {
  document.documentElement.classList.toggle('dark-mode');
  const isDark = document.documentElement.classList.contains('dark-mode');
  localStorage.setItem('theme', isDark ? 'dark' : 'light');
  document.getElementById('theme-toggle').textContent = isDark ? '🌙' : '☀️';
}

function initDropdown() {
  document.addEventListener('click', (e) => {
    const dd = document.getElementById('dropdown');
    if (dd && !e.target.closest('.profile-container')) dd.classList.remove('open');
  });
}
window.toggleDropdown = function(e) {
  e.stopPropagation();
  document.getElementById('dropdown').classList.toggle('open');
};

async function loadProfile(user) {
  try {
    const res = await fetch(`http://localhost:8085/api/users/${user.id}`);
    if (!res.ok) throw new Error('Failed to load profile');
    profileData = await res.json();
  } catch (err) {
    console.error('Profile load error:', err);
    profileData = user;
  }
  renderProfile(profileData);
}

function renderProfile(data) {
  document.getElementById('profile-name').textContent  = data.fullName || data.username || 'Unnamed';
  document.getElementById('profile-email').textContent = data.email || '—';

  if (data.photo) {
    const img = document.createElement('img');
    img.src   = data.photo;
    img.alt   = 'Profile photo';
    img.style.cssText = 'width:100%; height:100%; object-fit:cover; border-radius:50%;';
    const container = document.getElementById('profile-photo-display');
    container.innerHTML = '';
    container.appendChild(img);
  }

  setVal('field-name',          data.fullName || '');
  setVal('field-age',           data.age      || '');
  setVal('field-sex',           data.sex      || '');
  setVal('field-phone',         data.phone    || '');
  setVal('field-education',     data.education || '');
  setVal('field-expertise',     data.expertise || '');
  setVal('field-expertise-area', data.expertiseArea || '');
  setVal('field-organization',  data.organization || '');
  setVal('field-years-exp',     data.yearsExperience || '');
  setVal('field-address',       data.address  || '');
  setVal('field-bio',           data.bio      || '');

  const status = (data.editRequestStatus || 'none').toLowerCase();
  const hasData = !!(data.age || data.phone || data.education || data.bio);
  let canEdit = false;

  if (!hasData) {
    canEdit = true;
  } else if (status === 'approved') {
    canEdit = true;
  }

  const fields = document.querySelectorAll('#edit-section input, #edit-section select, #edit-section textarea');
  fields.forEach(f => f.disabled = !canEdit);
  document.getElementById('save-btn').disabled = false;

  const lockBanner = document.getElementById('lock-banner');
  const approvedBanner = document.getElementById('approved-banner');
  const requestBtn = document.getElementById('request-edit-btn');

  lockBanner.style.display = 'none';
  approvedBanner.style.display = 'none';

  if (canEdit) {
    document.getElementById('save-btn').style.display = 'inline-block';
    requestBtn.style.display = 'none';
    if (hasData && status === 'approved') {
      approvedBanner.style.display = 'block';
    }
  } else {
    document.getElementById('save-btn').style.display = 'none';
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
}

async function loadStats(user) {
  try {
    const res = await fetch(`http://localhost:8085/api/mentor-assignments/mentor/${user.id}`);
    if (res.ok) {
      const assignments = await res.json();
      document.getElementById('stat-assigned').textContent = assignments.length;
    }
    const eRes = await fetch(`http://localhost:8085/api/evaluations/mentor/${user.id}`);
    if (eRes.ok) {
      const evaluations = await eRes.json();
      document.getElementById('stat-evaluations').textContent = evaluations.length;
    }
    const nRes = await fetch(`http://localhost:8085/api/notifications/user/${user.id}`);
    if (nRes.ok) {
      const notifs = await nRes.json();
      document.getElementById('stat-notifications').textContent = notifs.filter(n => !n.read).length;
    }
  } catch (err) {
    console.error('Stats load error:', err);
  }
}

async function requestEdit() {
  const user = getCurrentUser();
  if (!user) return;
  const btn = document.getElementById('request-edit-btn');
  btn.disabled = true;
  btn.textContent = 'Sending request...';
  try {
    const res = await fetch(`http://localhost:8085/api/users/${user.id}/request-edit`, { method: 'PUT' });
    if (res.ok) {
      btn.textContent = '⏳ Edit Request Pending...';
      document.getElementById('lock-banner').style.display = 'block';
    } else {
      btn.disabled = false;
      btn.textContent = '🔓 Request to Edit Profile';
      alert('Failed to send edit request. Please try again.');
    }
  } catch (err) {
    console.error(err);
    btn.disabled = false;
    btn.textContent = '🔓 Request to Edit Profile';
    alert('Connection to server failed.');
  }
}

async function saveProfile() {
  const user = getCurrentUser();
  if (!user) return;

  const currentPhoto = profileData?.photo || (() => {
    try { return JSON.parse(localStorage.getItem('user')).photo; } catch(e) { return null; }
  })();

  const payload = {
    fullName        : document.getElementById('field-name').value.trim(),
    age             : parseInt(document.getElementById('field-age').value) || null,
    sex             : document.getElementById('field-sex').value,
    phone           : document.getElementById('field-phone').value.trim(),
    education       : document.getElementById('field-education').value.trim(),
    expertise       : document.getElementById('field-expertise').value.trim(),
    expertiseArea   : document.getElementById('field-expertise-area').value.trim(),
    organization    : document.getElementById('field-organization').value.trim(),
    yearsExperience : parseInt(document.getElementById('field-years-exp').value) || 0,
    address         : document.getElementById('field-address').value.trim(),
    bio             : document.getElementById('field-bio').value.trim(),
    photo           : currentPhoto || null
  };

  const saveBtn = document.getElementById('save-btn');
  saveBtn.disabled = true;
  saveBtn.textContent = 'Saving...';

  try {
    const res = await fetch(`http://localhost:8085/api/users/${user.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      const updated = await res.json();
      const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
      Object.assign(storedUser, {
        fullName: updated.fullName || storedUser.fullName,
        photo: updated.photo || storedUser.photo,
        phone: updated.phone || storedUser.phone,
        education: updated.education || storedUser.education,
        expertise: updated.expertise || storedUser.expertise,
        expertiseArea: updated.expertiseArea || storedUser.expertiseArea,
        organization: updated.organization || storedUser.organization,
        yearsExperience: updated.yearsExperience || storedUser.yearsExperience,
        bio: updated.bio || storedUser.bio
      });
      localStorage.setItem('user', JSON.stringify(storedUser));
      alert('Profile updated! It has been locked. To edit again, request admin approval.');
      window.location.reload();
    } else {
      const errText = await res.text();
      alert('Failed to save: ' + errText);
      saveBtn.disabled = false;
      saveBtn.textContent = 'Save Changes';
    }
  } catch (err) {
    console.error(err);
    alert('Connection to server failed.');
    saveBtn.disabled = false;
    saveBtn.textContent = 'Save Changes';
  }
}

function handlePhotoChange(input) {
  if (!input.files || !input.files[0]) return;
  const file = input.files[0];
  const reader = new FileReader();
  reader.onload = async (e) => {
    const base64 = e.target.result;
    const container = document.getElementById('profile-photo-display');
    container.innerHTML = `<img src="${base64}" alt="Profile" style="width:100%;height:100%;object-fit:cover;border-radius:50%;">`;

    const user = getCurrentUser();
    if (!user) return;

    const payload = {
      fullName        : document.getElementById('field-name').value.trim() || user.fullName,
      age             : parseInt(document.getElementById('field-age').value) || null,
      sex             : document.getElementById('field-sex').value || '',
      phone           : document.getElementById('field-phone').value.trim() || '',
      education       : document.getElementById('field-education').value.trim() || '',
      expertise       : document.getElementById('field-expertise').value.trim() || '',
      expertiseArea   : document.getElementById('field-expertise-area').value.trim() || '',
      organization    : document.getElementById('field-organization').value.trim() || '',
      yearsExperience : parseInt(document.getElementById('field-years-exp').value) || 0,
      address         : document.getElementById('field-address').value.trim() || '',
      bio             : document.getElementById('field-bio').value.trim() || '',
      photo           : base64
    };

    try {
      const res = await fetch(`http://localhost:8085/api/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        const updated = await res.json();
        const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
        storedUser.photo = updated.photo || base64;
        storedUser.fullName = updated.fullName || storedUser.fullName;
        localStorage.setItem('user', JSON.stringify(storedUser));
        profileData = updated;
      }
    } catch (err) {
      console.error('Photo upload error:', err);
    }
  };
  reader.readAsDataURL(file);
}

function setVal(id, value) {
  const el = document.getElementById(id);
  if (!el) return;
  el.value = value;
}
