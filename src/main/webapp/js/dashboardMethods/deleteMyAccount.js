function deleteMyAccount(){
    document.getElementById("content").innerHTML = `
        <h3>Type Confirm to delete your account</h3>
        <input id="confirmBox">
        <button onclick="confirmDelete()">Delete</button>
    `;
}

function confirmDelete(){
    const data = document.getElementById("confirmBox").value;
    fetch("/MyServletApp_war_exploded/dashboard?action=deleteMyAccount&data=" + data, {
        method:"POST"
    })
        .then(res => res.text())
        .then(msg =>{
            document.getElementById("content").innerHTML = msg;

            if(msg.includes("deleted")){
                setTimeout(()=>{
                    window.location.href="/MyServletApp_war_exploded/index.html";
                },5000);
            }
        });
}