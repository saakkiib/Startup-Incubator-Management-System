function goPage(p){ window.location.href = p }

function toggleMode(){
  document.body.classList.toggle("light");
}

function toggleDropdown(){
  const d = document.getElementById("dropdown");
  d.style.display = d.style.display==="block"?"none":"block";
}

function sendEmail(){
  window.location.href="mailto:support@incubator.com";
}

function callPhone(){
  window.location.href="tel:+880123456789";
}

function logout(){
  alert("Logged out!");
}