function viewPortfolio() {

    fetch("/MyServletApp_war_exploded/dashboard/pages/Account/viewPortfolio.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
            return fetch("/MyServletApp_war_exploded/api/account/portfolio");
        })
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("portfolioMessage").innerHTML =
                    "<p style='color:red'>" + response.message + "</p>";
                return;
            }

            const stockholdings = response.data;
            if (stockholdings.length === 0) {
                document.getElementById("portfolioMessage").innerHTML =
                    "<p>No stocks</p>";
                document.getElementById("portfolioTable").style.display = "none";
                return;
            }

            const tbody = document.getElementById("portfolioBody");
            stockholdings.forEach(s => {
                const row = `
                    <tr>
                        <td>${s.stockName}</td>
                        <td>${s.total}</td>
                        <td>${s.reserved}</td>
                        <td>${s.available}</td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        })
        .catch(err => {
            console.error(err);
            document.getElementById("portfolioMessage").innerHTML =
                "<p style='color:red'>API Error</p>";
        });
}