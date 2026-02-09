function cancelOrder(id){

    if(!confirm("Cancel order #" + id + "?")) return;

    fetch("/MyServletApp_war_exploded/dashboard?action=cancelOrder&orderId=" + id,{
        method:"POST"
    })
        .then(res => res.json())
        .then(data => {

            if(data.success){

                document.getElementById("content").innerHTML =
                    "<h3 style='color:green'>Order cancelled successfully!</h3>";

                setTimeout(viewMyOrders, 1000);

            }else{
                document.getElementById("content").innerHTML =
                    "<h3 style='color:red'>" + data.error + "</h3>";
            }
        });
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