function viewBalance(){
    fetch("/MyServletApp_war_exploded/dashboard?action=balance", {
        method: "POST"
    })
        .then(res => res.json())
        .then(data => {
            if(data.error){
                document.getElementById("content").innerHTML = data.error;
                return;
            }
            document.getElementById("content").innerHTML = `
                <h3>Balance</h3>
                <p>Total Balance : Rs.${data.total}</p>
                <p>Available Balance : Rs.${data.available}</p>
                <p>Reserved Balance : Rs.${data.reserved}</p>
            `;
        });
}