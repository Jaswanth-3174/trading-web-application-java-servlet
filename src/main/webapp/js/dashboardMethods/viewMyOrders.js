function viewMyOrders(){

    fetch("/MyServletApp_war_exploded/api/account/orders")
        .then(res => res.json())
        .then(response => {
            if(!response.success){
                document.getElementById("content").innerHTML = response.message || "Error loading orders";
                return;
            }

            const orders = response.data;

            if(orders.length === 0){
                document.getElementById("content").innerHTML = "<h3>No orders found</h3>";
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
                        <td><button onclick="openModify(${o.id},${o.qty},${o.price})">Modify</button></td>
                        <td><button onclick="cancelOrder(${o.id})">Cancel</button></td>
                    </tr>
                `;
            });

            html += "</table>";

            document.getElementById("content").innerHTML = html;
        })
        .catch(err => console.error("API error:", err));
}