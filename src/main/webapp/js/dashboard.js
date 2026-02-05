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
        <button type="button" onclick="viewAllStockOrders()">Order Book</button>
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

// VIEW MY ORDERS

function viewMyOrders(){

    fetch("/MyServletApp_war_exploded/dashboard?action=viewMyOrders", {
        method:"POST"
    })
        .then(res => res.json())
        .then(orders => {

            if(orders.length === 0){
                document.getElementById("content").innerHTML =
                    "<h3>No orders found</h3>";
                return;
            }

            let html = `
        <h3>My Orders</h3>
        <table border="1" cellpadding="8">
            <tr>
                <th>ID</th>
                <th>Stock</th>
                <th>Qty</th>
                <th>Price</th>
                <th>Type</th>
                <th>Modify</th>
                <th>Cancel</th>
            </tr>
        `;

            orders.forEach(o => {
                html += `
            <tr>
                <td>${o.id}</td>
                <td>${o.stock}</td>
                <td>${o.qty}</td>
                <td>${o.price}</td>
                <td>${o.type}</td>
                <td>
                    <button type="button"
                        onclick="openModify(${o.id},${o.qty},${o.price})">
                        Modify
                    </button>
                </td>
                <td>
                    <button type="button"
                        onclick="cancelOrder(${o.id})">
                        Cancel
                    </button>
                </td>
            </tr>`;
            });

            html += "</table>";

            document.getElementById("content").innerHTML = html;
        });
}

// STOCKS
function viewStocks(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewStocks",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table =>{
            document.getElementById("content").innerHTML = table;
        });
}

// TRANSACTIONS
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

//  PORTFOLIO
function viewPortfolio(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewPortfolio",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}
