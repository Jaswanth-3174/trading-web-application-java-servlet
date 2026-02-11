function deleteMyAccount() {
    fetch("/MyServletApp_war_exploded/dashboard/pages/Account/deleteAccount.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("content").innerHTML = html;
        });
}

function confirmDelete() {

    const data = document.getElementById("confirmBox").value;

    fetch("/MyServletApp_war_exploded/api/account/delete", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "data=" + data
    })
        .then(res => res.json())
        .then(response => {

            if (!response.success) {
                document.getElementById("deleteResult").innerHTML =
                    "<p style='color:red'>" + response.message + "</p>";
                return;
            }

            document.getElementById("deleteResult").innerHTML =
                "<p style='color:green'>" + response.message + "</p>";

            setTimeout(() => {
                window.location.replace("/MyServletApp_war_exploded/index.html");
            }, 2000);
        });
}
