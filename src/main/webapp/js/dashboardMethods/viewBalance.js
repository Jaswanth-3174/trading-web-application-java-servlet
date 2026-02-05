function viewBalance() {
    fetch("/MyServletApp_war_exploded/api/account/balance")
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("content").innerHTML = response.message || "Error";
                return;
            }

            const b = response.data;

            document.getElementById("content").innerHTML = `
                <h3>Balance</h3>
                <p>Total Balance : Rs.${b.total}</p>
                <p>Available Balance : Rs.${b.available}</p>
                <p>Reserved Balance : Rs.${b.reserved}</p>
            `;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML = "API Error";
        });
}