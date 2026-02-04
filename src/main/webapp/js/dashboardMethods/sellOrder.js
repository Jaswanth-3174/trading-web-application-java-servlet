function sellOrder(){
    document.getElementById("content").innerHTML = `
        <input type="text" id="stockName" placeholder="Enter the stock name"><br><br>
        <input type="number" id="quantity" placeholder="Enter the Quantity"><br><br>
        <input type="number" id="price" placeholder="Enter the price per share"><br><br>
        <button onclick='sell()'>Place Sell Order</button>
    `
}

function sell(){
    const stockName = document.getElementById("stockName").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;

    fetch("/MyServletApp_war_exploded/dashboard?action=sellOrder" +
        "&stockName=" + encodeURIComponent(stockName) +
        "&quantity=" + quantity +
        "&price=" + price, {
        method: "POST"
    })
        .then(res => res.text())
        .then(sellOrder =>{
            document.getElementById("content").innerHTML =
                sellOrder;
        });
}