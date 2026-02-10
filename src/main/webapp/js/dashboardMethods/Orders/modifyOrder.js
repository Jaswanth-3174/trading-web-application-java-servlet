// function modifyOrder(){
//     document.getElementById("content").innerHTML = `
//         <h3>Modify Order</h3>
//         <input type="number" id="orderId" placeholder="Enter the Order ID"><br><br>
//         <input type="number" id="quantity" placeholder="Enter the New Quantity"><br><br>
//         <input type="number" id="price" placeholder="Enter the New Price"><br><br>
//         <button onclick="confirmModify()">Modify Order</button>
//     `;
// }

// function confirmModify(){
//
//     const orderId = document.getElementById("orderId").value;
//     const quantity = document.getElementById("quantity").value;
//     const price = document.getElementById("price").value;
//
//     if (orderId <= 0 || quantity <= 0 || price <= 0) {
//         alert("Enter valid values");
//         return;
//     }
//
//     if (!confirm("Modify Order #" + orderId + "?")) return;
//
//     fetch("/MyServletApp_war_exploded/api/orders/" + orderId, {
//         method: "PUT",
//         headers: {
//             "Content-Type": "application/x-www-form-urlencoded"
//         },
//         body:
//             "quantity=" + quantity +
//             "&price=" + price
//     })
//         .then(res => res.text())
//         .then(text => {
//             if (!text) throw new Error("Empty response");
//             const data = JSON.parse(text);
//
//             if (data.success) {
//                 document.getElementById("content").innerHTML =
//                     "<h3 style='color:green'>Order modified successfully!</h3>";
//                 setTimeout(viewMyOrders, 1000);
//             } else {
//                 document.getElementById("content").innerHTML =
//                     "<h3 style='color:red'>" + data.message + "</h3>";
//             }
//         })
//         .catch(err => {
//             console.error(err);
//             document.getElementById("content").innerHTML =
//                 "<h3>Server error while modifying order</h3>";
//         });
// }

function openModify(id, qty, price){

    document.getElementById("content").innerHTML = `
        <h3>Modify Order #${id}</h3>

        Quantity:
        <button onclick="changeQty(-1)">-</button>
        <span id="qty">${qty}</span>
        <button onclick="changeQty(1)">+</button><br><br>

        Price:<input type="number" id="price" value="${price}"><br><br>

        <button onclick="submitModify(${id})">Save</button>
    `;
}

function changeQty(delta){
    let q = parseInt(document.getElementById("qty").innerText);
    q = q + delta;
    if(q < 1) q = 1;
    document.getElementById("qty").innerText = q;
}

function submitModify(id) {

    const qty = parseInt(document.getElementById("qty").innerText);
    const price = parseFloat(document.getElementById("price").value);

    fetch("/MyServletApp_war_exploded/api/orders/" + id, {
        method: "PUT",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "quantity=" + qty + "&price=" + price
    })
        .then(res => res.text())
        .then(text => {
            if (!text) throw new Error("Empty response");
            const data = JSON.parse(text);

            if (data.success) {
                document.getElementById("content").innerHTML =
                    "<h3 style='color:green'>Order modified successfully!</h3>";
                setTimeout(viewMyOrders, 1000);
            } else {
                document.getElementById("content").innerHTML =
                    `<h3 style='color:red'>${data.message}</h3>`;
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while modifying order</h3>";
        });
}

