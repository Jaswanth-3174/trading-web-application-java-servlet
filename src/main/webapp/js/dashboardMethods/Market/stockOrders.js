function showStockOrderBook(){

    fetch("/MyServletApp_war_exploded/api/account/stocks")
        .then(res => res.json())
        .then(response => {

            let html = `
                <h3>Select Stock</h3>
                <select id="stockDropdown">
                    <option value="">-- Select Stock --</option>
            `;

            response.data.forEach(s=>{
                html += `<option value="${s.name}">${s.name}</option>`;
            });

            html += `
                </select>
                <button type="button" onclick="viewAllStockOrders()">View Order Book</button>
                <div id="orderBookResult"></div>
            `;

            document.getElementById("content").innerHTML = html;
        });
}

function viewAllStockOrders(){

    const stock = document.getElementById("stockDropdown").value;

    if(!stock){
        alert("Please select a stock");
        return;
    }

    fetch(`/MyServletApp_war_exploded/api/account/orderbook?stock=${stock}`)
        .then(res => res.json())
        .then(response => {

            if(!response.success){
                document.getElementById("orderBookResult").innerHTML = response.message;
                return;
            }

            let html = `<h3>Order Book: ${response.stock}</h3>`;

            html += `<h4>BUY ORDERS</h4>`;

            if(response.buyOrders.length === 0){
                html += `<p>No active buy orders</p>`;
            } else {
                html += `
                <table border="1" cellpadding="8">
                <tr><th>ID</th><th>User</th><th>Qty</th><th>Price</th></tr>`;

                response.buyOrders.forEach(o=>{
                    html += `<tr>
                        <td>${o.id}</td>
                        <td>${o.user}</td>
                        <td>${o.qty}</td>
                        <td>${o.price}</td>
                    </tr>`;
                });

                html += `</table>`;
            }

            html += `<h4>SELL ORDERS</h4>`;
            if(response.sellOrders.length === 0){
                html += `<p>No active sell orders</p>`;
            } else {
                html += `
                <table border="1" cellpadding="8">
                <tr><th>ID</th><th>User</th><th>Qty</th><th>Price</th></tr>`;

                    response.sellOrders.forEach(o=>{
                        html += `<tr>
                            <td>${o.id}</td>
                            <td>${o.user}</td>
                            <td>${o.qty}</td>
                            <td>${o.price}</td>
                        </tr>`;
                    });

                    html += `</table>`;
            }
            document.getElementById("orderBookResult").innerHTML = html;
        });
}