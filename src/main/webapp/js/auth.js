const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new URLSearchParams(new FormData(loginForm));

        const res = await fetch("/MyServletApp_war_exploded/auth", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData.toString()
        });

        if (res.status === 200) {
            window.location.replace("/MyServletApp_war_exploded/dashboard");
        } else {
            const message = await res.text();
            document.getElementById("message").textContent = message;
        }
    });
}

const signupForm = document.getElementById("signupForm");
if(signupForm){
    signupForm.addEventListener("submit", async(e) => {
        e.preventDefault();

        const formData = new URLSearchParams(new FormData(signupForm));
        const res = await fetch("/MyServletApp_war_exploded/auth", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: formData.toString()
        });
        if (res.status === 200) {
            window.location.replace("/MyServletApp_war_exploded/dashboard");
        } else {
            const message = await res.text();
            document.getElementById("message").textContent = message;
        }
    });
}

const logoutForm = document.getElementById("logoutForm");
if(logoutForm){
    logoutForm.addEventListener("submit", async(e) => {
        e.preventDefault();

        const formData = new URLSearchParams(new FormData(logoutForm));
        const res = await fetch("/MyServletApp_war_exploded/auth", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: formData.toString()
        });

        if(res.status == 200){
            window.location.href = "/MyServletApp_war_exploded/index.html";
        }
    });
}
