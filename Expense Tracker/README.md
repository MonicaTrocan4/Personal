# Personal Expense Tracker

A modern and comprehensive web application for managing personal finances. This application allows users to track their income and expenses, set a monthly budget, and save detailed financial reports.

## Key Features

* Full CRUD Operations: Create, read, update, and delete transactions effortlessly.
* Data Persistence: All data (transactions, saved reports, theme preferences, and monthly budget) is stored in the browser's local storage, ensuring no information is lost upon page reload.
* Interactive Dashboard: Visual representation of expenses categorized by type, powered by Chart.js.
* Monthly Budget Management: Users can set a maximum spending limit. A dynamic progress bar updates in real-time and changes color (green, yellow, red) based on the percentage of the budget used.
* History and Monthly Reports: Users can save the current month's data as a permanent report and clear the current board. Past reports can be viewed in detail through custom modal windows.
* Transaction Filtering: Quickly filter the transaction list to show all, only income, or only expenses.
* Modern UI/UX:
    * Custom modal system for confirmations and inputs, completely replacing native browser alerts.
    * Dark Mode and Light Mode toggle, saving the user's preference.
    * Fully responsive design, optimized for both desktop and mobile devices.

## Technologies Used

* HTML: Semantic markup.
* CSS: Custom properties, Flexbox, CSS Grid, smooth transitions, and media queries for responsiveness.
* JavaScript: DOM manipulation, event handling, array methods (map, filter, reduce), and state management without the use of frameworks.
* External Libraries: Chart.js for data visualization.

## Future Development (Roadmap)

* Feature to export transactions to a .csv file.
* Support for multiple currencies (e.g., EUR, USD, RON).
* Advanced date picker for filtering transactions by specific timeframes.