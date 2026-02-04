function sellOrder(){

    fetch("/MyServletApp_war_exploded/dashboard?action=myStocks",{
        method:"POST"
    })
        .then(res => res.json())
        .then(stocks => {

            if(stocks.length === 0){
                document.getElementById("content").innerHTML =
                    "<h3>You don't own any stocks to sell</h3>";
                return;
            }

            let options = `<option value="">Select Stock</option>`;

            stocks.forEach(s=>{
                options += `<option value="${s.name}">
                ${s.name} (Available: ${s.qty})
            </option>`;
            });

            document.getElementById("content").innerHTML = `
            <h3>Place Sell Order</h3>

            <select id="stockName">
                ${options}
            </select><br><br>

            <input type="number" id="quantity" placeholder="Enter Quantity"><br><br>
            <input type="number" id="price" placeholder="Enter Price per share"><br><br>

            <button onclick="sell()">Place Sell Order</button>
        `;
        });
}

function sell(){

    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    fetch("/MyServletApp_war_exploded/dashboard?action=sellOrder" +
        "&stockName=" + stockName +
        "&quantity=" + quantity +
        "&price=" + price,{
        method:"POST"
    })
        .then(res => res.json())
        .then(data => {

            if(!data.success){
                document.getElementById("content").innerHTML =
                    `<h3 style="color:red">${data.error}</h3>`;
                return;
            }

            let html = `
            <h3>Sell Order Result</h3>
            <p><b>Order ID:</b> ${data.orderId}</p>
            <p><b>Status:</b> ${data.status}</p>
            <p><b>Remaining:</b> ${data.remaining}</p>
        `;

            if(data.trade){
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
        });
}
