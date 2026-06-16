# 🚀 Startup Incubator Management System
## Team Roles & Task Distribution (Kanban Board)

This document outlines the foundation built so far, followed by the finalized roles and task assignments for the rest of the project.

---

### 🏆 Phase 1: Completed by Sadman Sakib
**Sadman Sakib has already completed 80% of the core architecture and backend logic:**
- **Backend Architecture Setup:** Initialized the full Java Spring Boot project.
- **Database Entities Created:** Designed the core tables: `User`, `Startup`, `PitchDocument`, `Funding`, `Notification`, `MentorProfile`, `MentorAssignment`, and `Evaluation`.
- **Core APIs Developed:** Built REST Controllers and Services for Authentication, Evaluation, Funding, Mentors, and Startup registration.
- **Security Configured:** Set up `SecurityConfig` and `WebConfig` for CORS and basic security.
- **Frontend Scaffolding:** Created the initial HTML structure for `index.html`, `auth.html`, `profile.html`, and Role-Based Dashboards.

---

## 📌 Phase 2: Pending Task Distribution

### 🎯 1. Sadman Sakib - QA Tester, Content & Deployment Lead
**Responsibilities:**
- **End-to-End Testing:** Test the entire workflow (Login -> Pitch Submission -> Mentor Evaluation -> Funding) and identify any bugs in the existing system.
- **Data Population:** Create dummy accounts (Investors, Mentors, Startups) and submit sample pitch documents for the final presentation.
- **Frontend Deployment:** Host the final frontend source code on Vercel or Netlify.
- **System Quality:** Ensure all API URLs point correctly to the remote backend server before deployment.

### 🎨 2. Faiza - Frontend UI/UX Designer
**Responsibilities:**
- **Global UI Theme:** Finalize color palettes, typography, navbar, and footer across all HTML pages.
- **Dashboard Aesthetics:** Improve the layout and add modern aesthetics to the Admin, Entrepreneur, Mentor, and Investor dashboards.
- **Responsive Design:** Ensure all pages look perfect on both mobile and desktop screens using CSS Flexbox/Grid or Media Queries.
- **Form Styling:** Design clean, interactive forms for Pitch Document submission and mentor evaluations.

### 👩‍💻 3. Rifa - Backend Lead & Database Administrator
**Responsibilities:**
- **Database Setup:** Register and provision a free MySQL database on Aiven or TiDB.
- **Spring Boot Configuration:** Update the `application.properties` with the new remote database credentials.
- **API Maintenance:** Fix any backend API issues or bugs that arise during frontend integration.
- **Backend Deployment:** Host the Spring Boot backend on Render.com or Koyeb and provide the live API link to the team.

### ⚙️ 4. Tonni - Frontend Logic & API Integrator
**Responsibilities:**
- **Authentication Setup:** Connect the Login and Registration forms to the backend using JavaScript `fetch()`.
- **Session Management:** Securely save the generated JWT/Auth Token in the browser's `localStorage` after a successful login.
- **Dynamic Data Rendering:** Fetch startup lists and relevant data from the backend to display dynamically on the dashboards.
- **Logic Integration:** Implement the logic for Mentors and Investors to submit ratings/funding via the API.

---
*End of Document. Please track these tasks on GitHub Projects.*
