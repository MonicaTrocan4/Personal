# UniGame - University Gamification Platform

UniGame is an interactive educational solution designed to increase student engagement through gamification mechanics. The platform converts academic results and extracurricular activities into MeritPoints (MP) and digital badges, creating a collaborative and competitive environment.
This project only illustrates user interface concepts, there is no backend involved, the values are hardcoded and, if changed, will reset on the page refresh.

## Personal Contributions

I was responsible for developing the core user flow for **professors**, which includes:

1. **Authentication System (Login)**: Designing the interface and implementing the credential validation logic.
2. **Professor Module**: Architecture and functionality of the Class Dashboard, Student Monitoring, Badge Request Validation, and Resource Management pages.


## Key Features

### 1. Access and Security (Personal Contribution)

* **Dynamic Validation**: The login form performs real-time checks for username length (minimum 3 characters) and password complexity (minimum 6 characters, including at least one letter and one digit).
* **Role-Based Redirection**: The system identifies the account type and redirects the user to the appropriate interface for either Students or Professors.

*Note:* This is a simple login page; it does not include any kind of backend authorization. The credentials are hardcoded and checked via client-side scripts.

### 2. Professor Administration (Personal Contribution)

* **Class Monitoring & Dashboard**: A central control panel that provides general group statistics, overall class performance, and a detailed list of enrolled students.
* **Request Validation**: A dedicated interactive panel for reviewing, approving, or rejecting badge and reward requests submitted by students. This ensures the professor has full control over the gamified economy.
* **Resource Management**: Interfaces designed for professors to easily create, edit, or delete badges and shop items directly from the platform, keeping the educational content fresh and relevant.

### 3. Student Experience

* **Personalized Dashboard**: A central view for tracking obtained badges and the current MeritPoints (MP) balance.
* **Performance Analysis**: Integrated comparison charts allow students to monitor their points and badges relative to the class average.
* **Badge System & Crafting**: Students collect badges for various competencies. A specific feature allows merging basic badges (HTML, CSS, and JS) to unlock the "Front-End Master" title through interactive animations.
* **Reward Shop**: A marketplace where students spend MP on academic benefits such as "24h Deadline Extensions," "Mentorship Sessions," or cafeteria vouchers.
* **Collaboration and Portfolio**: Students can nominate peers for team-based badges and generate a secure public link to share their verified badge portfolio on external platforms.

---

## Technologies Used

* **Frontend**: HTML5 and CSS3 were used for a modern, responsive, and animated interface.
* **Interactivity**: Vanilla JavaScript handles DOM manipulation and simulates backend logic.
* **Iconography**: Integration with Font Awesome 6.4.0 for intuitive visual elements.

## File Structure

*(Note: Focused on the Professor & Authentication flow)*

* `login.html` & `login.js`: Access management, validation, and role routing.
* `dashboard-profesor.html` & `dashboard-profesor.js`: Professor activity control center and class statistics overview.
* `insigne.html` & `insigne-profesor.js`: Logic and UI for creating and managing student badges.
* `magazin.html` & `magazin-profesor.js`: Logic and UI for creating and managing items to include in the reward shop system.

## Application Usage and Improvements Ideas

In order to test the platform yourself, you can simply open `login.html` in any browser, and type in the credentials that can be found in the `login.js` file (lines 38 to 43). You will then be redirected to a dashboard from which you will be able to navigate anywhere on the platform.

For future improvements, a backend can be implemented with real authorization and a database which can store users, classes, badges, and real-time requests.

