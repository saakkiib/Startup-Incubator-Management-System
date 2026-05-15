// data.js - Mock Database using localStorage

class DB {
  static getUsers() {
    return JSON.parse(localStorage.getItem('users')) || [];
  }

  static saveUsers(users) {
    localStorage.setItem('users', JSON.stringify(users));
  }

  static getProjects() {
    return JSON.parse(localStorage.getItem('projects')) || [];
  }

  static saveProjects(projects) {
    localStorage.setItem('projects', JSON.stringify(projects));
  }

  static init() {
    const users = this.getUsers();
    if (users.length === 0) {
      // Seed initial data
      this.saveUsers([
        { id: 'u_admin', name: 'Admin', email: 'admin@incubator.com', password: 'admin', role: 'admin' },
        { id: 'u_fresher1', name: 'John Fresher', email: 'fresher@incubator.com', password: 'password', role: 'fresher' },
        { id: 'u_investor1', name: 'Sarah Investor', email: 'investor@incubator.com', password: 'password', role: 'investor' },
        { id: 'u_mentor1', name: 'Mike Mentor', email: 'mentor@incubator.com', password: 'password', role: 'mentor' }
      ]);
    }

    const projects = this.getProjects();
    if (projects.length === 0) {
      this.saveProjects([
        {
          id: 'p1',
          title: 'EcoPack Solutions',
          description: 'Biodegradable packaging alternatives for e-commerce deliveries.',
          authorId: 'u_fresher1',
          status: 'approved',
          fundingGoal: 50000,
          currentFunding: 10000,
          investors: ['u_investor1'],
          mentors: ['u_mentor1']
        },
        {
          id: 'p2',
          title: 'AI Health Diagnostics',
          description: 'A mobile app using AI to diagnose skin conditions from photos.',
          authorId: 'u_fresher1',
          status: 'pending',
          fundingGoal: 100000,
          currentFunding: 0,
          investors: [],
          mentors: []
        }
      ]);
    }
  }

  static getCurrentUser() {
    return JSON.parse(localStorage.getItem('currentUser')) || null;
  }

  static setCurrentUser(user) {
    if (user) {
      localStorage.setItem('currentUser', JSON.stringify(user));
    } else {
      localStorage.removeItem('currentUser');
    }
  }

  static generateId() {
    return 'id_' + Math.random().toString(36).substr(2, 9);
  }
}

// Initialize the DB on script load
DB.init();