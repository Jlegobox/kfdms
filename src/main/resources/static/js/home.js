function home_check_token() {
    // 如果lg_token存在，则直接登录
    var lg_token = sessionStorage['lg_token'];
    if (lg_token != null && lg_token.length > 10) {
        $.ajax({
            url: "/welcome",
            type: 'GET',
            beforeSend: function (xhr) {
                xhr.setRequestHeader('lg_token', lg_token);
            },
            success: function (result) {
                switch (result) {
                    case "error":
                        window.location.href = "login.html";
                }
            },
            error: function (result) {
                window.location.href = "login.html";
            }

        })
    }
}

function initHome() {
    $.ajax({
        url: "/initHome.ajax",
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token']);
        },
        success: function (result) {
            result = JSON.parse(result);
            switch (result["message"]) {
                case "success": {
                    let user = result["data"];
                    $("#accountName").text(user["username"]);
                    if (user["user_type"] === 0) { // 管理员
                        $("#manageMenu").css("display", "block")
                    }
                    break;
                }
                case "error":
                    sessionStorage.removeItem("lg_token");
                    window.location.href = "login.html";
            }
        },
        error: function (result) {
            window.location.href = "login.html";
        }
    })

}

function quitAccount() {
    // 可以加一个确认窗口
    sessionStorage.clear();
    location.href = "/login.html"
}

function showAccountInfo() {
    document.getElementById("myAccountSubMenu").click();
}