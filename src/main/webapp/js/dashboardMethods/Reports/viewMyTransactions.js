function viewMyTransactions() {
    fetch("/MyServletApp_war_exploded/dashboard/pages/Reports/viewMyTransactions.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;

            loadMyTransactions();  // 2️⃣ Load data
        });
}

function loadMyTransactions() {
    fetch("/MyServletApp_war_exploded/api/transactions")
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("myNoDataMsg").innerText =
                    response.message;
                return;
            }

            const transactions = response.data;
            const tbody = document.getElementById("myTransactionsBody");

            tbody.innerHTML = "";

            if (transactions.length === 0) {
                document.getElementById("myNoDataMsg").innerText =
                    "No transactions made";
                return;
            }

            transactions.forEach(t => {
                tbody.innerHTML += `
                    <tr>
                        <td>${t.stockId}</td>
                        <td>${t.stockName}</td>
                        <td>${t.buyerName}</td>
                        <td>${t.sellerName}</td>
                        <td>${t.quantity}</td>
                        <td>${t.price}</td>
                        <td>${t.total}</td>
                    </tr>
                `;
            });

        })
        .catch(err => {
            console.error("My transactions error:", err);
        });
}