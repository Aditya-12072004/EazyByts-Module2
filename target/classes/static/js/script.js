// A helper function for showing messages. This is defined in the global scope
// so it can be used by both login and dashboard logic.
function showMessage(msg, type) {
    const messageBox = document.getElementById('message');
    if (messageBox) {
        messageBox.textContent = msg;
        messageBox.className = `message-box ${type}`;
    }
}

// --- Login and Registration Logic ---
document.addEventListener('DOMContentLoaded', () => {
    // Variables for login/register page
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegisterLink = document.getElementById('showRegister');
    const showLoginLink = document.getElementById('showLogin');
    const loginSection = document.getElementById('login-form');
    const registerSection = document.getElementById('register-form');

    // Event listener to switch to register form
    if (showRegisterLink) {
        showRegisterLink.addEventListener('click', (e) => {
            e.preventDefault();
            loginSection.classList.remove('active');
            registerSection.classList.add('active');
        });
    }

    // Event listener to switch to login form
    if (showLoginLink) {
        showLoginLink.addEventListener('click', (e) => {
            e.preventDefault();
            registerSection.classList.remove('active');
            loginSection.classList.add('active');
        });
    }

    // Handle Registration Form Submission
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('registerUsername').value;
            const password = document.getElementById('registerPassword').value;

            try {
                const response = await fetch('/api/users/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();
                if (response.ok) {
                    showMessage('Registration successful! Please log in.', 'success');
                } else {
                    showMessage(`Error: ${data.message}`, 'error');
                }
            } catch (error) {
                showMessage('An unexpected error occurred.', 'error');
            }
        });
    }

    // Handle Login Form Submission
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;

            try {
                // 1. Pehle login try karein (jo sirf 'Login Successful' text bhejta hai)
                const response = await fetch('/api/users/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    // 2. Agar login successful hai (Status 200), to naye API se user ka data fetch karein
                    const userResponse = await fetch(`/api/users/username/${username}`); 

                    if(userResponse.ok) {
                        const user = await userResponse.json();
                        localStorage.setItem('loggedInUser', JSON.stringify(user));
                        window.location.href = '/dashboard.html'; // Redirect to dashboard
                    } else {
                         // Agar user data fetch nahi ho paya
                        showMessage('Login successful but could not retrieve user data.', 'error');
                    }
                } else {
                    // Agar Status 401 Unauthorized hai
                    const errorText = await response.text();
                    showMessage(`Login failed: ${errorText}`, 'error');
                }
            } catch (error) {
                showMessage('An unexpected error occurred. Check server logs.', 'error');
                console.error(error);
            }
        });
    }
});

// --- Dashboard Logic ---
document.addEventListener('DOMContentLoaded', () => {
    // Variables for the dashboard page (Combined Declaration)
    const userBalanceSpan = document.getElementById('userBalance');
    const tradeForm = document.getElementById('tradeForm');
    const tradeMessage = document.getElementById('tradeMessage');
    const portfolioTableBody = document.querySelector('#portfolioTable tbody');
    const stockSymbolInput = document.getElementById('stockSymbol');
    const quantityInput = document.getElementById('quantity');
    const logoutBtn = document.getElementById('logoutBtn');
    const livePriceElement = document.getElementById('livePrice');

    // Naye Analytics Variables
    const totalPnlSpan = document.getElementById('totalPnl');
    const pnlChartCanvas = document.getElementById('pnlChart');
    let pnlChartInstance = null; // Chart instance ko store karne ke liye

    // Naye Backtesting Variables (Theek kiya gaya: Ab yeh yahan dobara declare nahi honge)
    const backtestForm = document.getElementById('backtestForm');
    const backtestResultDiv = document.getElementById('backtestResult');
    const backtestSymbolInput = document.getElementById('backtestSymbol');
    const strategyNameSelect = document.getElementById('strategyName');
    const periodShortInput = document.getElementById('periodShort');
    const periodLongInput = document.getElementById('periodLong');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const backtestChartCanvas = document.getElementById('backtestChart');
    let backtestChartInstance = null; // Chart instance ko store karne ke liye

    // Get the logged-in user from localStorage
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));

    if (loggedInUser) {
        // Fetching both dashboard and analytics data on load
        fetchDashboardData(loggedInUser.id);
        fetchAnalyticsData(loggedInUser.id); // Naya call
    } else {
        if (window.location.pathname === '/dashboard.html') {
            window.location.href = '/index.html'; // Redirect to login if not logged in
        }
    }

    // Helper function to show messages on the trade form
    function showMessageInTradeForm(msg, type) {
        if (tradeMessage) {
            tradeMessage.textContent = msg;
            tradeMessage.className = `message-box ${type}`;
        }
    }

    // Function to draw the Backtest Chart
    function drawBacktestChart(result) {
        if (!backtestChartCanvas) return; 

        if (backtestChartInstance) {
            backtestChartInstance.destroy();
        }
        
        // Finnhub/Alpha Vantage historical data ko chart ke liye prepare karein
        const dates = result.t ? result.t.map(timestamp => new Date(timestamp * 1000).toLocaleDateString()) : [];
        const closePrices = result.c || []; 

        // Agar Alpha Vantage ka response ho jismein 't' (timestamp) na ho, to dates ko simple numbers se replace kar dein
        if(dates.length === 0 && closePrices.length > 0) {
            dates.push(...Array(closePrices.length).keys()); // Simple index numbers use karein
        }

        backtestChartInstance = new Chart(backtestChartCanvas.getContext('2d'), {
            type: 'line',
            data: {
                labels: dates,
                datasets: [{
                    label: `${result.symbol || 'Stock'} Closing Price`,
                    data: closePrices,
                    borderColor: '#bae8e8',
                    tension: 0.1,
                    pointRadius: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        display: false 
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Price ($)'
                        }
                    }
                }
            }
        });
    }

    // Function to draw the PnL Chart (Existing code)
    function drawChart(historyData, totalPnl) {
        if (!pnlChartCanvas) return;

        if (pnlChartInstance) {
            pnlChartInstance.destroy();
        }

        // Data ko chart ke liye prepare karein
        const labels = historyData.map(t => `${t.stockSymbol} (${t.timestamp.substring(5, 10)})`);
        const dataValues = historyData.map(t => {
            const amount = t.price * t.quantity;
            return t.transactionType === 'SELL' ? amount : -amount; // Buy ko negative dikhayein
        });
        const backgroundColors = historyData.map(t => t.transactionType === 'SELL' ? 'rgba(75, 192, 192, 0.7)' : 'rgba(255, 99, 132, 0.7)');

        pnlChartInstance = new Chart(pnlChartCanvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: `Transaction Amount (Total P&L: $${totalPnl.toFixed(2)})`,
                    data: dataValues,
                    backgroundColor: backgroundColors,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Amount ($)'
                        }
                    }
                }
            }
        });
    }
    
    // Naya function: Analytics data fetch karein (Existing code)
    async function fetchAnalyticsData(userId) {
        try {
            // 1. Profit/Loss data fetch karein
            const pnlResponse = await fetch(`/api/analytics/pnl/${userId}`);
            const pnlData = await pnlResponse.json();
            const pnlValue = pnlData.total_pnl || 0; 
            
            // P&L display update karein
            totalPnlSpan.textContent = `$${pnlValue.toFixed(2)}`;
            totalPnlSpan.style.color = pnlValue >= 0 ? 'green' : 'red';
            
            // 2. Transaction history fetch karein (Graph banane ke liye)
            const historyResponse = await fetch(`/api/analytics/history/${userId}`);
            const historyData = await historyResponse.json();

            // 3. Chart draw karein
            drawChart(historyData, pnlValue);

        } catch (error) {
            console.error('Failed to fetch analytics data:', error);
            totalPnlSpan.textContent = 'Error loading P&L';
        }
    }

    // Function to fetch user data and display on the dashboard (Existing code updated)
    async function fetchDashboardData(userId) {
        try {
            const userResponse = await fetch(`/api/users/${userId}`);
            const userData = await userResponse.json();
            userBalanceSpan.textContent = `$${userData.balance.toFixed(2)}`;

            const portfolioResponse = await fetch(`/api/portfolio/${userId}`);
            const portfolioData = await portfolioResponse.json();
            
            portfolioTableBody.innerHTML = '';
            
            if (portfolioData && portfolioData.length > 0) {
                portfolioData.forEach(item => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${item.stockSymbol}</td>
                        <td>${item.quantity}</td>
                        <td>$${item.averagePrice.toFixed(2)}</td>
                    `;
                    portfolioTableBody.appendChild(row);
                });
            } else {
                portfolioTableBody.innerHTML = `<tr><td colspan="3">No stocks in your portfolio.</td></tr>`;
            }
        } catch (error) {
            console.error('Failed to fetch dashboard data:', error);
            portfolioTableBody.innerHTML = `<tr><td colspan="3">Error loading portfolio data.</td></tr>`;
        }
    }
    
    // Event listener for live price display (Existing code)
    if (stockSymbolInput) {
        stockSymbolInput.addEventListener('keyup', async () => {
            const stockSymbol = stockSymbolInput.value.toUpperCase();
            
            if (stockSymbol.length > 1) { 
                try {
                    const response = await fetch(`/api/market/quote?symbol=${stockSymbol}`);
                    if (response.ok) {
                        const priceData = await response.json();
                        livePriceElement.textContent = `Live Price: $${priceData.price.toFixed(2)}`;
                    } else {
                        livePriceElement.textContent = `Could not get price`;
                    }
                } catch (error) {
                    livePriceElement.textContent = `Error fetching price`;
                    console.error('Error fetching live price:', error);
                }
            } else {
                livePriceElement.textContent = '';
            }
        });
    }

    // Handle Buy/Sell (Existing code)
    if (tradeForm) {
        tradeForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const buttonId = e.submitter.id;
            const userId = loggedInUser.id;
            const stockSymbol = stockSymbolInput.value;
            const quantity = parseInt(quantityInput.value);

            const tradeData = {
                userId,
                stockSymbol,
                quantity
            };

            const endpoint = buttonId === 'buyBtn' ? '/api/trading/buy' : '/api/trading/sell';

            try {
                const response = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(tradeData)
                });

                const result = await response.text(); 
                if (response.ok) {
                    showMessageInTradeForm(result, 'success');
                    fetchDashboardData(userId);    // Refresh portfolio/balance
                    fetchAnalyticsData(userId);    // Naya: Refresh analytics data
                } else {
                    showMessageInTradeForm(`Error: ${result}`, 'error');
                }
            } catch (error) {
                showMessageInTradeForm('An unexpected error occurred.', 'error');
            }
        });
    }

    // Naya: Backtesting Logic
    if (backtestForm) {
        backtestForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // 1. DTO (Data Transfer Object) ke liye data collect karen
            const requestData = {
                symbol: backtestSymbolInput.value.toUpperCase(),
                strategyName: strategyNameSelect.value,
                periodShort: parseInt(periodShortInput.value),
                periodLong: parseInt(periodLongInput.value),
                startDate: startDateInput.value, 
                endDate: endDateInput.value,     
            };
            
            // 2. Simple validation check
            if (!requestData.startDate || !requestData.endDate || new Date(requestData.startDate) >= new Date(requestData.endDate)) {
                backtestResultDiv.textContent = 'Error: Please select valid start and end dates.';
                backtestResultDiv.className = 'message-box error';
                return;
            }

            try {
                // 3. Backend API call karen
                const response = await fetch('/api/backtest/run', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(requestData)
                });

                const result = await response.json();

                if (response.ok) {
                    // Successful result handling
                    const pnl = result.profit_percent.toFixed(2);
                    backtestResultDiv.textContent = `Backtest Complete! Total P&L: ${pnl}%`;
                    backtestResultDiv.className = `message-box ${pnl >= 0 ? 'success' : 'error'}`;
                    
                    // Chart ko plot karein
                    drawBacktestChart(result); 

                } else {
                    backtestResultDiv.textContent = `Backtest Error: ${result.error || result.backtestResult}`;
                    backtestResultDiv.className = 'message-box error';
                }
            } catch (error) {
                backtestResultDiv.textContent = 'An unexpected error occurred during backtest. Check Finnhub data availability.';
                backtestResultDiv.className = 'message-box error';
                console.error('Backtest error:', error);
            }
        });
    }


    // Logout functionality
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('loggedInUser');
            window.location.href = '/index.html';
        });
    }
});