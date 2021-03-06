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
                $('#userType').val(data["user_type"] === 1 ? "普通用户" : "管理员");
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

function initAccountDesk(){
    $.ajax({
        url:"/Account/getAllAccount.ajax",
        type:"POST",
        beforeSend:function (xhr){
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success:function (result){
            result = JSON.parse(result);
            switch (result.message){
                case "success": loadAccountDesk(result["data"]);break;
                case "authError":alertConfirmTrans("无权限");break;
            }
        }
    })
    getInviteMode();
}
var accountInfoTemplate ={
    "name":"",
    "sex":"",
    "studentId":"",
    "email":"",
    "telephone":"",
    "lastLogin":"",
    "verification":""
}
function loadAccountDesk(data){
    let tbody = document.getElementById("accountDesk_tbody");
    tbody.innerHTML = "";
    for(let i=0;i<data.length;i++){
        let row = createAccountDeskTableRow(data[i]);
        tbody.appendChild(row);
    }
}

function createAccountDeskTableRow(data){
    let row = document.createElement('tr');

    let name = document.createElement('td');
    name.innerHTML = "<a><b>" + data["username"] + "</b></a>";
    row.appendChild(name)

    let sex = document.createElement('td');
    let sexStr = data["sex"]===0?"男":"女"
    sex.innerHTML = "<a>" + sexStr + "</a>";
    row.appendChild(sex)

    let studentId = document.createElement('td');
    studentId.innerHTML = "<a>" + data["student_id"] + "</a>";
    row.appendChild(studentId)

    let email = document.createElement('td');
    email.innerHTML = "<a>" + data["email"] + "</a>";
    row.appendChild(email)

    let telephone = document.createElement('td');
    telephone.innerHTML = "<a>" + data["telephone"] + "</a>";
    row.appendChild(telephone)

    let lastLogin = document.createElement('td');
    lastLogin.innerHTML = "<a>" + data["last_login"] + "</a>";
    row.appendChild(lastLogin)

    let verification = document.createElement('td');
    verification.innerHTML = "<a>" + data["verification"] + "</a>";
    row.appendChild(verification)

    let forbiddenBtn = document.createElement('button')
    if(data["login_forbidden"] === 0){
        forbiddenBtn.innerHTML = '<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="forbiddenLogin('+data["id"]+')">禁止登录</button>'
    }else {
        forbiddenBtn.innerHTML = '<button type="button" class="layui-btn layui-btn-sm layui-btn-normal" onclick="forbiddenLogin('+data["id"]+')">开放登录</button>'
    }

    row.appendChild(forbiddenBtn)
    return row;
}

function forbiddenLogin(id){
    $.ajax({
        url:"/Account/setLoginForbidden.ajax",
        type:"POST",
        data:{
            "userId":id
        },
        beforeSend:function (xhr){
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success:function (result){
            initAccountDesk();
        },
        error:function (result){
            alertConfirmTrans("error");
        }
    })
}

function getInviteMode(){
    $.ajax({
        url:"/Account/getInviteMode.ajax",
        type:"POST",
        success:function (result){
            if(result!=null){
                switch (result){
                    case "0":{
                        $("#inviteModeBtn").text("开启邀请模式");
                        break;
                    }
                    case "1":{
                        $("#inviteModeBtn").text("关闭邀请模式");
                        break;
                    }
                }
            }
        },
        error:function (result){
            alertConfirmTrans("服务器异常，请刷新后再试")
        }
    })
}

function changeInviteMode(){
    $.ajax({
        url:"/Account/changeInviteMode.ajax",
        type:"GET",
        beforeSend:function (xhr){
            xhr.setRequestHeader('lg_token', sessionStorage['lg_token'])
        },
        success:function (result){
            if(result!=null){
                switch (result){
                    case "0":{
                        $("#inviteModeBtn").text("开启邀请模式");
                        break;
                    }
                    case "1":{
                        $("#inviteModeBtn").text("关闭邀请模式");
                        break;
                    }
                    default:{
                        alertConfirmTrans(result);
                    }
                }
            }
        },
        error:function (result){
            alertConfirmTrans("服务器异常，请刷新后再试")
        }
    })
}