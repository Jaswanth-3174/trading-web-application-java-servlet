function cancelOrder(){
    document.getElementById("content").innerHTML = `
        <h3> CANCEL ORDER </h3>
        <input type="number" id="id" placeholder="Enter the Order ID to cancel">
        <button onclick="cancelId()">Cancel Order</button>
    `;
}

function cancelId(){
    const id = document.getElementById("id").value;
    if(id <= 0){
        alert("Enter valid Order ID");
        return;
    }
    if(!confirm("Confirm to cancel your order #" + id + " ")){
        return;
    }
    fetch("/MyServletApp_war_exploded/dashboard?action=cancelOrder&orderId=" + id,{
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;
        });
}