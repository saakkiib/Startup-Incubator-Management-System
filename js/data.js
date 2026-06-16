// =============================================================================
// data.js — Mock Database using localStorage
// Used as a fallback/offline data layer when the backend is not available.
// Provides seed data for testing the UI without a running server.
// =============================================================================

class DB {

    // ─── User Storage ───────────────────────────────────────────────────────────

    /** Retrieve all users from localStorage (returns empty array if none) */
    static getUsers() {
        return JSON.parse(localStorage.getItem('users')) || [];
    }

    /** Persist the full users array to localStorage */
    static saveUsers(users) {
        localStorage.setItem('users', JSON.stringify(users));
    }

    // ─── Project/Startup Storage ───────────────────────────────────────────────

    /** Retrieve all projects from localStorage (returns empty array if none) */
    static getProjects() {
        return JSON.parse(localStorage.getItem('projects')) || [];
    }

    /** Persist the full projects array to localStorage */
    static saveProjects(projects) {
        localStorage.setItem('projects', JSON.stringify(projects));
    }

    // ─── Seed Initial Data ─────────────────────────────────────────────────────

    /**
     * Populate localStorage with demo users and projects if none exist.
     * Called automatically when this script loads.
     * Safe to call multiple times — only seeds if data is missing.
     */
    static init() {
        // Seed demo users (one per role)
        if (this.getUsers().length === 0) {
            this.saveUsers([
                { id: 'u_admin',     name: 'Admin',          email: 'admin@incubator.com',    password: 'admin',    role: 'admin'    },
                { id: 'u_student1',  name: 'John Student',   email: 'student@incubator.com',  password: 'password', role: 'STUDENT'  },
                { id: 'u_investor1', name: 'Sarah Investor',  email: 'investor@incubator.com', password: 'password', role: 'INVESTOR' },
                { id: 'u_mentor1',   name: 'Mike Mentor',     email: 'mentor@incubator.com',   password: 'password', role: 'MENTOR'   }
            ]);
        }

        // Seed demo startup projects
        if (this.getProjects().length === 0) {
            this.saveProjects([
                {
                    id: 'p1',
                    title: 'EcoPack Solutions',
                    description: 'Biodegradable packaging alternatives for e-commerce deliveries.',
                    authorId: 'u_student1',
                    status: 'approved',
                    fundingGoal: 50000,
                    currentFunding: 10000,
                    investors: ['u_investor1'],
                    mentors:   ['u_mentor1']
                },
                {
                    id: 'p2',
                    title: 'AI Health Diagnostics',
                    description: 'A mobile app using AI to diagnose skin conditions from photos.',
                    authorId: 'u_student1',
                    status: 'pending',
                    fundingGoal: 100000,
                    currentFunding: 0,
                    investors: [],
                    mentors:   []
                }
            ]);
        }
    }

    // ─── Current User Session ──────────────────────────────────────────────────

    /** Get the currently logged-in user object (or null if not logged in) */
    static getCurrentUser() {
        return JSON.parse(localStorage.getItem('currentUser')) || null;
    }

    /**
     * Set or clear the current user session.
     * Pass null to log out.
     */
    static setCurrentUser(user) {
        if (user) {
            localStorage.setItem('currentUser', JSON.stringify(user));
        } else {
            localStorage.removeItem('currentUser');
        }
    }

    // ─── Utilities ─────────────────────────────────────────────────────────────

    /** Generate a short random ID string (e.g. "id_x4f2k9q") */
    static generateId() {
        return 'id_' + Math.random().toString(36).substr(2, 9);
    }
}

// Initialize with seed data when this script loads
DB.init();