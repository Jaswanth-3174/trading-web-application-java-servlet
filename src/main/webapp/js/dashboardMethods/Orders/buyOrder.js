function buyOrder(){
    document.getElementById("content").innerHTML = `
        <h3>Place Buy Order</h3>

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

        <button onclick="buy()">Place Buy Order</button>
    `;
}

function buy() {
    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    if (!stockName || quantity <= 0 || price <= 0) {
        alert("Invalid input");
        return;
    }

    fetch("/MyServletApp_war_exploded/api/orders/buy", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body:
            "stockName=" + encodeURIComponent(stockName) +
            "&quantity=" + quantity +
            "&price=" + price
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(t => { throw new Error(t); });
            }
            return res.text();   // ✅ SAFE
        })
        .then(text => {
            if (!text) {
                throw new Error("Empty response from server");
            }

            const data = JSON.parse(text); // ✅ MANUAL PARSE

            if (!data.success) {
                document.getElementById("content").innerHTML =
                    data.message || "Order failed";
                return;
            }

            let html = `
            <h3>Order Result</h3>
            <p>Order ID: ${data.orderId}</p>
        `;

            if (data.status) {
                html += `<p>Status: ${data.status}</p>`;
            }
            if (data.remaining !== undefined) {
                html += `<p>Remaining: ${data.remaining}</p>`;
            }

            if (data.trade) {
                html += `
                <hr>
                <h4>Trade Executed</h4>
                <p>Buyer: ${data.trade.buyer}</p>
                <p>Seller: ${data.trade.seller}</p>
                <p>Stock: ${data.trade.stock}</p>
                <p>Qty: ${data.trade.quantity}</p>
                <p>Price: ₹${data.trade.price}</p>
                <p>Total: ₹${data.trade.total}</p>
            `;
            }

            document.getElementById("content").innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while placing order</h3>";
        });
}