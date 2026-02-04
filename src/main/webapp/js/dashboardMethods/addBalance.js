function addBalance(){
    document.getElementById("content").innerHTML = `
        <h2> Add balance </h2>
        <input type="number" id="amount" placeholder="Enter the amount to add">
        <button onclick='addMoney()'>ADD</button>
    `
}

function addMoney(){
    const amount = document.getElementById("amount").value;
    fetch("/MyServletApp_war_exploded/dashboard?action=addMoney&amount=" + amount, {
        method:"POST"
    })
        .then(res => res.text())
        .then(newBalance =>{
            document.getElementById("content").innerHTML = newBalance;
        })
}