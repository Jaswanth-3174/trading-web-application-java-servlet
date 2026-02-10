function viewMyOrders() {

    fetch("/MyServletApp_war_exploded/api/orders")
        .then(res => res.text())
        .then(text => {
            if (!text) throw new Error("Empty response");
            const data = JSON.parse(text);

            if (!data.success) {
                document.getElementById("content").innerHTML =
                    `<h3>${data.message}</h3>`;
                return;
            }

            let html = `<h3>My Orders</h3>`;

            if (data.data.length === 0) {
                html += `<p>No orders found</p>`;
            } else {
                html += `
                <table border="1" cellpadding="8">
                <tr>
                    <th>ID</th><th>Stock</th><th>Qty</th>
                    <th>Price</th><th>Type</th><th>Action</th>
                </tr>`;

                data.data.forEach(o => {
                    html += `
                    <tr>
                        <td>${o.id}</td>
                        <td>${o.stock}</td>
                        <td>${o.qty}</td>
                        <td>${o.price}</td>
                        <td>${o.type}</td>
                        <td>
                            <button onclick="openModify(${o.id}, ${o.qty}, ${o.price})">Modify</button>
                            <button onclick="cancelOrder(${o.id})">Cancel</button>
                        </td>
                    </tr>`;
                });

                html += `</table>`;
            }

            document.getElementById("content").innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while loading orders</h3>";
        });
}
