async function sendForm(form) {
    const params = new URLSearchParams(new FormData(form));

    const res = await fetch("/MyServletApp_war_exploded/auth", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    });

    const message = await res.text();
    return { status: res.status, message };
}


const loginForm = document.getElementById("loginForm");
if(loginForm){
    loginForm.addEventListener("submit", async(e) => {
            e.preventDefault();

            const res = await sendForm(loginForm);
            if(res.status == 200){
                window.location.replace("/MyServletApp_war_exploded/dashboard");
            }else{
                showMessage(res.message, "error")
            }
        }
    );
}

const logoutForm = document.getElementById("logoutForm");
if (logoutForm) {
    logoutForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const res = await sendForm(logoutForm);

        if (res.status === 200) {
            window.location.replace("/MyServletApp_war_exploded/index.html");

        } else {
            showMessage(res.message, "error");
        }
    });
}

function showMessage(message, type){
    const el = document.getElementById("message");
    el.textContent = message;
    el.className = "message " + type;
}