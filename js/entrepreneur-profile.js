let profileData = null;
let newNidCertData = null;
let newAcademicCertData = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;
  populateNavbar(user);
  document.getElementById('save-btn').addEventListener('click', saveProfile);
  document.getElementById('request-edit-btn').addEventListener('click', requestEdit);
  await loadProfile(user);
  await loadStats(user);
});

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

  setVal('field-name',      data.fullName || '');
  setVal('field-age',       data.age      || '');
  setVal('field-sex',       data.sex      || '');
  setVal('field-nid',       data.nidNo    || '');
  setVal('field-degree',    data.degree   || '');
  setVal('field-education', data.education || '');
  setVal('field-university', data.university || '');
  setVal('field-department', data.department || '');
  setVal('field-batch',     data.batchYear || '');
  setVal('field-phone',     data.phone    || '');
  setVal('field-address',   data.address  || '');
  setVal('field-bio',       data.bio      || '');
  setVal('field-expertise', data.expertise || '');

  showCertPreview('nid-cert-preview', data.nidCertificate);
  showCertPreview('academic-cert-preview', data.academicCertificate);

  const status = (data.editRequestStatus || 'none').toLowerCase();
  const hasData = !!data.age;
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

function showCertPreview(id, data) {
  const el = document.getElementById(id);
  if (!el) return;
  if (data) {
    const ext = data.startsWith('data:image') ? 'image' : 'pdf';
    if (ext === 'image') {
      el.innerHTML = `<img src="${data}" style="max-width:200px;max-height:150px;border-radius:8px;border:1px solid var(--border);">`;
    } else {
      el.innerHTML = `<a href="${data}" target="_blank" style="color:var(--primary);font-weight:600;">📄 View Document</a>`;
    }
    el.style.display = 'block';
  } else {
    el.style.display = 'none';
  }
}

async function loadStats(user) {
  try {
    const [sRes, fRes] = await Promise.all([
      fetch(`http://localhost:8085/api/startups/founder/${user.id}`),
      fetch(`http://localhost:8085/api/fund/founder/${user.id}`)
    ]);
    if (sRes.ok) {
      const startups = await sRes.json();
      document.getElementById('stat-startups').textContent = startups.length;
      if (startups.length > 0) {
        const avg = Math.round(startups.reduce((a, s) => a + (s.progress || 0), 0) / startups.length);
        document.getElementById('stat-progress').textContent = `${avg}%`;
      }
    }
    if (fRes.ok) {
      const fundings = await fRes.json();
      const total = fundings.reduce((a, f) => a + (f.amount || 0), 0);
      document.getElementById('stat-funding').textContent = `$${total.toLocaleString()}`;
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
    fullName            : document.getElementById('field-name').value.trim(),
    age                 : parseInt(document.getElementById('field-age').value) || null,
    sex                 : document.getElementById('field-sex').value,
    nidNo               : document.getElementById('field-nid').value.trim(),
    degree              : document.getElementById('field-degree').value.trim(),
    education           : document.getElementById('field-education').value.trim(),
    university          : document.getElementById('field-university').value.trim(),
    department          : document.getElementById('field-department').value.trim(),
    batchYear           : parseInt(document.getElementById('field-batch').value) || 0,
    phone               : document.getElementById('field-phone').value.trim(),
    address             : document.getElementById('field-address').value.trim(),
    bio                 : document.getElementById('field-bio').value.trim(),
    expertise           : document.getElementById('field-expertise').value.trim(),
    photo               : currentPhoto || null,
    nidCertificate      : newNidCertData || profileData?.nidCertificate || null,
    academicCertificate : newAcademicCertData || profileData?.academicCertificate || null
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
        bio: updated.bio || storedUser.bio,
        university: updated.university || storedUser.university,
        department: updated.department || storedUser.department,
        batchYear: updated.batchYear || storedUser.batchYear
      });
      localStorage.setItem('user', JSON.stringify(storedUser));
      newNidCertData = null;
      newAcademicCertData = null;
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
      fullName            : document.getElementById('field-name').value.trim() || user.fullName,
      age                 : parseInt(document.getElementById('field-age').value) || null,
      sex                 : document.getElementById('field-sex').value || '',
      nidNo               : document.getElementById('field-nid').value.trim() || '',
      degree              : document.getElementById('field-degree').value.trim() || '',
      education           : document.getElementById('field-education').value.trim() || '',
      university          : document.getElementById('field-university').value.trim() || '',
      department          : document.getElementById('field-department').value.trim() || '',
      batchYear           : parseInt(document.getElementById('field-batch').value) || 0,
      phone               : document.getElementById('field-phone').value.trim() || '',
      address             : document.getElementById('field-address').value.trim() || '',
      bio                 : document.getElementById('field-bio').value.trim() || '',
      expertise           : document.getElementById('field-expertise').value.trim() || '',
      photo               : base64,
      nidCertificate      : newNidCertData || profileData?.nidCertificate || null,
      academicCertificate : newAcademicCertData || profileData?.academicCertificate || null
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

function handleCertUpload(input, type) {
  if (!input.files || !input.files[0]) return;
  const file = input.files[0];
  if (file.size > 5 * 1024 * 1024) {
    alert('File too large. Max 5MB.');
    input.value = '';
    return;
  }
  const reader = new FileReader();
  reader.onload = (e) => {
    const base64 = e.target.result;
    if (type === 'nid') {
      newNidCertData = base64;
      showCertPreview('nid-cert-preview', base64);
    } else {
      newAcademicCertData = base64;
      showCertPreview('academic-cert-preview', base64);
    }
  };
  reader.readAsDataURL(file);
}

function setVal(id, value) {
  const el = document.getElementById(id);
  if (!el) return;
  el.value = value;
}
