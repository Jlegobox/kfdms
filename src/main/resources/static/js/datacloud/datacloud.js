var deskFolderId=0
// 分页参数
var pageParam = {
    currentPage:1,//当前页数
    PageSize:10, // 每页记录大小
    totalCount:0, // 总记录条数
    totalPage:0 // 总页数
}

// 加载页面时操作
$(
    getBaseFolderId(),
    refreshFileDesk(deskFolderId)
)

function getBaseFolderId(){
    if(deskFolderId !== 0){
        return ;
    }
    if(typeof(sessionStorage["deskFolderId"]) !=="undefined" && sessionStorage["deskFolderId"] !== ""){
        deskFolderId = sessionStorage["deskFolderId"]
        return ;
    }
    $.ajax({
        url:"DataCloud/getDeskFolderId.ajax",
        async:false, // 发送同步请求，保证deskFolder的有效性
        type:"POST",
        beforeSend:function (xhr){
            xhr.setRequestHeader('lg_token',sessionStorage["lg_token"])
        },
        success:function (result){
            switch (result){
                case "error":{
                    alert("Access Forbidden!");
                    sessionStorage["lg_token"]="";
                    top.location="login.html"
                    // window.navigator("login.html") // 不合适
                    break;
                }
                default:{
                    deskFolderId = JSON.parse(result);
                    sessionStorage["deskFolderId"] = deskFolderId;
                    break;
                }
            }

        },
        error:function (result){
            alert("Access Forbidden!");
            sessionStorage["lg_token"]="";
            top.location="login.html"
            // window.navigator("login.html") // 不合适
        }
    })
}

// 刷新整个DataCloud 包括导航栏等
function refreshFileDesk(folderId){
    deskFolderId = folderId
    refreshNavigation(folderId)
    refreshFolder(folderId,pageParam)
}
// 设置同步时间
function refreshTime(){
    var synTime = document.getElementById("synTime");
    if(synTime ==null)
        return ;
    var currentDate = new Date();
    var year = currentDate.getFullYear();
    var month = currentDate.getMonth() + 1;
    var day = currentDate.getDate();
    var hour = currentDate.getHours();
    var minute = currentDate.getMinutes();
    var second = currentDate.getSeconds();
    var tmpMonth = month;
    var week = ['日','一','二','三','四','五','六'];
    var timeContext="<a>云盘同步时间："+year+"/"+month+"/"+day+" 星期"+week[currentDate.getDay()]+" "+hour+":"+minute+":"+second+" </a>";
    synTime.innerHTML=timeContext;
}
// 请求导航栏数据
function refreshNavigation(deskFolderId){
    $.ajax({
        url:"DataCloud/getNavigation.ajax",
        type:"POST",
        data:{
            folderId:deskFolderId
        },
        beforeSend:function (xhr){
            xhr.setRequestHeader("lg_token",sessionStorage["lg_token"])
        },
        success:function (result){
            loadNavigation(result);
        },
        error:function (result){
            alert("error");
        }
    })
}
//加载导航栏
function loadNavigation(data){

    data = JSON.parse(data)
    var navigation = document.getElementById("fileDeskNavigation");
    var upperlayerBtn = document.getElementById("upperlayerBtn")
    // navigation.innerHTML="";
    // // root标签
    // var elem = document.createElement("a");
    var root_id = data[data.length -1].folder_id;
    if(data.length>1){
        navigation.innerHTML = "<a href='javascript:refreshFileDesk("+ root_id + ")'> root</a>";
    }else {
        // 当前为root文件夹
        navigation.innerHTML = "<a href='javascript:refreshFileDesk("+ root_id + ")'> root</a>";
        upperlayerBtn.setAttribute("onclick","javascript:refreshFileDesk("+ root_id + ")")
        return ;
    }

    // 分隔符 只创建一次，在重复append的时候，不是重复添加，而是调换了次序
    // var seperateElem = document.createElement('span');
    // seperateElem.innerHTML="/";
    // seperateElem.setAttribute("lay-separator","");

    if(data != null){
        for(var i=data.length - 2;i>=1;i--){
            var seperateElem = document.createElement('span');
            seperateElem.innerHTML="/";
            seperateElem.setAttribute("lay-separator","");
            navigation.appendChild(seperateElem);

            var elem = document.createElement("a");
            elem.innerHTML = data[i].folder_name;
            elem.setAttribute("href","javascript:refreshFileDesk("+data[i].folder_id+")")
            navigation.appendChild(elem);
        }
        upperlayerBtn.setAttribute("onclick","javascript:refreshFileDesk("+data[1].folder_id+")")
        // 最后一个文件夹,加粗处理
        var seperateElem = document.createElement('span');
        seperateElem.innerHTML="/";
        seperateElem.setAttribute("lay-separator","");
        navigation.appendChild(seperateElem);

        elem = document.createElement("a");
        elem.innerHTML = "<a href='javascript:refreshFileDesk("+deskFolderId+")'><cite>" + data[0].folder_name +"</cite></a>";
        navigation.appendChild(elem);
    }
}

// 获取页面总数
function getTotalCount(deskFolderId){
    $.ajax({
        url:"DataCloud/getTotalCount.ajax",
        type:"POST",
        data:{
            folderId:deskFolderId
        },
        beforeSend:function (xhr){
            xhr.setRequestHeader("lg_token",sessionStorage["lg_token"])
        },
        success:function (result){
            pageParam.totalCount = result;
        },
        error:function (result){
            alert("error");
        }
    })
}

function changePageParam(){
    pageParam.currentPage = $("#drumpPage").val();
    pageParam.PageSize = $("#pageSizeSelect").val();
    // 后面直接从后台得到，再refreshFolder中设置
    // pageParam.totalCount = getTotalCount(deskFolderId);
    // pageParam.totalPage = Math.ceil(pageParam.totalCount / pageParam.PageSize);
    refreshFolder(deskFolderId,pageParam);
}

function prePage(){
    // 后台数据可能变化，因此直接减一，其他交给后端去做
    pageParam.currentPage = pageParam.currentPage - 1;
    refreshFolder(deskFolderId,pageParam)
}

function nextPage(){
    pageParam.currentPage = pageParam.currentPage + 1;
    refreshFolder(deskFolderId,pageParam)
}

function refreshFolder(folderId,pageParam){
    // todo 权限验证
    $.ajax({
        url: "DataCloud/loadFolder.ajax",
        type: "POST",
        data:{
            folderId:folderId,
            pageParam:pageParam
            // currentPage:pageParam.currentPage,//当前页数
            // PageSize:pageParam.PageSize, // 每页记录大小
            // totalCount:pageParam.totalCount, // 总记录条数
            // totalPage:pageParam.totalPage // 总页数
        },
        beforeSend(xhr){
            xhr.setRequestHeader('lg_token',sessionStorage['lg_token'])
        },
        success:function (result){
            switch (result){
                case "error":
                    alert("error");break;
                default: {
                    // 记住页面
                    deskFolderId=folderId;
                    sessionStorage["deskFolderId"] = deskFolderId;
                    result = JSON.parse(result);
                    loadFolderData(result);
                    loadTotalCount(result.pageParam);
                    deskFolderId=folderId;
                    refreshTime();
                }
            }
        }
    })
}

function loadTotalCount(pageParam){
    var totalCountLabel = document.getElementById("totalCount")
    totalCountLabel.innerHTML="共有 "+pageParam.totalCount+" 条数据"

}

function loadFolderData(folderView){
    // folderView = JSON.parse(folderView)
    // 拿到文件夹、文件以及对应的权限列表
    var folders = folderView.folders;
    var files = folderView.files;
    var foldersPermission = folderView.foldersPermission;
    var filesPermission = folderView.filesPermission;
    $("#file_desk_tbody").html("");
    displayFoldersElem(folders,foldersPermission);
    displayFilesElem(files,filesPermission);
}

function displayFoldersElem(folders,foldersPermission){
    //获得表格
    var tbody = document.getElementById("file_desk_tbody");
    // 打包成展示对象，包括权限的设置等
    var elemParam = {
        fileId:"",
        fileName:"",
        fileType:"",
        fileShareType:"",
        fileModifiedTime:"",
        fileSize:"",
        fileDescription:"",
        isFolder:""
        // file_operation:""
    }
    for(var i=0;i<folders.length;i++){
        elemParam.fileId = folders[i].folder_id;
        elemParam.fileName = folders[i].folder_name;
        elemParam.fileType = "文件夹";
        elemParam.fileShareType = folders[i].folder_type;
        elemParam.fileModifiedTime = folders[i].folder_modified_time;
        elemParam.fileSize = "" // folders[i].folder_size;
        elemParam.fileDescription = folders[i].folder_description;
        elemParam.isFolder= 1
        var trow = getElemRow(elemParam);
        tbody.appendChild(trow);
    }
}

function displayFilesElem(files,filesPermission){
    //获得表格
    var tbody = document.getElementById("file_desk_tbody");
    // 打包成展示对象，包括权限的设置等
    var elemParam = {
        fileId:"",
        fileName:"",
        fileType:"",
        fileShareType:"",
        fileModifiedTime:"",
        fileSize:"",
        fileDescription:"",
        isFolder:""
        // file_operation:""
    }
    for(var i=0;i<files.length;i++){
        elemParam.fileId = files[i].file_id;
        elemParam.fileName = files[i].file_name;
        elemParam.fileType = files[i].data_type;
        elemParam.fileShareType = files[i].file_type;
        elemParam.fileModifiedTime = files[i].file_modified_time;
        elemParam.fileSize = files[i].file_size;
        elemParam.fileDescription = files[i].file_description;
        elemParam.isFolder=0
        var trow = getElemRow(elemParam);
        tbody.appendChild(trow);
    }
}

function getElemRow(elemParam){
    var row = document.createElement('tr'); //创建行

    var check_box = document.createElement('td'); // check_box 一种添加html的方式
    check_box.innerHTML = "<div class=\"layui-unselect layui-form-checkbox\" lay-skin=\"primary\" data-id='2'><i class=\"layui-icon\">&#xe605;</i></div>"
    row.appendChild(check_box)

    //  以下是另一种添加html的方式
    var fileName = document.createElement('td'); //名称 加粗 移动变色并添加下划线
    var fileNameLink = document.createElement('a');//点击进入文件夹
    fileNameLink.innerHTML = elemParam.fileName ;
    if(elemParam.isFolder){
        fileNameLink.innerHTML ="<b>"+elemParam.fileName+"</b>" ;
        fileNameLink.setAttribute("href","javascript:refreshFileDesk(\""+elemParam.fileId+"\");")
    }

    fileName.appendChild(fileNameLink);
    row.appendChild(fileName);

    var fileType = document.createElement('td'); //文件类型
    fileType.innerHTML = elemParam.fileType;
    row.appendChild(fileType);

    var fileShareType = document.createElement('td'); //共享类型
    var sharType = ""
    switch (elemParam.fileShareType){
        case -1: sharType="无权限";break;
        case 0: sharType="私有";break;
        case 1:sharType="公开";break;
        case 2:sharType="小组可见";break;
    }
    fileShareType.innerHTML = sharType;
    row.appendChild(fileShareType);


    var fileModifiedTime = document.createElement('td'); //修改时间
    fileModifiedTime.innerHTML = elemParam.fileModifiedTime;
    row.appendChild(fileModifiedTime);

    var fileSize = document.createElement('td'); //文件大小
    fileSize.innerHTML = elemParam.fileSize;
    row.appendChild(fileSize);

    var fileDescription = document.createElement('td'); //文件描述
    fileDescription.innerHTML = elemParam.fileDescription;
    row.appendChild(fileDescription);

    var fileOperation = document.createElement('td'); //根据权限添加操作目录
    createOperationBtn(fileOperation,elemParam);
    // operation.appendChild(rename_folder_btn());

    row.appendChild(fileOperation)

    return row
}

function createOperationBtn(fileOperation,elemParam){
    var disable = "layui-btn layui-btn-sm layui-btn-radius layui-btn-disabled"
    // 根据权限
    var downloadBtn = document.createElement('button');
    downloadBtn.innerHTML = "下载"
    downloadBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-normal")
    downloadBtn.setAttribute("onclick","downloadFile("+elemParam.fileId+")")

    var editBtn = document.createElement('button');
    editBtn.innerHTML = "编辑"
    editBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-normal")

    var previewBtn = document.createElement('button');
    previewBtn.innerHTML = "预览"
    previewBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-normal")

    var deleteBtn = document.createElement('button');
    deleteBtn.innerHTML = "删除"
    deleteBtn.setAttribute("class","layui-btn layui-btn-sm layui-btn-radius layui-btn-danger")
    deleteBtn.setAttribute("onclick","delFile("+elemParam.fileId+","+elemParam.isFolder+")")

    // 权限校验
    if(elemParam.isFolder){
        previewBtn.setAttribute("class",disable);
        downloadBtn.setAttribute("class",disable);
    }

    // 添加按钮
    fileOperation.appendChild(downloadBtn)
    fileOperation.appendChild(editBtn)
    fileOperation.appendChild(previewBtn)
    fileOperation.appendChild(deleteBtn)
}

/**
 *删除文件（根据fileType判断是文件or文件夹）
 */
function delFile(fileId,isFolder){

    $.ajax({
        url:"DataCloud/deleteFile.ajax",
        type:"POST",
        data:{
            fileId:fileId,
            fileType:isFolder
        },
        beforeSend:function (xhr){
            xhr.setRequestHeader("lg_token",sessionStorage["lg_token"])
        },
        success:function (result){
            switch (result){
                case "not empty":{
                    alert("文件夹不为空");
                    break;
                }
                case "sucess":{
                    if(isFolder)
                        alert("文件夹已删除");
                    else
                        alert("文件已被删除");
                    break;
                }
                case "error":{
                    alert("删除失败");
                }
            }

        },
        error:function (result){
            alert("sever error!");
            sessionStorage.clear();
            top.location="login.html";
        },
        complete:function (xhr,data){
            refreshFileDesk(deskFolderId);
        }
    })

}

function downloadFile(fileId){
    window.open("DataCloud/downloadFile.do?fileId=" + fileId)
    // $.ajax({
    //     url:"DataCloud/downloadFile.do",
    //     type:"POST",
    //     data:{
    //         fileId:fileId
    //     },
    //     beforeSend:function (xhr){
    //         xhr.setRequestHeader("lg_token",sessionStorage["lg_token"])
    //     },
    //     success:function (result){
    //         switch (result){
    //             case "not exists":{
    //                 alert("服务器文件破损，请联系管理员");
    //                 break;
    //             }
    //             case "sucess":{
    //                 break;
    //             }
    //             case "error":{
    //                 alert("下载失败")
    //             }
    //         }
    //
    //     },
    //     error:function (result){
    //         alert("sever error!");
    //         sessionStorage.clear();
    //         top.location="login.html";
    //     }
    // })
}

function createFolder(){
    var data = $('#new_folderInfo').serialize() + '&folder_description=' + $('#folder_description').val()
            + '&folderId=' + deskFolderId;
    $.ajax({
        url:"DataCloud/createFolder.ajax",
        type:'POST',
        data:data,
        async:false,
        dataType:'text',
        beforeSend:function (xhr){
            xhr.setRequestHeader('lg_token',sessionStorage['lg_token'])
        },
        success:function (result){
            alert(result)
        },
        error:function (result){
            alert("error")
        },
        complete:function (xhr,data){
            x_admin_close();
            // 调用refreshFileDesk()会显式不成功，可能是因为还在子窗口，因此直接使用刷新父窗口
            x_admin_father_reload();

        }

    })
}

function datacloud_close_refresh(){
    x_admin_close();
    // 找到父类的document再去找刷新按钮
    parent.document.getElementById("fileDeskRefresh").click();
}

