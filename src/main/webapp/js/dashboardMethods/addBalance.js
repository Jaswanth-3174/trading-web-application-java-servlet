function addBalance(){
    document.getElementById("content").innerHTML = `
        <h2>Add balance</h2>
        <input type="number" id="amount" placeholder="Enter the amount to add">
        <button onclick="addMoney()">ADD</button>
    `;
}

function addMoney(){
    const amount = document.getElementById("amount").value;

    fetch("/MyServletApp_war_exploded/api/account/addMoney?amount=" + amount, {
        method: "POST"
    })
        .then(res => res.json())
        .then(response => {

            if(!response.success){
                document.getElementById("content").innerHTML = response.message;
                return;
            }

            const b = response.data;

            document.getElementById("content").innerHTML = `
            <h3>${response.message}</h3>
            <p>Total Balance : Rs.${b.total}</p>
            <p>Available Balance : Rs.${b.available}</p>
            <p>Reserved Balance : Rs.${b.reserved}</p>
        `;
        });
}