function sellOrder() {

    fetch("/MyServletApp_war_exploded/dashboard/pages/orders/sellOrder.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            loadMyStocks();
        });
}

function loadMyStocks() {

    fetch("/MyServletApp_war_exploded/api/orders/myStocks")
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

            const dropdown = document.getElementById("sellStockName");

            stocks.forEach(s => {
                dropdown.innerHTML +=
                    `<option value="${s.name}">
                        ${s.name} (Available: ${s.qty})
                    </option>`;
            });
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Error loading stocks</h3>";
        });
}

function submitSellOrder() {

    const stockName = document.getElementById("sellStockName").value;
    const quantity = document.getElementById("sellQuantity").value;
    const price = document.getElementById("sellPrice").value;

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
        .then(res => res.json())
        .then(data => {

            if (!data.success) {
                alert(data.message);
                return;
            }

            document.getElementById("sellResult").style.display = "block";

            document.getElementById("sellOrderId").innerText = data.orderId;
            document.getElementById("sellStatus").innerText = data.status;
            document.getElementById("sellRemaining").innerText = data.remaining;

            if (data.trade) {

                document.getElementById("sellTradeSection").style.display = "block";

                document.getElementById("sellBuyer").innerText = data.trade.buyer;
                document.getElementById("sellSeller").innerText = data.trade.seller;
                document.getElementById("sellTradeStock").innerText = data.trade.stock;
                document.getElementById("sellTradeQty").innerText = data.trade.quantity;
                document.getElementById("sellTradePrice").innerText = data.trade.price;
                document.getElementById("sellTradeTotal").innerText = data.trade.total;
            }
        })
        .catch(err => {
            console.error(err);
            alert("Server error while placing sell order");
        });
}