fetch("/MyServletApp_war_exploded/dashboard", { method: "POST" })
    .then(res => {
        if (res.status === 401) {
            window.location.replace("/MyServletApp_war_exploded/index.html");
        }
    });

// Welcome name
fetch("/MyServletApp_war_exploded/dashboard", { method: "POST" })
    .then(res => res.text())
    .then(name => {
        document.getElementById("name").innerText =
            "Welcome, " + name + " | Dashboard";
    });

function showOrders() {
    document.getElementById("content").innerHTML = `
        <div>
            <h3>Orders</h3>
            <p>
                The Orders section serves as a centralized space to view and manage all your trading activities.
                It provides a clear overview of every buy and sell order you place, helping you stay informed
                about your market actions at all times.
            </p>
            <p>
                Each order displays important details such as stock name, order type, quantity, price,
                and current status. Whether an order is pending, successfully executed, or cancelled,
                you can easily track its progress without any confusion.
            </p>
            <p>
                This section also helps you review your past transactions, making it easier to analyze
                your trading decisions and performance over time.
            </p>
            <p>
                By offering real-time updates and organized records, the Orders page allows you to manage
                trades efficiently and make confident, well-informed decisions.
            </p>
        </div>
    `;

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="buyOrder()">Buy</button>
        <button type="button" onclick="sellOrder()">Sell</button>
<!--        <button type="button" onclick="modifyOrder()">Modify</button>-->
<!--        <button type="button" onclick="cancelOrder()">Cancel</button>-->
        <button type="button" onclick="viewMyOrders()">My Orders</button>
    `;
}

function showMarket(){
    document.getElementById("content").innerHTML = `
        <div>
            <h3>Market</h3>

            <p>
                The Market section provides access to live stock market data and available trading options.
                It helps you explore stocks, track price movements, and understand current market trends
                before placing an order.
            </p>

            <p>
                The Stocks Order Book displays real-time buy and sell orders for selected stocks.
                It shows bid and ask prices, quantities, and market depth, helping you analyze
                supply and demand effectively.
            </p>

            <p>
                Using the Select a Market option, you can choose different market segments or exchanges
                to view relevant stocks and trading information. This allows you to switch between markets
                easily and focus on your preferred trading environment.
            </p>

            <p>
                Overall, the Market section supports informed trading decisions by offering
                transparent pricing, real-time updates, and a structured view of market activity.
            </p>
        </div>
    `;

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="viewStocks()">Stocks</button>
        <button type="button" onclick="showStockOrderBook()">Order Book</button>
    `;
}

function showAccount(){
    document.getElementById("content").innerHTML = `
        <div>
            <h3>Accounts</h3>

            <p>
                The Accounts section helps you manage your trading account and funds efficiently.
                It provides a clear overview of your account details and available balance.
            </p>

            <p>
                Using the View Balance option, you can check your current funds at any time.
                The Add Money feature allows you to securely add funds to your account for trading.
            </p>

            <p>
                The Portfolio option shows all the stocks you currently hold, including quantities
                and investment details, helping you track your overall performance.
            </p>

            <p>
                With the Delete option, you can remove an account when it is no longer needed.
                Select an Account option to perform actions easily and manage your account
                in a simple and organized way.
            </p>
        </div>
    `;

    document.getElementById("subnav").innerHTML = `
        <button onclick="viewBalance()">View Balance</button>
        <button type="button" onclick="addBalance()">Add Money</button>
        <button type="button" onclick="viewPortfolio()">Portfolio</button>
        <button type="button" onclick="deleteMyAccount()">Delete</button>
    `;
}

function showReports(){
    document.getElementById("content").innerHTML = `
        <div>
            <h3>Reports</h3>

            <p>
                The Reports section provides detailed insights into your trading activity.
                It helps you review and analyze all financial records in a clear and organized manner.
            </p>

            <p>
                The Transactions option shows individual buy and sell records with complete details,
                including date, stock name, quantity, price, and transaction status.
            </p>

            <p>
                The All Transactions option gives a consolidated view of your entire transaction history.
                This helps you track performance over time and understand your overall trading behavior.
            </p>

            <p>
                Select a Report option to view specific transaction data and generate accurate
                reports for better financial analysis and decision-making.
            </p>
        </div>
    `;

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="viewMyTransactions()">Transactions</button>
        <button type="button" onclick="viewAllTransactions()">All Transactions</button>
    `;
}