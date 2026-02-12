function viewMyOrders() {
    fetch("/MyServletApp_war_exploded/dashboard/pages/Orders/viewMyOrders.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            loadMyOrders();
        });
}

function loadMyOrders() {

    fetch("/MyServletApp_war_exploded/api/orders")
        .then(res => res.json())
        .then(data => {

            if (!data.success) {
                document.getElementById("content").innerHTML =
                    `<h3>${data.message}</h3>`;
                return;
            }

            if (data.data.length === 0) {
                document.getElementById("noOrdersMessage").style.display = "block";
                return;
            }

            const table = document.getElementById("ordersTable");
            const tbody = document.getElementById("ordersTableBody");

            table.style.display = "table";

            data.data.forEach(o => {

                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${o.id}</td>
                    <td>${o.stock}</td>
                    <td>${o.qty}</td>
                    <td>${o.price}</td>
                    <td>${o.type}</td>
                    <td>
                        <button onclick="openModify(${o.id}, ${o.qty}, ${o.price})">
                            Modify
                        </button>
                        <button onclick="cancelOrder(${o.id})">
                            Cancel
                        </button>
                    </td>
                `;

                tbody.appendChild(row);
            });

        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while loading orders</h3>";
        });
}