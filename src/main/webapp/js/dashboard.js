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

function showOrders(){
    document.getElementById("content").innerHTML = "<h3>Select an Orders option</h3>";

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="buyOrder()">Buy</button>
        <button type="button" onclick="sellOrder()">Sell</button>
<!--        <button type="button" onclick="modifyOrder()">Modify</button>-->
<!--        <button type="button" onclick="cancelOrder()">Cancel</button>-->
        <button type="button" onclick="viewMyOrders()">My Orders</button>
    `;
}

function showMarket(){
    document.getElementById("content").innerHTML = "<h3>Select a Market option</h3>";

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="viewStocks()">Stocks</button>
        <button type="button" onclick="showStockOrderBook()">Order Book</button>
    `;
}

function showAccount(){
    document.getElementById("content").innerHTML = "<h3>Select an Account option</h3>";

    document.getElementById("subnav").innerHTML = `
        <button onclick="viewBalance()">View Balance</button>
        <button type="button" onclick="addBalance()">Add Money</button>
        <button type="button" onclick="viewPortfolio()">Portfolio</button>
        <button type="button" onclick="deleteMyAccount()">Delete</button>
    `;
}

function showReports(){
    document.getElementById("content").innerHTML = "<h3>Select a Report option</h3>";

    document.getElementById("subnav").innerHTML = `
        <button type="button" onclick="viewMyTransactions()">Transactions</button>
        <button type="button" onclick="viewAllTransactions()">All Transactions</button>
    `;
}