function deleteMyAccount(){
    document.getElementById("content").innerHTML = `
        <h3>Type CONFIRM to delete your account</h3>
        <input id="confirmBox" placeholder="CONFIRM">
        <br><br>
        <button onclick="confirmDelete()">Delete</button>
    `;
}

function confirmDelete(){
    const data = document.getElementById("confirmBox").value;

    fetch("/MyServletApp_war_exploded/api/account/delete?data=" + data, {
        method: "POST"
    })
        .then(res => res.json())
        .then(response => {
            document.getElementById("content").innerHTML = response.message;

            if(response.success){
                setTimeout(() => {
                    window.location.replace("/MyServletApp_war_exploded/index.html");
                }, 3000);
            }
        });
}