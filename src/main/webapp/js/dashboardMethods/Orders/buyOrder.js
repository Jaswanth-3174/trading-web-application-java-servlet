function buyOrder() {

    fetch("/MyServletApp_war_exploded/dashboard/pages/Orders/buyOrder.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            loadStocksForBuy();
        });
}

function loadStocksForBuy() {

    fetch("/MyServletApp_war_exploded/api/market/stocks")
        .then(res => res.json())
        .then(response => {

            if (!response.success) return;

            const dropdown = document.getElementById("buyStockName");

            response.data.forEach(s => {
                dropdown.innerHTML +=
                    `<option value="${s.name}">${s.name}</option>`;
            });
        });
}

function submitBuyOrder() {

    const stockName = document.getElementById("buyStockName").value;
    const quantity = document.getElementById("buyQuantity").value;
    const price = document.getElementById("buyPrice").value;

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
        .then(res => res.text())
        .then(text => {

            const data = JSON.parse(text);

            if (!data.success) {
                alert(data.message);
                return;
            }

            document.getElementById("buyResult").style.display = "block";

            document.getElementById("resultOrderId").innerText = data.orderId;
            document.getElementById("resultStatus").innerText = data.status;
            document.getElementById("resultRemaining").innerText = data.remaining;

            if (data.trade) {

                document.getElementById("tradeSection").style.display = "block";

                document.getElementById("tradeBuyer").innerText = data.trade.buyer;
                document.getElementById("tradeSeller").innerText = data.trade.seller;
                document.getElementById("tradeStock").innerText = data.trade.stock;
                document.getElementById("tradeQty").innerText = data.trade.quantity;
                document.getElementById("tradePrice").innerText = data.trade.price;
                document.getElementById("tradeTotal").innerText = data.trade.total;
            }
        });
}
