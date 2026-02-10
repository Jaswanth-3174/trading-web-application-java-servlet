function sellOrder() {

    fetch("/MyServletApp_war_exploded/api/orders/myStocks")
        .then(res => res.text())
        .then(text => {
            if (!text) throw new Error("Empty response");
            const response = JSON.parse(text);

            if (!response.success) {
                document.getElementById("content").innerHTML = response.message;
                return;
            }

            const stocks = response.data;

            if (stocks.length === 0) {
                document.getElementById("content").innerHTML =
                    "<h3>You don't own any stocks to sell</h3>";
                return;
            }

            let options = `<option value="">Select Stock</option>`;
            stocks.forEach(s => {
                options += `<option value="${s.name}">
                    ${s.name} (Available: ${s.qty})
                </option>`;
            });

            document.getElementById("content").innerHTML = `
                <h3>Place Sell Order</h3>

                <select id="stockName">${options}</select><br><br>
                <input type="number" id="quantity" placeholder="Quantity"><br><br>
                <input type="number" id="price" placeholder="Price"><br><br>

                <button onclick="sell()">Place Sell Order</button>
            `;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Error loading stocks</h3>";
        });
}

function sell() {

    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    if (!stockName || quantity <= 0 || price <= 0) {
        alert("Invalid input");
        return;
    }

    fetch("/MyServletApp_war_exploded/api/orders/sell", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body:
            "stockName=" + encodeURIComponent(stockName) +
            "&quantity=" + quantity +
            "&price=" + price
    })
        .then(res => res.text())
        .then(text => {
            if (!text) throw new Error("Empty response");
            const data = JSON.parse(text);

            if (!data.success) {
                document.getElementById("content").innerHTML =
                    `<h3 style="color:red">${data.message}</h3>`;
                return;
            }

            let html = `
            <h3>Sell Order Result</h3>
            <p>Order ID: ${data.orderId}</p>
            <p>Status: ${data.status}</p>
            <p>Remaining: ${data.remaining}</p>
        `;

            if (data.trade) {
                html += `
                <hr>
                <h4>Trade Executed</h4>
                <p>Buyer: ${data.trade.buyer}</p>
                <p>Seller: ${data.trade.seller}</p>
                <p>Stock: ${data.trade.stock}</p>
                <p>Qty: ${data.trade.quantity}</p>
                <p>Price: Rs.${data.trade.price}</p>
                <p>Total: Rs.${data.trade.total}</p>
            `;
            }

            document.getElementById("content").innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while placing sell order</h3>";
        });
}
