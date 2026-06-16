document.addEventListener('DOMContentLoaded', () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  const form = document.getElementById('startup-form');
  const submitBtn = document.getElementById('submit-btn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('startup-title').value;
    const description = document.getElementById('startup-desc').value;
    const industry = document.getElementById('startup-industry').value;
    const stage = document.getElementById('startup-stage').value;
    const fundingGoal = parseFloat(document.getElementById('startup-goal').value) || 0;

    const payload = {
      name,
      description,
      industry,
      stage,
      founderId: user.id,
      fundingGoal,
      progress: 0,
      currentFunding: 0
    };

    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';

    try {
      const res = await fetch('http://localhost:8085/api/startups', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        alert('✅ Startup submitted successfully for review!');
        window.location.href = 'my-startups.html';
      } else {
        alert('❌ Failed to submit startup. Please try again.');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Submit for Review';
      }
    } catch (error) {
      console.error('Submit startup error:', error);
      alert('❌ Connection to server failed.');
      submitBtn.disabled = false;
      submitBtn.textContent = 'Submit for Review';
    }
  });
});
