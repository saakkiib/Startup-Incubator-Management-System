# 🚀 GitHub Kanban Issues - Startup Incubator Management System

*Instruction: Copy each block below and paste it into your GitHub Projects "To Do" column using the `+ Add item` button. You can then assign the respective team member.*

---

## 🏆 Completed Tasks (Move these to "Done")

**Issue:** Backend Architecture & Core Entities Setup
**Assignee:** @SadmanSakib
**Description:** 
- Initialized Java Spring Boot project.
- Designed core tables: `User`, `Startup`, `PitchDocument`, `Funding`, `Notification`, `MentorProfile`, `MentorAssignment`.
- Built REST Controllers and Services for core features.
- Set up `SecurityConfig` and frontend HTML scaffolding.

---

## 📌 Pending Tasks (Add to "To Do")

**Issue:** [QA & Deployment] End-to-End System Testing & Dummy Data
**Assignee:** @SadmanSakib
**Description:** 
- Test the entire workflow (Login -> Pitch Submission -> Mentor Evaluation -> Funding).
- Create dummy accounts (Investors, Mentors, Startups) for presentation.
- Host the final frontend source code on Vercel or Netlify.

**Issue:** [Frontend] Global UI Theme & Responsive Design
**Assignee:** @Faiza
**Description:** 
- Finalize color palettes, typography, navbar, and footer across all HTML pages.
- Improve the layout for Admin, Entrepreneur, Mentor, and Investor dashboards.
- Ensure all pages are responsive on mobile and desktop using CSS Flexbox/Grid.

**Issue:** [Backend] Remote Database Setup & Application Hosting
**Assignee:** @Rifa
**Description:** 
- Register and provision a free MySQL database on Aiven or TiDB.
- Update `application.properties` with remote credentials.
- Host the Spring Boot backend on Render.com or Koyeb.

**Issue:** [Frontend Logic] Authentication & API Integration
**Assignee:** @Tonni
**Description:** 
- Connect Login and Registration forms to the backend using `fetch()`.
- Save JWT/Auth Token securely in `localStorage`.
- Implement dynamic data rendering for dashboards (e.g., fetch startup lists).
