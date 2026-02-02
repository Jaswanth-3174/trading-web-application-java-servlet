fetch("/MyServletApp_war_exploded/dashboard", { method: "POST" })
    .then(res => res.text())
    .then(name => {
        document.getElementById("name").innerText =
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
    `;
}

// 9. view balance
function viewBalance(){
        fetch("/MyServletApp_war_exploded/dashboard?action=balance", {
            method: "POST"
        })
            .then(res => res.json())
            .then(data => {
                if(data.error){
                    document.getElementById("content").innerHTML = data.error;
                    return;
                }
                document.getElementById("content").innerHTML = `
                <h3>Balance</h3>
                <p>Total Balance : Rs.${data.total}</p>
                <p>Available Balance : Rs.${data.available}</p>
                <p>Reserved Balance : Rs.${data.reserved}</p>
            `;
            });

    // fetch("/MyServletApp_war_exploded/dashboard?action=balance", {
    //     method: "POST"
    // })
    //     .then(res => res.text())
    //     .then(balance => {
    //         document.getElementById("content").innerHTML =
    //             "<h3>Balance : " + balance + "</h3>";
    //     });
}

function addBalance(){
    document.getElementById("content").innerHTML = `
        <h2> Add balance </h2>
        <input type="number" id="amount" placeholder="Enter the amount to add">
        <button onclick='addMoney()'>ADD</button>
    `
}

function addMoney(){
    const amount = document.getElementById("amount").value;
    fetch("/MyServletApp_war_exploded/dashboard?action=addMoney&amount=" + amount, {
        method:"POST"
    })
        .then(res => res.text())
        .then(newBalance =>{
            document.getElementById("content").innerHTML = newBalance;
        })
}

function buyOrder(){
    document.getElementById("content").innerHTML = `
        <input type="text" id="stockName" placeholder="Enter the stock name"><br><br>
        <input type="number" id="quantity" placeholder="Enter the Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the price per share"><br><br>
        <button onclick='buy()'>Place Buy Order</button>
    `
}

function buy(){
    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    fetch("/MyServletApp_war_exploded/dashboard?action=buyOrder" +
        "&stockName=" + encodeURIComponent(stockName) +
        "&quantity=" + quantity +
        "&price=" + price, {
        method: "POST"
    })
        .then(res => res.text())
        .then(buyOrder =>{
            document.getElementById("content").innerHTML =
                buyOrder;
        });
}

function sellOrder(){
    document.getElementById("content").innerHTML = `
        <input type="text" id="stockName" placeholder="Enter the stock name"><br><br>
        <input type="number" id="quantity" placeholder="Enter the Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the price per share"><br><br>
        <button onclick='sell()'>Place Sell Order</button>
    `
}

function sell(){
    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    fetch("/MyServletApp_war_exploded/dashboard?action=sellOrder" +
        "&stockName=" + encodeURIComponent(stockName) +
        "&quantity=" + quantity +
        "&price=" + price, {
        method: "POST"
    })
        .then(res => res.text())
        .then(sellOrder =>{
            document.getElementById("content").innerHTML =
                sellOrder;
        });
}

function viewStocks(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewStocks",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table =>{
            document.getElementById("content").innerHTML = table;
        })
}

function viewMyOrders(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewMyOrders",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}

function viewAllStockOrders(){
    document.getElementById("content").innerHTML = `
        <h2>Order Book</h2>
        <input id="stockName" placeholder="Enter the stock name">
        <button onclick="loadOrderBook()">View Order Book</button>
    `;
}

function loadOrderBook(){
    const stockName = document.getElementById("stockName").value;

    fetch("/MyServletApp_war_exploded/dashboard?action=viewAllStockOrders&stockName=" +
        encodeURIComponent(stockName), {
        method: "POST"
    })
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
        });
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

function deleteMyAccount(){
    document.getElementById("content").innerHTML = `
        <h3>Type Confirm to delete your account</h3>
        <input id="confirmBox">
        <button onclick="confirmDelete()">Delete</button>
    `;
}

function confirmDelete(){
    const data = document.getElementById("confirmBox").value;
    fetch("/MyServletApp_war_exploded/dashboard?action=deleteMyAccount&data=" + data, {
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;

            if(msg.includes("deleted")){
                setTimeout(()=>{
                    window.location.href="/MyServletApp_war_exploded/index.html";
                },5000);
            }
        });
}

function cancelOrder(){
    document.getElementById("content").innerHTML = `
        <h3> CANCEL ORDER </h3>
        <input type="number" id="id" placeholder="Enter the Order ID to cancel">
        <button onclick="cancelId()">Cancel Order</button>
    `;
}

function cancelId(){
    const id = document.getElementById("id").value;
    if(id <= 0){
        alert("Enter valid Order ID");
        return;
    }
    if(!confirm("Confirm to cancel your order #" + id + " ")){
        return;
    }
    fetch("/MyServletApp_war_exploded/dashboard?action=cancelOrder&orderId=" + id,{
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;
        });
}

function modifyOrder(){
    document.getElementById("content").innerHTML = `
        <h3>Modify Order</h3>
        <input type="number" id="orderId" placeholder="Enter the Order ID"><br><br>
        <input type="number" id="quantity" placeholder="Enter the New Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the New Price"><br><br>
        <button onclick="confirmModify()">Modify Order</button>
    `;
}

function confirmModify(){
    const orderId = document.getElementById("orderId").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    if(orderId <= 0 || quantity <= 0 || price <= 0){
        alert("Enter valid values");
        return;
    }

    if(!confirm("Modify Order #" + orderId + " ?")){
        return;
    }

    fetch("/MyServletApp_war_exploded/dashboard?action=modifyOrder" +
        "&orderId=" + orderId +
        "&quantity=" + quantity +
        "&price=" + price, {
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;
        });
}

