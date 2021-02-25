
function doRegister(){ //向指定url发送请求，有publickey验证
    var email = $("#email").val()
    var password = $("#password1").val()
    var verification_code = $("#verification_code").val()
    $.ajax({
        url:'Login/getPublicKey',
        type:'POST',
        data:{},
        dataType:'text',
        success:function (result){
            var publicKeyInfo = eval("("+result+")");
            var encrypt = new JSEncrypt();
            encrypt.setPublicKey(publicKeyInfo.publicKey);
            var RegisterInfo = '{email:"' + email +
                '",password:"'+password +
                '",verification_code:"'+verification_code +
                '",time:"' + publicKeyInfo.time + '"}';
            var encryptedData = encrypt.encrypt(RegisterInfo);
            sendRegisterInfo(encryptedData);
        },
        error:function (result){
            alert(result)
            alert("请求失败！");
        }
    })
}

function sendRegisterInfo(encryptedData){
    $.ajax({
        url:'Login/doRegister.ajax',
        type:'POST',
        data:{
            RegisterInfo:encryptedData
        },
        success:function (result){
            switch (result){
                case "success":
                    window.location.href="login.html";break;
                case "error":
                    alert("注册失败");break;
            }
        },
        error:function (result){
            alert(result);
        }

    })
}

function doLogin(){
    var email = $("#email").val()
    var password = $("#password").val()
    $.ajax({
        url:'Login/getPublicKey',
        type:'POST',
        data:{},
        dataType:'text',
        success:function (result){
            var publicKeyInfo = eval("("+result+")");
            var encrypt = new JSEncrypt();
            encrypt.setPublicKey(publicKeyInfo.publicKey);
            var LoginInfo = '{email:"' + email +
                '",password:"' + password +
                '",time:"' + publicKeyInfo.time + '"}';
            var encryptedData = encrypt.encrypt(LoginInfo);
            sendLoginInfo(encryptedData);
        },
        error:function (){
            alert("请求失败！")
        }
    })
}

function sendLoginInfo(encryptedData){
    $.ajax({
        url:'Login/doLogin.ajax',
        type:'POST',
        data:{
            LoginInfo:encryptedData
        },
        success:function (data,textStatus,xhr){
            switch (data){
                case "success":
                    sessionStorage['lg_token'] = xhr.getResponseHeader('lg_token');
                    // 前页是系统内页面，则回退
                    // let referrerUrl = document.referrer;
                    // if(referrerUrl.startsWith("localhost:8080"))
                    //     history.go(-1);

                    // 另一种发请求方式
                    // var new_xhr = new XMLHttpRequest();
                    // new_xhr.open('GET',"home.html")
                    // new_xhr.setRequestHeader('lg_token',sessionStorage['lg_token'])
                    // new_xhr.send(null);
                    window.location.href="home.html";
                    break;
                case "error":
                    alert("登录失败");
            }
        },
        error:function (result){
            alert(result);
        }

    })
}

function check_token(){
// 如果lg_token存在，则直接登录
    var lg_token = sessionStorage['lg_token'];
    if(lg_token!=null && lg_token.length>10){
        $.ajax({
            url:"/welcome",
            type:'GET',
            headers:{
                lg_token:lg_token
            },
            success:function (result){
                switch(result){
                    case "success":
                        window.location.href="home.html";
                }
            }

        })
    }
}


