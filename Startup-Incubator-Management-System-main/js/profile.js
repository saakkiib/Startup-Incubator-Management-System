const DESIGN_PREVIEW = new URLSearchParams(window.location.search).get('preview') === '1';
const AUTH_DISABLED = typeof isAuthDisabled === 'function' && isAuthDisabled();

async function checkAuthStatus() {
    if (DESIGN_PREVIEW || AUTH_DISABLED) {
        loadPreviewData();
        return;
    }
    try {
        const res = await fetch('api/auth.php?action=me');
        const data = await res.json();
        if (data.status !== 'success') {
            window.location.href = 'auth.html?returnTo=profile.html';
        } else {
            loadProfileData();
        }
    } catch(e) {
        window.location.href = 'auth.html?returnTo=profile.html';
    }
}

function loadPreviewData() {
    const previewUser = {
        name: 'Preview User',
        role: 'fresher',
        age: 24,
        contact_no: '+8801000000000',
        sex: 'Other',
        nid_no: '1234567890123',
        degree: 'B.Sc in CSE',
        address: 'Dhaka, Bangladesh',
        edit_request_status: 'approved',
        photo: ''
    };

    document.getElementById('age-input').value = previewUser.age;
    document.getElementById('contact-input').value = previewUser.contact_no;
    document.getElementById('sex-input').value = previewUser.sex;
    document.getElementById('nid-input').value = previewUser.nid_no;
    document.getElementById('degree-input').value = previewUser.degree;
    document.getElementById('address-input').value = previewUser.address;

    document.getElementById('card-name').innerText = previewUser.name;
    document.getElementById('card-role').innerText = previewUser.role;
    document.getElementById('card-role-badge').innerText = previewUser.role.toUpperCase();
    document.getElementById('card-age').innerText = previewUser.age;
    document.getElementById('card-contact').innerText = previewUser.contact_no;
    document.getElementById('card-sex').innerText = previewUser.sex;
    document.getElementById('card-nid').innerText = formatNid(previewUser.nid_no);
    document.getElementById('card-degree').innerText = previewUser.degree;
    document.getElementById('card-address').innerText = previewUser.address;

    const badge = document.getElementById('status-badge');
    badge.innerText = 'preview';
    badge.className = 'status-badge status-approved';

    const inputs = ['age-input', 'contact-input', 'sex-input', 'nid-input', 'degree-input', 'address-input', 'photo-input'];
    inputs.forEach(id => {
        document.getElementById(id).disabled = false;
        document.getElementById(id).style.opacity = '1';
    });

    document.getElementById('update-btn').classList.remove('hidden');
    document.getElementById('request-btn').classList.add('hidden');
    document.getElementById('project-card').classList.add('hidden');
    document.getElementById('profile-msg').innerText = 'Preview mode: changes are not saved.';
}

async function loadProfileData() {
    try {
        const res = await fetch('api/profile.php?action=get');
        const data = await res.json();
        if (data.status === 'success') {
            const user = data.user;
            
            // Populate inputs
            document.getElementById('age-input').value = user.age || '';
            document.getElementById('contact-input').value = user.contact_no || '';
            document.getElementById('sex-input').value = user.sex || '';
            document.getElementById('nid-input').value = user.nid_no || '';
            document.getElementById('degree-input').value = user.degree || '';
            document.getElementById('address-input').value = user.address || '';
            
            // Update Card
            document.getElementById('card-name').innerText = user.name || 'User Name';
            document.getElementById('card-role').innerText = user.role || 'Role';
            document.getElementById('card-role-badge').innerText = (user.role || 'user').toUpperCase();
            document.getElementById('card-age').innerText = user.age || '-';
            document.getElementById('card-contact').innerText = user.contact_no || '-';
            document.getElementById('card-sex').innerText = user.sex || '-';
            document.getElementById('card-nid').innerText = formatNid(user.nid_no || '');
            document.getElementById('card-degree').innerText = user.degree || '-';
            document.getElementById('card-address').innerText = user.address || '-';
            if (user.photo) {
                document.getElementById('card-photo').src = user.photo;
            }

            // Handle Lock Logic
            const status = user.edit_request_status || 'none';
            const badge = document.getElementById('status-badge');
            badge.innerText = status;
            badge.className = `status-badge status-${status}`;

            const hasData = !!user.age; // If age is set, assume profile is "permanent"
            const canEdit = (status === 'approved') || !hasData;

            const inputs = ['age-input', 'contact-input', 'sex-input', 'nid-input', 'degree-input', 'address-input', 'photo-input'];
            inputs.forEach(id => {
                document.getElementById(id).disabled = !canEdit;
                document.getElementById(id).style.opacity = canEdit ? '1' : '0.6';
            });

            const updateBtn = document.getElementById('update-btn');
            const requestBtn = document.getElementById('request-btn');

            if (canEdit) {
                updateBtn.classList.remove('hidden');
                requestBtn.classList.add('hidden');
            } else {
                updateBtn.classList.add('hidden');
                requestBtn.classList.remove('hidden');
                if (status === 'pending') {
                    requestBtn.innerText = 'Request Pending...';
                    requestBtn.disabled = true;
                } else {
                    requestBtn.innerText = 'Request Admin to Edit';
                    requestBtn.disabled = false;
                }
            }

            // Populate Project Card
            if (data.project) {
                const p = data.project;
                document.getElementById('project-card').classList.remove('hidden');
                document.getElementById('p-title').innerText = p.title;
                document.getElementById('p-status').innerText = p.status;
                document.getElementById('p-status').className = `status-badge status-${p.status}`;
                
                const progress = p.progress || 0;
                document.getElementById('p-progress-text').innerText = `${progress}%`;
                document.getElementById('p-progress-bar').style.width = `${progress}%`;
                
                document.getElementById('p-mentor').innerText = p.mentor;
                document.getElementById('p-investors').innerText = p.investors;
            } else {
                document.getElementById('project-card').classList.add('hidden');
            }
        }
    } catch(e) {
        console.error("Failed to load profile", e);
    }
}

function formatNid(value) {
    const digits = String(value || '').replace(/\D/g, '');
    if (!digits) return '-';
    return digits.replace(/(\d{4})(?=\d)/g, '$1-');
}

function getIdCardElement() {
    return document.querySelector('.id-card');
}

async function captureCardCanvas() {
    const card = getIdCardElement();
    if (!card || typeof html2canvas !== 'function') {
        throw new Error('Card capture is not available.');
    }
    return await html2canvas(card, {
        backgroundColor: null,
        scale: 2
    });
}

window.downloadCardPNG = async function() {
    try {
        const canvas = await captureCardCanvas();
        const link = document.createElement('a');
        link.download = 'my-incubator-id-card.png';
        link.href = canvas.toDataURL('image/png');
        link.click();
    } catch (e) {
        document.getElementById('profile-msg').innerText = 'Failed to download PNG.';
    }
};

window.downloadCardPDF = async function() {
    try {
        const canvas = await captureCardCanvas();
        const image = canvas.toDataURL('image/png');
        const jsPDFConstructor = window.jspdf && window.jspdf.jsPDF;
        if (!jsPDFConstructor) {
            throw new Error('PDF library unavailable');
        }
        const pdf = new jsPDFConstructor({
            orientation: canvas.width >= canvas.height ? 'landscape' : 'portrait',
            unit: 'px',
            format: [canvas.width, canvas.height]
        });
        pdf.addImage(image, 'PNG', 0, 0, canvas.width, canvas.height);
        pdf.save('my-incubator-id-card.pdf');
    } catch (e) {
        document.getElementById('profile-msg').innerText = 'Failed to download PDF.';
    }
};

// Live Preview Logic
const fields = [
    { input: 'age-input', card: 'card-age' },
    { input: 'contact-input', card: 'card-contact' },
    { input: 'sex-input', card: 'card-sex' },
    { input: 'nid-input', card: 'card-nid' },
    { input: 'degree-input', card: 'card-degree' },
    { input: 'address-input', card: 'card-address' }
];

fields.forEach(f => {
    document.getElementById(f.input).addEventListener('input', (e) => {
        if (f.input === 'nid-input') {
            document.getElementById(f.card).innerText = formatNid(e.target.value || '');
            return;
        }
        document.getElementById(f.card).innerText = e.target.value || '-';
    });
});

// Photo Preview
document.getElementById('photo-input').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            document.getElementById('card-photo').src = event.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// Submit Profile
document.getElementById('profile-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    if (DESIGN_PREVIEW) {
        document.getElementById('profile-msg').innerText = 'Preview mode only. Saving is disabled.';
        return;
    }
    const form = e.target;
    const formData = new FormData(form);
    
    try {
        const res = await fetch('api/profile.php?action=update', {
            method: 'POST',
            body: formData
        });
        const data = await res.json();
        if (data.status === 'success') {
            showToast(data.message);
            if (data.photo) {
                localStorage.setItem('profilePhoto', data.photo);
            }
            setTimeout(() => {
                location.reload(); // Reload to lock fields
            }, 1000);
        } else {
            showToast(data.message || "Failed to update profile", "error");
        }
    } catch (error) {
        showToast("An error occurred.", "error");
    }
});

// Request Edit
document.getElementById('request-btn').addEventListener('click', async () => {
    if (DESIGN_PREVIEW) {
        document.getElementById('profile-msg').innerText = 'Preview mode only.';
        return;
    }
    try {
        const res = await fetch('api/profile.php?action=request_edit', { method: 'POST' });
        const data = await res.json();
        if (data.status === 'success') {
            showToast("Request sent successfully!");
            setTimeout(() => location.reload(), 1000);
        } else {
            showToast(data.message || "Failed to send request", "error");
        }
    } catch(e) {
        showToast("An error occurred.", "error");
    }
});

checkAuthStatus();
