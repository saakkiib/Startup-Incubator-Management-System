package com.incubator.management.dto;

import com.incubator.management.entity.User;

/**
 * User Response DTO
 * -----------------
 * Safe response object returned to the client after auth and profile operations.
 * IMPORTANT: The password hash is intentionally excluded from this DTO.
 * Built by UserService.mapToResponse().
 */
public class UserResponse {

    private Long id;           // Unique user ID (used for subsequent API calls)
    private String username;   // Unique login handle
    private String email;      // User's email address
    private User.Role role;    // User's role — determines their dashboard and permissions
    private String fullName;   // Display name shown in the UI
    private String phone;      // Contact phone number (optional)
    private String photo;
    private Integer age;
    private String sex;
    private String nidNo;
    private String address;
    private String degree;
    private String education;
    private String bio;
    private String expertise;
    private String university;
    private String department;
    private Integer batchYear;
    private String expertiseArea;
    private String organization;
    private Integer yearsExperience;
    private String nidCertificate;
    private String academicCertificate;
    private String editRequestStatus;
    private boolean isSuperAdmin;
    private boolean isActive;  // Whether the account is active or deactivated
    private boolean isBlocked; // Whether the account is blocked
    private String investmentFocus;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, User.Role role,
                        String fullName, String phone, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.isActive = isActive;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public User.Role getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public Integer getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getNidNo() {
        return nidNo;
    }

    public String getAddress() {
        return address;
    }

    public String getDegree() {
        return degree;
    }

    public String getEducation() {
        return education;
    }

    public String getBio() {
        return bio;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getUniversity() {
        return university;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getBatchYear() {
        return batchYear;
    }

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public String getOrganization() {
        return organization;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public String getNidCertificate() {
        return nidCertificate;
    }

    public String getAcademicCertificate() {
        return academicCertificate;
    }

    public String getEditRequestStatus() {
        return editRequestStatus;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNidNo(String nidNo) {
        this.nidNo = nidNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public void setExpertiseArea(String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public void setNidCertificate(String nidCertificate) {
        this.nidCertificate = nidCertificate;
    }

    public void setAcademicCertificate(String academicCertificate) {
        this.academicCertificate = academicCertificate;
    }

    public void setEditRequestStatus(String editRequestStatus) {
        this.editRequestStatus = editRequestStatus;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public String getInvestmentFocus() {
        return investmentFocus;
    }

    public void setInvestmentFocus(String investmentFocus) {
        this.investmentFocus = investmentFocus;
    }
}
