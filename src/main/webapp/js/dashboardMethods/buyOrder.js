function buyOrder(){
    document.getElementById("content").innerHTML = `
        <h3>Place Buy Order</h3>
<!--        <input type="text" id="stockName" placeholder="Enter the stock name">-->
        <select id="stockName">
            <option value="">Select Stock</option>
            <option value="TCS">TCS</option>
            <option value="NIFTY">NIFTY</option>
            <option value="SBI">SBI</option>
            <option value="INFY">INFY</option>
        </select>
        <br><br>
        <input type="number" id="quantity" placeholder="Enter the Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the price per share"><br><br>
        <button onclick='buy()'>Place Buy Order</button>
    `
}

function buy() {
    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    if (!stockName) {
        alert("Select a stock first");
        return;
    }

    fetch("/MyServletApp_war_exploded/dashboard?action=buyOrder" +
        "&stockName=" + encodeURIComponent(stockName) +
        "&quantity=" + quantity +
        "&price=" + price, {
        method: "POST"
    })
        .then(res => res.json())
        .then(data => {
            if (!data.success) {
                content.innerHTML = "Order failed";
                return;
            }

            let html = `
                    <h3>Order Result</h3>
                    <p>Order ID: ${data.orderId}</p>
                `;

            if (data.trade) {
                html += `
                    <h4>Trade Executed</h4>
                    <p>Sold Quantity: ${data.trade.quantity}</p>
                    <p>Buyer: ${data.trade.buyer}</p>
                    <p>Seller: ${data.trade.seller}</p>    
                    <p>Stock Name: ${data.trade.stock}</p>
                    <p>Price: Rs.${data.trade.price}</p>
                    <p>Total: Rs.${data.trade.total}</p>
                `;
            } else {
                html += `
                    <p>Status: ${data.status}</p>
                    <p>Remaining Quantity: ${data.remaining}</p>
                `;
            }
            content.innerHTML = html;
        });
}