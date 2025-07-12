# Attendance Management System for Engineering College

**Project Title:** Attendance Management System for Engineering College

**Prepared By:** Clein Costa
**Date:** 09 June 2025

---

## 1.0 Executive Summary

The Attendance Management System (AMS) is a web-based solution designed to efficiently record, monitor, and manage student attendance across various departments in an engineering college. The primary goal is to replace manual attendance processes with a digital system that improves accuracy, accountability, and provides real-time data for all stakeholders.

---

## 2.0 Business Objectives

* Automate attendance tracking for students in lectures, labs, and tutorials.
* Provide attendance reports for teachers, students, heads of departments (HoDs), and administrative staff.
* Enable subject-wise, teacher-wise, and student-wise attendance tracking.
* Support different departments and their courses with flexible scheduling.

---

## 3.0 Stakeholders

| Role          | Responsibility                                                               |
| :------------ | :--------------------------------------------------------------------------- |
| Admin         | Manages departments, users, courses, and global settings                     |
| Teacher       | Takes attendance, views reports, and manages lecture sessions              |
| Student       | Views own attendance records                                                 |
| HoD           | Views department-level attendance reports and manages staff *(future work)* |
| College Admin | Oversees all departments and generates analytical reports *(future work)* |

---

## 4.0 Scope of the Project

### In Scope:

* Department/course (specification to be finalised)/subject setup
* Student and teacher management
* Attendance session creation
* Attendance marking (manual or QR scan)
* Attendance analytics/reporting *(future work)*
* Notifications to students on low attendance *(future work)*

### Out of Scope (for now):

* Biometric hardware integration
* Offline attendance tracking

---

## 5.0 Assumptions

* All users will have unique login credentials.
* Internet connectivity is available for users.
* React will be used for the frontend; Spring Boot for the backend.

---

## 6.0 Constraints

* System should handle concurrent usage by multiple departments *(future work)*
* Mobile responsiveness is required.
* Data must be securely stored and comply with privacy norms *(future work)*
