function viewStocks(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewStocks",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table =>{
            document.getElementById("content").innerHTML = table;
        })
}

function viewMyOrders(){
    fetch("/MyServletApp_war_exploded/dashboard?action=viewMyOrders",{
        method:"POST"
    })
        .then(res => res.text())
        .then(table => {
            document.getElementById("content").innerHTML = table;
        });
}