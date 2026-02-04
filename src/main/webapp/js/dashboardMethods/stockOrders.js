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