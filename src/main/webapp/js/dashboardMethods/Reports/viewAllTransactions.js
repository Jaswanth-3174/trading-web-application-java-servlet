function viewAllTransactions() {
    fetch("/MyServletApp_war_exploded/dashboard/pages/Reports/viewAllTransactions.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            loadAllTransactions();
        });
}

function loadAllTransactions() {
    fetch("/MyServletApp_war_exploded/api/transactions/all")
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("noDataMsg").innerText = response.message;
                return;
            }

            const transactions = response.data;
            const tbody = document.getElementById("transactionsBody");

            tbody.innerHTML = "";
            if (transactions.length === 0) {
                document.getElementById("noDataMsg").innerText =
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
            console.error("Transaction load error:", err);
        });
}