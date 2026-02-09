function viewStocks(){
    fetch("/MyServletApp_war_exploded/api/account/stocks")
        .then(res => res.json())
        .then(response => {
            if(!response.success){
                document.getElementById("content").innerHTML = "Error loading stocks!";
                return;
            }

            let html = `
                <h3>Stocks</h3>
                <table border="1" cellpadding="8">
                    <tr>
                        <th>Stock Name</th>
                    </tr>
            `;

            response.data.forEach(s => {
                html += `
                    <tr>
                        <td>${s.name}</td>
                    </tr>
                `;
            });

            html += `</table>`;

            document.getElementById("content").innerHTML = html;
        });
}