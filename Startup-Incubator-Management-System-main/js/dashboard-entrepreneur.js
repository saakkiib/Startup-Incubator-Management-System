document.addEventListener('DOMContentLoaded', async () => {
  const user = getCurrentUser();
  if (!user) return;

  populateNavbar(user);

  try {
    // 1. Fetch startups
    const startupsRes = await fetch(`http://localhost:8085/api/startups/founder/${user.id}`);
    if (startupsRes.ok) {
      const startups = await startupsRes.json();
      
      const approved = startups.filter(s => s.status.toLowerCase() === 'approved').length;
      const pending = startups.filter(s => s.status.toLowerCase() === 'pending').length;

      document.getElementById('active-count').textContent = String(approved).padStart(2, '0');
      document.getElementById('startup-stats-summary').textContent = `${approved} Approved • ${pending} Pending`;

      // Calculate average progress
      if (totalStartups > 0) {
        const totalProgress = startups.reduce((acc, curr) => acc + (curr.progress || 0), 0);
        const avg = Math.round(totalProgress / totalStartups);
        document.getElementById('avg-progress').textContent = `${avg}%`;
      } else {
        document.getElementById('avg-progress').textContent = '0%';
      }
    }

    // 2. Fetch funding
    const fundingRes = await fetch(`http://localhost:8085/api/fund/founder/${user.id}`);
    if (fundingRes.ok) {
      const fundings = await fundingRes.json();
      
      const totalAmount = fundings.reduce((acc, curr) => acc + (curr.amount || 0), 0);
      const uniqueInvestors = new Set(fundings.map(f => f.investor.id)).size;

      document.getElementById('total-funding').textContent = `$${totalAmount.toLocaleString()}`;
      document.getElementById('investors-stats-summary').textContent = `From ${uniqueInvestors} Investor${uniqueInvestors !== 1 ? 's' : ''}`;
    }
  } catch (error) {
    console.error('Error fetching dashboard stats:', error);
  }
});
