function modifyOrder(){
    document.getElementById("content").innerHTML = `
        <h3>Modify Order</h3>
        <input type="number" id="orderId" placeholder="Enter the Order ID"><br><br>
        <input type="number" id="quantity" placeholder="Enter the New Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the New Price"><br><br>
        <button onclick="confirmModify()">Modify Order</button>
    `;
}

function confirmModify(){
    const orderId = document.getElementById("orderId").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    if(orderId <= 0 || quantity <= 0 || price <= 0){
        alert("Enter valid values");
        return;
    }

    if(!confirm("Modify Order #" + orderId + " ?")){
        return;
    }

    fetch("/MyServletApp_war_exploded/dashboard?action=modifyOrder" +
        "&orderId=" + orderId +
        "&quantity=" + quantity +
        "&price=" + price, {
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;
        });
}