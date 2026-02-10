function cancelOrder(id) {

    if (!confirm("Cancel order #" + id + "?")) return;

    fetch("/MyServletApp_war_exploded/api/orders/" + id, {
        method: "DELETE"
    })
        .then(res => res.text())
        .then(text => {
            if (!text) throw new Error("Empty response");
            const data = JSON.parse(text);

            if (data.success) {
                document.getElementById("content").innerHTML =
                    "<h3 style='color:green'>Order cancelled successfully!</h3>";
                setTimeout(viewMyOrders, 1000);
            } else {
                document.getElementById("content").innerHTML =
                    `<h3 style='color:red'>${data.message}</h3>`;
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById("content").innerHTML =
                "<h3>Server error while cancelling order</h3>";
        });
}
