function viewStocks(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewStocks",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table =>{
            document.getElementById("content").innerHTML = table;
        })
}

function viewMyOrders(){
    console.log("JS FUNCTION RUNNING");

    fetch("/MyServletApp_war_exploded/dashboard?action=viewMyOrders", {
        method:"POST"
    })
        .then(res => res.json())
        .then(orders => {

            if(orders.length === 0){
                document.getElementById("content").innerHTML =
                    "<h3>No orders found</h3>";
                return;
            }

            let html = `
        <h3>My Orders</h3>
        <table border="1" cellpadding="8">
            <tr>
                <th>ID</th>
                <th>Stock</th>
                <th>Qty</th>
                <th>Price</th>
                <th>Type</th>
                <th>Modify</th>
                <th>Cancel</th>
            </tr>
        `;

            orders.forEach(o => {
                html += `
            <tr>
                <td>${o.id}</td>
                <td>${o.stock}</td>
                <td>${o.qty}</td>
                <td>${o.price}</td>
                <td>${o.type}</td>
                <td><button onclick="openModify(${o.id}, ${o.qty}, ${o.price})">✏️</button></td>
                <td><button onclick="cancelOrder(${o.id})">❌</button></td>
            </tr>
            `;
            });

            html += "</table>";

            document.getElementById("content").innerHTML = html;
        });
}