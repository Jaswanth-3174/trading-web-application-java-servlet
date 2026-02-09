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

function openModify(id, qty, price){

    document.getElementById("content").innerHTML = `
        <h3>Modify Order #${id}</h3>

        Quantity:
        <button onclick="changeQty(-1)">-</button>
        <span id="qty">${qty}</span>
        <button onclick="changeQty(1)">+</button>

        <br><br>

        Price:
        <input type="number" id="price" value="${price}">

        <br><br>

        <button onclick="submitModify(${id})">Save</button>
    `;
}

function changeQty(delta){
    let q = parseInt(document.getElementById("qty").innerText);
    q = q + delta;
    if(q < 1) q = 1;
    document.getElementById("qty").innerText = q;
}

function submitModify(id){

    const qty = parseInt(document.getElementById("qty").innerText);
    const price = parseFloat(document.getElementById("price").value);

    fetch("/MyServletApp_war_exploded/dashboard?action=modifyOrder" +
        "&orderId=" + id +
        "&quantity=" + qty +
        "&price=" + price,{
        method:"POST"
    })
        .then(res => res.json())
        .then(data => {

            if(data.success){

                document.getElementById("content").innerHTML =
                    "<h3 style='color:green'>Order modified successfully!</h3>";

                setTimeout(viewMyOrders, 1000);

            }else{
                document.getElementById("content").innerHTML =
                    "<h3 style='color:red'>" + data.error + "</h3>";
            }
        });
}
