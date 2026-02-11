function addBalance() {
    fetch("/MyServletApp_war_exploded/dashboard/pages/Account/addBalance.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
        });
}

function addMoney() {

    const amount = document.getElementById("amount").value;

    fetch("/MyServletApp_war_exploded/api/account/balance", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "amount=" + amount
    })
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("result").innerHTML =
                    "<p style='color:red'>" + response.message + "</p>";
                return;
            }

            const b = response.data;

            document.getElementById("result").innerHTML = `
            <h3>${response.message}</h3>
            <p>Total: Rs.${b.total}</p>
            <p>Available: Rs.${b.available}</p>
            <p>Reserved: Rs.${b.reserved}</p>
        `;
        });
}
