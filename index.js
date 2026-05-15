function toggleDropdown(){
  document.getElementById("dropdown").classList.toggle("show");
}

function goToAuthTab(tab) {
  window.location.href = `auth.html?tab=${encodeURIComponent(tab)}`;
}



let isLoggedIn = false;
let currentUserRole = null;

async function checkAuthStatus() {
  try {
    const res = await fetch('api/auth.php?action=me');
    const data = await res.json();
    if (data.status === 'success') {
      isLoggedIn = true;
      currentUserRole = data.user.role;
      document.getElementById('nav-login').style.display = 'none';
      document.getElementById('nav-signup').style.display = 'none';
      document.getElementById('nav-logout').style.display = 'block';
      document.getElementById('nav-profile').style.display = 'block';
      document.getElementById('nav-dash').style.display = 'block';
      
      if (data.user.photo) {
        localStorage.setItem('profilePhoto', data.user.photo);
        document.getElementById('profile-emoji').style.display = 'none';
        document.getElementById('profile-img').style.display = 'block';
        document.getElementById('profile-img').src = data.user.photo;
      } else {
        localStorage.removeItem('profilePhoto');
        document.getElementById('profile-emoji').style.display = 'block';
        document.getElementById('profile-img').style.display = 'none';
      }

      // Notification logic
      const notifySpan = document.querySelector('.notify span');
      if (notifySpan) {
        if (currentUserRole === 'admin') {
          try {
            const pRes = await fetch('api/projects.php?action=list');
            const pData = await pRes.json();
            const pendingProjects = (pData.data || []).filter(p => p.status === 'pending').length;
            
            const rRes = await fetch('api/profile.php?action=list_requests');
            const rData = await rRes.json();
            const pendingRequests = (rData.data || []).length;
            
            const total = pendingProjects + pendingRequests;
            notifySpan.innerText = total;
            notifySpan.parentElement.style.display = total > 0 ? 'flex' : 'none';
          } catch(e) { notifySpan.innerText = '0'; }
        } else {
          notifySpan.innerText = '0';
          notifySpan.parentElement.style.display = 'none';
        }
      }
    } else {
      isLoggedIn = false;
      currentUserRole = null;
      document.getElementById('nav-login').style.display = 'block';
      document.getElementById('nav-signup').style.display = 'block';
      document.getElementById('nav-logout').style.display = 'none';
      document.getElementById('nav-dash').style.display = 'none';
      document.getElementById('nav-profile').style.display = 'none';
      const notifySpan = document.querySelector('.notify span');
      if (notifySpan) notifySpan.parentElement.style.display = 'none';
    }
  } catch(e) {
    isLoggedIn = false;
    currentUserRole = null;
    document.getElementById('nav-login').style.display = 'block';
    document.getElementById('nav-signup').style.display = 'block';
    document.getElementById('nav-logout').style.display = 'none';
    document.getElementById('nav-dash').style.display = 'none';
    document.getElementById('nav-profile').style.display = 'none';
  }
}

function handleNav(target) {
  if (target === 'investors' || target === 'guidelines') {
    openPopup(target);
  } else {
    if (isLoggedIn) {
      window.location.href = getDashboardPathByRole(currentUserRole);
    } else {
      window.location.href = 'auth.html';
    }
  }
}
// On page load, set profile icon from stored photo if available
document.addEventListener('DOMContentLoaded', () => {
  const storedPhoto = localStorage.getItem('profilePhoto');
  if (storedPhoto) {
    document.getElementById('profile-emoji').style.display = 'none';
    document.getElementById('profile-img').style.display = 'block';
    document.getElementById('profile-img').src = storedPhoto;
  } else {
    document.getElementById('profile-emoji').style.display = 'block';
    document.getElementById('profile-img').style.display = 'none';
  }
});

async function handleLogout() {
  await fetch('api/auth.php?action=logout');
  window.location.reload();
}

function openPopup(type) {
  const modal = document.getElementById('info-modal');
  const title = document.getElementById('modal-title');
  const body = document.getElementById('modal-body');
  
  if (type === 'about') {
    title.innerText = 'About Us';
    body.innerHTML = `Welcome to My Incubator! We are a premier platform bridging the gap between brilliant freshers and experienced investors. Our goal is to empower the next generation of startups by providing access to world-class mentors and direct seed funding. Join us to build the future.`;
  } else if (type === 'contact') {
    title.innerText = 'Contact Information';
    body.innerHTML = `<strong>Email:</strong> <a href="mailto:sakib@gmail.com" style="color: var(--primary); text-decoration: none; font-weight: 600;">sakib@gmail.com</a><br><br>
                      <strong>Phone:</strong> <a href="tel:+8801316448433" style="color: var(--primary); text-decoration: none; font-weight: 600;">+8801316448433</a><br><br>
                      <strong>Address:</strong> 123 Innovation Drive, Tech City`;
  } else if (type === 'investors') {
    title.innerText = 'Our Investors';
    body.innerHTML = `<p>We have a network of over 2,000 active investors ready to fund the next big idea. Connect with top-tier VCs, angel investors, and venture builders.</p>`;
  } else if (type === 'guidelines') {
    title.innerText = 'All Guidelines';
    body.innerHTML = `<ul><li>Ensure your profile is complete with photo and skills.</li><li>Submit your project with a detailed business plan.</li><li>Communicate regularly with your assigned mentors.</li><li>Maintain transparency with investors.</li></ul>`;
  }
  
  modal.classList.remove('hidden');
}

function closePopup() {
  document.getElementById('info-modal').classList.add('hidden');
}

// Click outside to close modal
window.onclick = function(event) {
  const modal = document.getElementById('info-modal');
  const dropdown = document.getElementById('dropdown');
  
  if (event.target === modal) {
    closePopup();
  }
  
  // Also handle dropdown closing if clicked outside
  if (dropdown && dropdown.classList.contains('show')) {
      const profile = document.getElementById('profile-icon-wrapper');
      if (!profile.contains(event.target) && !dropdown.contains(event.target)) {
          dropdown.classList.remove('show');
      }
  }
}

/* IMAGE SYSTEM */
const images=[
"https://images.unsplash.com/photo-1559136555-9303baea8ebd?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1517048676732-d65bc937f952?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1531482615713-2afd69097998?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1543269865-cbf427effbad?auto=format&fit=crop&w=800&q=80",
"https://images.unsplash.com/photo-1521737711867-e3b97375f902?auto=format&fit=crop&w=800&q=80"
];

let i=0;

const positions = [
  { top: '10px', left: '30px' },
  { top: '50px', left: '400px' },
  { top: '420px', left: '20px' },
  { top: '460px', left: '380px' },
  { top: '230px', left: '-50px' },
  { top: '250px', left: '450px' }
];

function update(){
  document.getElementById("mainImg").src=images[i];
  
  document.querySelectorAll(".card").forEach((img,index)=>{
    img.src=images[(i+index+1)%images.length];
    
    img.style.top=positions[index].top;
    img.style.left=positions[index].left;
  });
  
  i=(i+1)%images.length;
}

update();
setInterval(update,10000);