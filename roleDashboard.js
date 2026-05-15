function getDashboardPathByRole(role) {
  if (role === 'fresher') return 'dashboard-entrepreneur.html';
  if (role === 'investor') return 'dashboard-investor.html';
  if (role === 'mentor') return 'dashboard-mentor.html';
  if (role === 'admin') return 'dashboard-admin.html';
  return 'auth.html';
}

function isAuthDisabled() {
  return false;
}

function getMockUserByRole(role) {
  const finalRole = role || 'fresher';
  return {
    id: 0,
    name: `Demo ${finalRole.charAt(0).toUpperCase() + finalRole.slice(1)}`,
    role: finalRole,
    photo: ''
  };
}
