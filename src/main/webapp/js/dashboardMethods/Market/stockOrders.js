function showStockOrderBook() {

    fetch("/MyServletApp_war_exploded/dashboard/pages/Market/stockOrderBook.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;

            loadStocks();
        });
}

function loadStocks() {

    fetch("/MyServletApp_war_exploded/api/market/stocks")
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                alert("Error loading stocks");
                return;
            }

            const dropdown = document.getElementById("stockDropdown");
            dropdown.innerHTML = `<option value=""> Select Stock </option>`;

            response.data.forEach(s => {
                dropdown.innerHTML += `
                    <option value="${s.name}">
                        ${s.name}
                    </option>
                `;
            });
        })
        .catch(err => {
            console.error("Stock load error:", err);
        });
}

function viewAllStockOrders() {

    const stock = document.getElementById("stockDropdown").value;

    if (!stock) {
        alert("Please select a stock");
        return;
    }

    fetch("/MyServletApp_war_exploded/api/market/orderbook", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "stock=" + stock
    })
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                alert(response.message);
                return;
            }

            document.getElementById("orderBookTitle").innerText = "Order Book: " + response.stock;

            const buyBody = document.getElementById("buyBody");
            const sellBody = document.getElementById("sellBody");

            buyBody.innerHTML = "";
            sellBody.innerHTML = "";

            // buy orders
            if (response.buyOrders.length === 0) {
                buyBody.innerHTML = `<tr><td colspan="4">No Buy Orders</td></tr>`;
            } else {
                response.buyOrders.forEach(o => {
                    buyBody.innerHTML += `
                        <tr>
                            <td>${o.id}</td>
                            <td>${o.user}</td>
                            <td>${o.qty}</td>
                            <td>${o.price}</td>
                        </tr>
                    `;
                });
            }

            // sell orders
            if (response.sellOrders.length === 0) {
                sellBody.innerHTML = `<tr><td colspan="4">No Sell Orders</td></tr>`;
            } else {
                response.sellOrders.forEach(o => {
                    sellBody.innerHTML += `
                        <tr>
                            <td>${o.id}</td>
                            <td>${o.user}</td>
                            <td>${o.qty}</td>
                            <td>${o.price}</td>
                        </tr>
                    `;
                });
            }

        })
        .catch(err => {
            console.error("Order book error:", err);
        });
}