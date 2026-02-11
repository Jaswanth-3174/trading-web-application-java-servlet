function viewStocks() {

    fetch("/MyServletApp_war_exploded/dashboard/pages/Market/viewStocks.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            return fetch("/MyServletApp_war_exploded/api/market/stocks");
        })
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("stocksMessage").innerText = "Error loading stocks!";
                return;
            }

            const stocks = response.data;
            if (!stocks || stocks.length === 0) {
                document.getElementById("stocksMessage").innerText = "No stocks available";
                document.getElementById("stocksTable").style.display = "none";
                return;
            }

            const tbody = document.getElementById("stocksBody");
            stocks.forEach(s => {
                tbody.innerHTML += `<tr> <td>${s.name}</td> </tr>`;
            });
        })
        .catch(err => {
            console.error(err);
            document.getElementById("stocksMessage").innerText =
                "Server error";
        });
}
