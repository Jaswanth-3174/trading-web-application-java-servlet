fetch("/MyServletApp_war_exploded/dashboard", { method: "POST" })
    .then(res => res.text())
    .then(name => {
        document.getElementById("username").innerText =
            "Welcome, " + name + " | Dashboard";
    });


function showOrders(){
    document.getElementById("subnav").innerHTML = `
        <button onclick="buyOrder()">Buy</button>
        <button onclick="sellOrder()">Sell</button>
        <button onclick="modifyOrder()">Modify</button>
        <button onclick="cancelOrder()">Cancel</button>
        <button onclick="viewMyOrders()">My Orders</button>
    `;
}

function showMarket(){
    document.getElementById("subnav").innerHTML = `
        <button onclick="viewStocks()">Stocks</button>
        <button onclick="viewAllStockOrders()">Order Book</button>
    `;
}

function showAccount(){
    document.getElementById("subnav").innerHTML = `
        <button onclick="viewBalance()">Balance</button>
        <button onclick="addBalance()">Add Money</button>
        <button onclick="viewPortfolio()">Portfolio</button>
        <button onclick="deleteMyAccount()">Delete</button>
    `;
}

function showReports(){
    document.getElementById("subnav").innerHTML = `
        <button onclick="viewMyTransactions()">Transactions</button>
        <button onclick="viewAllTransactions()">View All Transactions</button>
    `;
}

function viewMyTransactions(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewMyTransactions",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}

function viewAllTransactions(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewAllTransactions",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}

function viewPortfolio(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewPortfolio",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}