function sellOrder() {

    fetch("/MyServletApp_war_exploded/api/account/myStocks")
        .then(res => res.json())
        .then(response => {

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
        });
}


function sell() {

    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    fetch("/MyServletApp_war_exploded/api/account/sellOrder" +
        "?stockName=" + stockName +
        "&quantity=" + quantity +
        "&price=" + price,
        { method: "POST" }
    )
        .then(res => res.json())
        .then(data => {

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
                    <p>Price: ₹${data.trade.price}</p>
                    <p>Total: ₹${data.trade.total}</p>
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

