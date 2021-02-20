function home_check_token(){
    // 如果lg_token存在，则直接登录
    var lg_token = sessionStorage['lg_token'];
    if(lg_token!=null && lg_token.length>10){
        $.ajax({
            url:"/welcome",
            type:'GET',
            beforeSend:function(xhr){
                xhr.setRequestHeader('lg_token',lg_token);
            },
            success:function (result){
                switch(result){
                    case "error":
                        window.location.href="login.html";
                }
            },
            error:function (result){
                window.location.href="login.html";
            }

        })
    }
}