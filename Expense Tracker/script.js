const balanceEl = document.getElementById("balance");
const incomeAmountEl = document.getElementById("income-amount");
const expenseAmountEl = document.getElementById("expense-amount");
const transactionListEl = document.getElementById("transaction-list");
const transactionFormEl = document.getElementById("transaction-form");
const descriptionEl = document.getElementById("description");
const amountEl = document.getElementById("amount");
const categoryEl = document.getElementById("category");
const dateEl = document.getElementById("date");
const themeToggle = document.getElementById('theme-toggle');


let editingId = null; 
const submitBtn = transactionFormEl.querySelector('button[type="submit"]');

const today = new Date().toISOString().split('T')[0];
if(dateEl) dateEl.value = today;

let transactions = JSON.parse(localStorage.getItem("transactions")) || [];
let balanceChart;

let currentFilter = 'all'; 

let monthlyBudget = parseFloat(localStorage.getItem('monthly_budget')) || 0;

if (localStorage.getItem('theme') === 'dark') {
    document.body.classList.add('dark-theme');
    if (themeToggle) themeToggle.textContent = '☀️';
}

if (themeToggle) {
    themeToggle.addEventListener('click', () => {
        document.body.classList.toggle('dark-theme');
        
        if (document.body.classList.contains('dark-theme')) {
            themeToggle.textContent = '☀️';
            localStorage.setItem('theme', 'dark');
        } else {
            themeToggle.textContent = '🌙';
            localStorage.setItem('theme', 'light');
        }
        updateChart();
    });
}

transactionFormEl.addEventListener("submit", addTransaction);

function addTransaction(e) {
    e.preventDefault();

    const description = descriptionEl.value.trim();
    const amount = parseFloat(amountEl.value);
    const category = categoryEl.value;
    const dateValue = dateEl.value;

    if (description === "" || isNaN(amount) || dateValue === "") return;

    const [year, month, day] = dateValue.split('-');
    const formattedDate = `${day}.${month}.${year}`;

    if (editingId !== null) {
        transactions = transactions.map(t => {
            if (t.id === editingId) {
                return { ...t, description, category, amount, date: formattedDate };
            }
            return t;
        });
        
        editingId = null;
        submitBtn.textContent = "Add Transaction";
        submitBtn.style.backgroundColor = "var(--btn-color)"; 
    } else {
        const transaction = {
            id: Date.now(),
            description,
            category,
            amount,
            date: formattedDate
        };
        transactions.push(transaction);
    }

    updateLocalStorage();
    updateTransactionList();
    updateSummary();
    
    transactionFormEl.reset();
    dateEl.value = today; 
}

function setFilter(filter) {
    currentFilter = filter;
    
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    
    document.getElementById(`btn-${filter}`).classList.add('active');
    
    updateTransactionList();
}

function updateTransactionList() {
    transactionListEl.innerHTML = "";
    
    let filteredTransactions = [...transactions];
    
    if (currentFilter === 'income') {
        filteredTransactions = filteredTransactions.filter(t => t.amount > 0);
    } else if (currentFilter === 'expense') {
        filteredTransactions = filteredTransactions.filter(t => t.amount < 0);
    }

    const sortedTransactions = filteredTransactions.reverse();

    if (sortedTransactions.length === 0) {
        transactionListEl.innerHTML = `<p style="text-align:center; padding: 20px; color: var(--text-heading); font-style: italic;">No transactions found.</p>`;
        return;
    }

    sortedTransactions.forEach((transaction) => {
        const transactionEl = createTransactionElement(transaction);
        transactionListEl.appendChild(transactionEl);
    });
}

function createTransactionElement(transaction) {
    const li = document.createElement("li");
    li.classList.add("transaction");
    li.classList.add(transaction.amount > 0 ? "income" : "expense");

    li.innerHTML = `
        <div>
            <strong>${transaction.description}</strong>
            <small>${transaction.category} | ${transaction.date}</small>
        </div>
        <div class="transaction-actions">
            <span class="amount">${formatCurrency(transaction.amount)}</span>
            <button class="edit-btn" onclick="editTransaction(${transaction.id})" title="Edit">✎</button>
            <button class="delete-btn" onclick="removeTransaction(${transaction.id})" title="Delete">&times;</button>
        </div>
    `;
    return li;
}

function openBudgetModal() {
    const modal = document.getElementById('budget-modal');
    const input = document.getElementById('budget-input');
    
    input.value = monthlyBudget > 0 ? monthlyBudget : '';
    modal.style.display = 'flex';
    input.focus();
}

function closeBudgetModal() {
    document.getElementById('budget-modal').style.display = 'none';
}

function clearBudget() {
    monthlyBudget = 0;
    localStorage.removeItem('monthly_budget');
    
    const expenses = transactions
        .filter(t => t.amount < 0)
        .reduce((acc, t) => acc + t.amount, 0);
        
    updateBudgetUI(Math.abs(expenses));
    closeBudgetModal();
}

function saveBudgetFromModal() {
    const inputVal = document.getElementById('budget-input').value;
    const newBudget = parseFloat(inputVal);
    
    if (!isNaN(newBudget) && newBudget > 0) {
        monthlyBudget = newBudget;
        localStorage.setItem('monthly_budget', monthlyBudget);
        
        const expenses = transactions
            .filter(t => t.amount < 0)
            .reduce((acc, t) => acc + t.amount, 0);
            
        updateBudgetUI(Math.abs(expenses));
        closeBudgetModal();
    } else {
        showConfirmModal("Invalid Input", "Please enter a valid amount greater than 0.", null);
    }
}

function updateBudgetUI(totalExpenses) {
    const progressBar = document.getElementById('budget-progress');
    const budgetText = document.getElementById('budget-text');
    
    if (!progressBar || !budgetText) return;
    
    if (monthlyBudget === 0) {
        progressBar.style.width = '0%';
        budgetText.innerText = `No budget set. Click 'Set'.`;
        return;
    }
    
    const percent = Math.min((totalExpenses / monthlyBudget) * 100, 100);
    progressBar.style.width = `${percent}%`;
    
    budgetText.innerText = `Spent: ${formatCurrency(totalExpenses)} / ${formatCurrency(monthlyBudget)} (${percent.toFixed(1)}%)`;
    
    if (percent < 50) {
        progressBar.style.backgroundColor = '#b8e994'; 
    } else if (percent < 85) {
        progressBar.style.backgroundColor = '#feca57'; 
    } else {
        progressBar.style.backgroundColor = '#ff7675'; 
    }
}

function updateSummary() {
    const balance = transactions.reduce((acc, t) => acc + t.amount, 0);
    const income = transactions
        .filter(t => t.amount > 0)
        .reduce((acc, t) => acc + t.amount, 0);
    
    const expenses = transactions
        .filter(t => t.amount < 0)
        .reduce((acc, t) => acc + t.amount, 0);

    balanceEl.textContent = formatCurrency(balance);
    incomeAmountEl.textContent = formatCurrency(income);
    expenseAmountEl.textContent = formatCurrency(expenses);

    updateBudgetUI(Math.abs(expenses));

    updateChart();
}

function removeTransaction(id) {
    transactions = transactions.filter(t => t.id !== id);
    updateLocalStorage();
    updateTransactionList();
    updateSummary();
}

function editTransaction(id) {
    const transaction = transactions.find(t => t.id === id);
    if (!transaction) return;

    descriptionEl.value = transaction.description;
    amountEl.value = transaction.amount;
    categoryEl.value = transaction.category;
    
    const [day, month, year] = transaction.date.split('.');
    dateEl.value = `${year}-${month}-${day}`;

    editingId = id;
    
    submitBtn.textContent = "Update Transaction";
    submitBtn.style.backgroundColor = "#ff9ff3"; 
    
    transactionFormEl.scrollIntoView({ behavior: 'smooth' });
}

function updateLocalStorage() {
    localStorage.setItem("transactions", JSON.stringify(transactions));
}

function formatCurrency(number) {
    return new Intl.NumberFormat("ro-RO", {
        style: "currency",
        currency: "RON",
    }).format(number);
}

function updateChart() {
    const canvas = document.getElementById('balanceChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const expenseTransactions = transactions.filter(t => t.amount < 0);
    
    if (expenseTransactions.length === 0) {
        if (balanceChart) balanceChart.destroy();
        canvas.parentElement.style.display = 'none';
        return;
    }

    canvas.parentElement.style.display = 'block';

    const categoryTotals = {};
    expenseTransactions.forEach(t => {
        categoryTotals[t.category] = (categoryTotals[t.category] || 0) + Math.abs(t.amount);
    });

    if (balanceChart) balanceChart.destroy();

    const isDark = document.body.classList.contains('dark-theme');

    balanceChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(categoryTotals),
            datasets: [{
                data: Object.values(categoryTotals),
                backgroundColor: ['#a1c4fd', '#78e08f', '#ff9ff3', '#feca57', '#54a0ff', '#5f27cd'],
                borderWidth: 2,
                borderColor: isDark ? '#1e293b' : '#ffffff'
            }]
        },
        options: {  
            cutout: '75%',
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: isDark ? '#cbd5e1' : '#576574',
                        padding: 20,
                        font: { family: 'Poppins' }
                    }
                }
            },
            elements: {
                arc: { borderRadius: 8 }
            }
        }
    });
}

function saveMonthlyReport() {
    if(transactions.length === 0) {
        showConfirmModal("Atenție", "Nu ai nicio tranzacție de salvat!", null);
        return;
    }
    
    const now = new Date();
    const monthName = now.toLocaleString('ro-RO', {month: 'long', year: 'numeric'});

    showConfirmModal(
        "Save Report",
        `Do you want to save the report for ${monthName} and clear the current transactions?`,
        () => {
            const income = transactions.filter(t => t.amount > 0).reduce((acc, t) => acc + t.amount, 0);
            const expenses = transactions.filter(t => t.amount < 0).reduce((acc, t) => acc + t.amount, 0);

            const report = {
                id: Date.now(),
                period: monthName,
                income: income,
                expenses: expenses,
                balance: income + expenses,
                details: [...transactions]
            };

            let reports = JSON.parse(localStorage.getItem("monthly_reports")) || [];
            reports.push(report);
            localStorage.setItem("monthly_reports", JSON.stringify(reports));

            transactions = [];
            updateLocalStorage();
            updateTransactionList();
            updateSummary();
            displayReports();
        }
    );
}

function deleteReport(id) {
    showConfirmModal(
        "Delete Report",
        "Are you sure you want to delete this report? This action cannot be undone.",
        () => {
            let reports = JSON.parse(localStorage.getItem("monthly_reports")) || [];
            reports = reports.filter(r => r.id !== id);
            localStorage.setItem("monthly_reports", JSON.stringify(reports));
            displayReports();
            
            document.getElementById("report-modal").style.display = "none";
        }
    );
}

function displayReports() {
    const reportsListEl = document.getElementById("reports-list");
    const reports = JSON.parse(localStorage.getItem("monthly_reports")) || [];

    if (reports.length === 0) {
        reportsListEl.innerHTML = "<p style='text-align:center; color: var(--text-heading); grid-column: 1 / -1; padding: 20px;'>No saved reports yet.</p>";
        return;
    }

    reportsListEl.innerHTML = ""; 

    reports.forEach(report => {
        const reportEl = document.createElement("div");
        reportEl.classList.add("report-card");
        
        reportEl.setAttribute("onclick", `openReportDetails(${report.id})`);
        
        reportEl.innerHTML = `
            <div class="report-header" style="border-bottom: none; padding-bottom: 0;">
                <strong>🗓️ ${report.period}</strong>
                <button class="delete-btn" onclick="event.stopPropagation(); deleteReport(${report.id})" title="Delete Report">&times;</button>
            </div>
        `;
        
        reportsListEl.appendChild(reportEl);
    });
}


function openReportDetails(id) {
    const reports = JSON.parse(localStorage.getItem("monthly_reports")) || [];
    const report = reports.find(r => r.id === id);
    if (!report) return;

    const modal = document.getElementById("report-modal");
    const modalTitle = document.getElementById("modal-title");
    const modalSummary = document.getElementById("modal-summary");
    const detailsList = document.getElementById("modal-details-list");

    modalTitle.innerText = `Raport: ${report.period}`;
    
    modalSummary.innerHTML = `
        <div class="modal-summary-item income">
            <small>Venituri</small>
            <span>${formatCurrency(report.income)}</span>
        </div>
        <div class="modal-summary-item expense">
            <small>Cheltuieli</small>
            <span>${formatCurrency(report.expenses)}</span>
        </div>
    `;

    detailsList.innerHTML = "";

    if (report.details && report.details.length > 0) {
        report.details.forEach(t => {
            const item = document.createElement("div");
            item.className = `transaction ${t.amount > 0 ? 'income' : 'expense'}`;
            item.innerHTML = `
                <div>
                    <strong>${t.description}</strong>
                    <small style="display:block; color:var(--text-heading); margin-top:3px;">${t.category} | ${t.date}</small>
                </div>
                <span style="font-weight:600; font-size:1.1rem;">${formatCurrency(t.amount)}</span>
            `;
            detailsList.appendChild(item);
        });
    } else {
        detailsList.innerHTML = "<p style='text-align:center; padding: 20px; color: var(--text-heading);'>Nu există tranzacții pentru această lună.</p>";
    }

    modal.style.display = "flex";
}

document.querySelector(".close-modal").onclick = () => {
    document.getElementById("report-modal").style.display = "none";
};

window.onclick = (event) => {
    const modal = document.getElementById("report-modal");
    if (event.target == modal) modal.style.display = "none";
};

let pendingConfirmAction = null;

function showConfirmModal(title, message, onConfirm) {
    document.getElementById('confirm-title').innerText = title;
    document.getElementById('confirm-message').innerText = message;
    
    const btnYes = document.getElementById('confirm-yes');
    const btnNo = document.getElementById('confirm-no');
    
    if (!onConfirm) {
        btnNo.style.display = 'none';
        btnYes.textContent = 'OK';
    } else {
        btnNo.style.display = 'block';
        btnYes.textContent = 'Yes';
    }

    document.getElementById('confirm-modal').style.display = 'flex';
    pendingConfirmAction = onConfirm;
}

document.getElementById('confirm-yes').onclick = () => {
    if (pendingConfirmAction) pendingConfirmAction();
    document.getElementById('confirm-modal').style.display = 'none';
};

document.getElementById('confirm-no').onclick = () => {
    document.getElementById('confirm-modal').style.display = 'none';
    pendingConfirmAction = null;
};

window.onclick = (event) => {
    const reportModal = document.getElementById("report-modal");
    const confirmModal = document.getElementById("confirm-modal");
    const budgetModal = document.getElementById("budget-modal");
    
    if (event.target == reportModal) reportModal.style.display = "none";
    if (event.target == confirmModal) confirmModal.style.display = "none";
    if (event.target == budgetModal) budgetModal.style.display = "none";
};

updateSummary();
updateTransactionList();
displayReports();