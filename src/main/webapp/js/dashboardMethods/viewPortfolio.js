function viewPortfolio(){
    fetch("/MyServletApp_war_exploded/api/account/portfolio")
        .then(res => res.json())
        .then(response => {
            if(!response.success){
                document.getElementById("content").innerHTML = response.message;
                return;
            }

            const stockholdings = response.data;

            if(stockholdings.length == 0){
                document.getElementById("content").innerHTML = "<h3>No stocks</h3>";
                return;
            }

            let html = `
                <h3>Your Portfolio</h3>
                <table border="1" cellpadding="8">
                <tr>
                    <th>Stock Name</th>
                    <th>Total</th>
                    <th>Reserved</th>
                    <th>Available</th>
                </tr>
            `;

            stockholdings.forEach(s =>{
                html += `
                    <tr>
                        <td>${s.stockName}</td>
                        <td>${s.total}</td>
                        <td>${s.reserved}</td>
                        <td>${s.available}</td>
                    </tr>
                `;
            })
            document.getElementById("content").innerHTML = html;
        })
        .catch(err => console.log(err));
}