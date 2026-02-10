function viewAllTransactions(){
    fetch("/MyServletApp_war_exploded/api/transactions/all")
        .then(res => res.json())
        .then(response => {
            if(!response.success){
                document.getElementById("content").innerHTML = response.message;
                return;
            }

            const transactions = response.data;

            if(transactions.length === 0){
                document.getElementById("content").innerHTML = "<h3>No transactions made</h3>";
                return;
            }

            let html = `
                <h3> Your Transactions </h3>
                <table border="1" cellpadding="8">
                <tr>
                    <th>Stock ID</th>
                    <th>Stock Name</th>
                    <th>Buyer Name</th>
                    <th>Seller Name</th>
                     <th>Quantity</th>
                    <th>Price</th>
                    <th>Total</th>
                </tr>
            `;

            transactions.forEach(t=>{
                html += `
                    <tr>
                        <td>${t.stockId}</td>
                        <td>${t.stockName}</td>
                        <td>${t.buyerName}</td>
                        <td>${t.sellerName}</td>
                        <td>${t.quantity}</td>
                        <td>${t.price}</td>
                        <td>${t.total}</td>
                    </tr>
                `;
            })
            document.getElementById("content").innerHTML = html;
        })
        .catch(err => console.error(err));
}