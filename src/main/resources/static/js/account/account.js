function initAccountInfo() {
    $.ajax({
        url: "getAccountInfo.ajax",
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success: function (result) {
            result = JSON.parse(result);
            let message = result["message"];
            if (message === "success") {
                let data = result["data"];
                document.getElementById("userName").setAttribute("value", data["username"]);
                $('#sex').val(data["sex"])
                document.getElementById("studentId").setAttribute("value", data["student_id"]);
                $('#startYear').val(data["start_year"]);
                let birthday = new Date(data["birthday"]);
                let num = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"]
                let day = birthday.getDay() > 10 ? birthday.getDay() : num[birthday.getDay() - 1]
                let birthdayStr = birthday.getFullYear() + "-" + num[birthday.getMonth()] + "-" + day;
                document.getElementById("birthday").setAttribute("value", birthdayStr);
                document.getElementById("email").setAttribute("value", data["email"]);
                document.getElementById("phoneNumber").setAttribute("value", data["telephone"]);
                $('#userType').val(data["user_type"] === "0" ? "普通用户" : "管理员");
                document.getElementById("verification").setAttribute("value", data["verification"]);
            }
        }
    })

    $.ajax({
        url: "getInviteCode.ajax",
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success: function (result) {
            document.getElementById("inviteCode").setAttribute("value", result);
        },
        error: function (result) {
            alert("error")
        }
    })

}

function modifyAccountInfo() {
    let accountInfo = {}
    accountInfo["username"] = $("#userName").val();
    accountInfo["sex"] = $("#sex").val();
    accountInfo["studentId"] = $("#studentId").val();
    accountInfo["birthday"] = $("#birthday").val();
    accountInfo["startYear"] = $("#startYear").val();
    accountInfo["email"] = $("#email").val();
    accountInfo["telephone"] = $("#phoneNumber").val();
    let userType = $("#userType").val();
    if (userType === "管理员") {
        accountInfo["userType"] = 1;
    } else if (userType === "普通用户") {
        accountInfo["userType"] = 0;
    }

    $.ajax({
        url: "modifyAccountInfo.ajax",
        type: "POST",
        async: false,
        data: accountInfo,
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success: function (result) {
            alertConfirmTrans("修改成功")
            initAccountInfo();
        },
        error: function (result) {
            alertConfirmTrans(result);
        }
    })
}

function changePassword() {
    let oldPass = $("#oldPasswordStr").val();
    let newPass = $("#newPasswordStr").val();
    let newPass2 = $("#newPasswordStr2").val();
    if (newPass !== newPass2) {
        alertConfirmTrans("两次密码不相同！")
        return;
    }
    $.ajax({
        url: "changePassword.ajax",
        type: "POST",
        data: {
            "oldPass": oldPass,
            "newPass": newPass
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success: function (result) {
            alertConfirmTrans(result)
        },
        error: function (result) {
            alertConfirmTrans(result)
        }
    })
}

function eliminateAccount() {
    let password = $("#password").val();
    $.ajax({
        url: "eliminateAccount.ajax",
        type: "POST",
        data: {
            "password": password
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success: function (result) {
            alertConfirmTrans(result)
            switch (result) {
                case "success": {
                    sessionStorage.clear();
                    parent.location.reload();
                    break;
                }
            }
        },
        error: function (result) {
            alertConfirmTrans(result)
        }
    })
}

function refreshCode(){
    $.ajax({
        url: "refreshInviteCode.ajax",
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success:function (result){
            document.getElementById("inviteCode").setAttribute("value", result);
        },
        error:function (result){
            alertConfirmTrans("error");
        }
    })
}