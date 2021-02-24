function initFileShareCreateHtml() {
    $("#limitDiv").click(function () {
        $("#limitNumDiv").css("display", "block");
    })
    $("#unLimitDiv").click(function () {
        $("#limitNumDiv").css("display", "none");
    })
    $("#randAccessCodeDiv").click(function () {
        $("#customAccessCodeStrDiv").css("display", "none");
    })
    $("#customAccessCodeDiv").click(function () {
        $("#customAccessCodeStrDiv").css("display", "block");
    })
    let shareFileId = sessionStorage["shareFileId"];
    let shareFileType = sessionStorage["shareFileType"];
    sessionStorage.removeItem("shareFileId")
    sessionStorage.removeItem("shareFileType");
    document.getElementById("createShareLinkBtn").setAttribute("onclick", "createShareLink(" + shareFileId + "," + shareFileType + ")")
}

function createShareLink(fileId, isFolder) {
    let fileShareLinkInfo = {};
    if (isFolder) {
        fileShareLinkInfo['fileId'] = -1;
        fileShareLinkInfo['folderId'] = fileId;
    } else {
        fileShareLinkInfo['fileId'] = fileId;
        fileShareLinkInfo['folderId'] = -1;
    }
    let info = getJsonFromForm("fileShareLinkInfo"); // 得到的value均为字符串
    if (info["accessCodeType"] === "1") {
        let customAccessCode = $("#customAccessCodeStr").val();
        if (customAccessCode.length !== 4) {
            alert("请输入四位自定义提取码")
            return;
        }
        fileShareLinkInfo["accessCode"] = $("#customAccessCodeStr").val();
    } else {
        fileShareLinkInfo["accessCode"] = -1;
    }
    if (info["visitLimitType"] === "1") {
        let visitLimit = null
        try {
            visitLimit = parseInt($("#limitNum").val());
        } catch (e) {
            alert("请输入正确限制访问人数")
        }
        if (visitLimit < 1 || visitLimit > 10) {
            alert("请输入正确限制访问人数");
            return;
        }
        fileShareLinkInfo["visitLimit"] = visitLimit;
    } else {
        fileShareLinkInfo["visitLimit"] = -1;
    }
    fileShareLinkInfo["validPeriod"] = info["validTime"];
    $.ajax({
        url: "createLink.ajax",
        type: "POST",
        data: fileShareLinkInfo,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("lg_token", sessionStorage["lg_token"])
        },
        success: function (result) {
            result = JSON.parse(result);
            let shareLink = result["dataMap"]["shareLink"];
            let accessCode = result["dataMap"]["accessCode"];
            $("#fileShareLinkInfo").css("display","none");
            $("#createSuccessForm").css("display","block");
            $("#shareLinkStr").val(shareLink);
            $("#accessCodeStr").val(accessCode)
            let content = "链接："+shareLink+" \n" +
                "提取码："+accessCode+" \n"
            copyToClipBord(content);
        },
        error: function (result) {
            alert("error")
        }
    })
}

function getShareMessage(shareLink, accessCode){
    return "链接："+shareLink+" \n" +
        "提取码："+accessCode+" \n";
}


function copyShareLink(){
    let shareLink = $("#shareLinkStr").val();
    let accessCode = $("#accessCodeStr").val();
    let content = getShareMessage(shareLink, accessCode)
    alertConfirmTrans("链接已复制到剪切板！",1000)
    copyToClipBord(content);
}

function getJsonFromForm(id) { // 得到的value均为字符串
    let infoArray = $("#" + id).serializeArray();
    let info = {};
    for (let i = 0; i < infoArray.length; i++) {
        info[infoArray[i].name] = infoArray[i].value
    }
    return info;
}

function copyToClipBord(content){
    let aux = document.createElement("input");
    aux.setAttribute("value", content);
    document.body.appendChild(aux);
    aux.select();
    document.execCommand("copy");
    document.body.removeChild(aux);

}

function initFileShareLinkListHtml(){
    // 保证导航的面包屑class起作用，不然默认隐藏
    layui.use('element', function(){
        var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块

        //监听导航点击
        element.on('nav(demo)', function(elem){
            //console.log(elem)
            layer.msg(elem.text());
        });
    });

    $.ajax({
        url:"getShareLinkList.ajax",
        type:"POST",
        beforeSend:function (xhr){
            xhr.setRequestHeader("lg_token",sessionStorage["lg_token"])
        },
        success:function (result){
            result = JSON.parse(result)
            loadFileShareLinkList(result.data)
        },
        error:function (result){
            alert("error")
        }
    })
}

var shareLinkTemplate = {
    "shareLogId":0,
    "fileName":0, // fileName或者folderName为空则不展示
    "folderName":0,
    "createTime":0,
    "expiredTime":0,
    "shareLink":0,
    "accessCode":0,
    "status":0,
    "visitNum":0,
    "visitLimit":0
}

function loadFileShareLinkList(shareLinkList){
    let tbody = document.getElementById("fileShareLink_tbody");
    tbody.innerHTML = "";
    for(let i=0;i<shareLinkList.length;i++){
        let row = createFileShareLinkTableRow(shareLinkList[i]);
        tbody.appendChild(row);
    }
}

function createFileShareLinkTableRow(shareLink){
    let row = document.createElement('tr');

    let checkBox = document.createElement('td');
    checkBox.innerHTML = "<div class=\"layui-unselect layui-form-checkbox\" lay-skin=\"primary\" data-id='2'><i class=\"layui-icon\">&#xe605;</i></div>";
    row.appendChild(checkBox);

    let fileName = document.createElement('td');
    fileName.innerHTML = "<a><b>"+shareLink.fileName+"</b></a>";
    row.appendChild(fileName)

    let createTime = document.createElement('td');
    createTime.innerText = shareLink.createTime;
    row.appendChild(createTime)

    let expireTime = document.createElement('td');
    if(shareLink.expiredTime == null || shareLink.expiredTime === "undefined"){
        expireTime.innerText = "无期限";
    }else {
        expireTime.innerText = shareLink.expiredTime;
    }
    row.appendChild(expireTime)

    let visitNum = document.createElement('td');
    visitNum.innerText = shareLink.visitNum;
    row.appendChild(visitNum)

    let visitLimit = document.createElement('td');
    if(shareLink.visitLimit == null || shareLink.visitLimit === "undefined" || shareLink.visitLimit === -1){
        visitLimit.innerText = "无限制";
    }else {
        visitLimit.innerText = shareLink.visitLimit;
    }
    row.appendChild(visitLimit)

    // 构造操作按钮
    let operationBtn = document.createElement('td');
    // operationBtn.setAttribute("class","layui-btn-group")
    let copyBtn = document.createElement('button')
    copyBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-normal")
    copyBtn.addEventListener('click',function (){
        let content = getShareMessage(shareLink.shareLink,shareLink.accessCode);
        copyToClipBord(content);
        alertConfirmTrans("已复制到粘贴板",1000);
    })
    copyBtn.innerText = "复制链接"
    operationBtn.appendChild(copyBtn);
    let cancelBtn = document.createElement('button')
    cancelBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-danger")
    cancelBtn.setAttribute("onclick","cancelShareLink("+shareLink.shareLogId+")")
    cancelBtn.innerHTML = "取销链接"
    operationBtn.appendChild(cancelBtn)

    row.appendChild(operationBtn)
    return row;
}


function cancelShareLink(shareLogId){
    alertConfirmTrans("取消链接")
}