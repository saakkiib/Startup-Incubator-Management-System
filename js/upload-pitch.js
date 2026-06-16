// =============================================================================
// upload-pitch.js — Entrepreneur: Pitch Document Upload
// Populates startup dropdown, handles file selection, and posts FormData.
// =============================================================================

let selectedFile = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  // Pre-select startup from query param if provided
  const params    = new URLSearchParams(window.location.search);
  const preselect = params.get('id');

  // Load startups into dropdown
  const select = document.getElementById('startup-select');
  try {
    const res = await fetch(`http://localhost:8085/api/startups/founder/${user.id}`);
    if (res.ok) {
      const startups = await res.json();
      if (startups.length === 0) {
        select.innerHTML = '<option value="">No startups found — submit one first</option>';
        return;
      }
      select.innerHTML = startups.map(s =>
        `<option value="${s.id}" ${String(s.id) === preselect ? 'selected' : ''}>${escapeHtml(s.name)}</option>`
      ).join('');
    }
  } catch (err) {
    select.innerHTML = '<option value="">Error loading startups</option>';
  }

  // Drag & drop support
  const area = document.getElementById('upload-area');
  area.addEventListener('dragover', e => { e.preventDefault(); area.style.background = 'rgba(20,184,166,0.15)'; });
  area.addEventListener('dragleave', () => { area.style.background = ''; });
  area.addEventListener('drop', e => {
    e.preventDefault();
    area.style.background = '';
    const file = e.dataTransfer.files[0];
    if (file) applyFile(file);
  });
});

function handleFileSelect(input) {
  if (input.files && input.files[0]) applyFile(input.files[0]);
}

function applyFile(file) {
  const allowed = ['application/pdf',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];

  if (!allowed.includes(file.type) && !file.name.match(/\.(pdf|ppt|pptx|doc|docx)$/i)) {
    showMessage('❌ Unsupported file type. Please upload PDF, PPT, PPTX, DOC, or DOCX.', 'error');
    return;
  }
  if (file.size > 500 * 1024 * 1024) {
    showMessage('❌ File exceeds 500MB limit.', 'error');
    return;
  }

  selectedFile = file;
  document.getElementById('file-name').textContent = file.name;
  document.getElementById('file-size').textContent = formatBytes(file.size);

  const preview = document.getElementById('file-preview');
  preview.style.display = 'flex';
  document.getElementById('upload-area').style.display = 'none';
}

function clearFile() {
  selectedFile = null;
  document.getElementById('file-preview').style.display = 'none';
  document.getElementById('upload-area').style.display = 'block';
  document.getElementById('pitch-file').value = '';
  document.getElementById('upload-message').style.display = 'none';
}

async function handleUpload() {
  const user = getCurrentUser();
  const startupId = document.getElementById('startup-select').value;
  if (!startupId) {
    showMessage('❌ Please select a startup first.', 'error');
    return;
  }
  if (!selectedFile) {
    showMessage('❌ Please select a file to upload.', 'error');
    return;
  }

  const btn = document.getElementById('upload-btn');
  btn.disabled = true;
  btn.textContent = 'Uploading...';

  // Read file as base64
  const reader = new FileReader();
  reader.onload = async (e) => {
    const base64 = e.target.result;
    const ext = selectedFile.name.split('.').pop().toLowerCase();

    const payload = {
      startupId : parseInt(startupId),
      userId    : user ? user.id : null,
      fileName  : selectedFile.name,
      filePath  : `uploads/${startupId}/${selectedFile.name}`,
      fileType  : ext,
      fileSize  : selectedFile.size,
      fileData  : base64
    };

    try {
      const res = await fetch('http://localhost:8085/api/documents', {
        method : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body   : JSON.stringify(payload)
      });

      if (res.ok) {
        showToast('✅ Pitch document uploaded successfully!', 'success');
        clearFile();
      } else {
        const msg = await res.text();
        showMessage(`❌ Upload failed: ${msg || 'Unknown error'}`, 'error');
      }
    } catch (err) {
      console.error('Upload error:', err);
      showMessage('❌ Connection to server failed. Is the backend running?', 'error');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Upload Pitch Document';
    }
  };
  reader.readAsDataURL(selectedFile);
}

// Fallback showToast in case theme.js didn't load
if (typeof window.showToast !== 'function') {
  window.showToast = function (msg, type) {
    const el = document.getElementById('upload-message');
    if (el) { el.style.display = 'block'; el.textContent = msg; el.style.color = type === 'error' ? '#ef4444' : '#10b981'; }
  };
}

function showMessage(text, type) {
  const el = document.getElementById('upload-message');
  el.style.display = 'block';
  el.style.padding  = '14px 18px';
  el.style.borderRadius = '12px';
  el.style.fontWeight   = '600';
  el.style.background = type === 'success' ? 'rgba(16,185,129,0.1)' : 'rgba(239,68,68,0.1)';
  el.style.color      = type === 'success' ? '#10b981' : '#ef4444';
  el.style.border     = `1px solid ${type === 'success' ? '#10b981' : '#ef4444'}`;
  el.textContent = text;
}

function formatBytes(bytes) {
  if (bytes < 1024)       return bytes + ' B';
  if (bytes < 1048576)    return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / 1048576).toFixed(1) + ' MB';
}

function escapeHtml(str) {
  const d = document.createElement('div');
  d.appendChild(document.createTextNode(str));
  return d.innerHTML;
}
