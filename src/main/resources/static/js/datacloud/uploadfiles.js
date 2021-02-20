// 相关变量
var uploader; // 保存WebUploader容器
var status; // uploader目前状态
var uploaderToken; // 上传权限验证
var current_location;

function init(){
    uploaderToken = sessionStorage["lg_token"]
    current_location = sessionStorage["deskFolderId"] // 使用sessionStorage实现打开就是上次
    createUploader();
}

function createUploader(){ //初始化
    // todo 事件全部调用注册插件进行请求处理
    // todo 利用File.StatusChange事件来控制文件状态改变。这里因为简单直接保存文件状态
    // 注册插件
    WebUploader.Uploader.register({
        'before-send-file':'checkFileUploaded', // 分片前进行，此时还未分片
        'before-send':'sendSlice' // 比on("uploadBeforeSend")早
    },{
        checkFileUploaded:function (file){
            console.log("before-send-file")
            let owner = this.owner; // 此uploader对象
            let options = this.options; // webUploader的各种参数
            let deferred = WebUploader.Deferred();
            let uploadFile = file.source.getSource(); // 转换为file对象，file对象继承自blob
            let md5Promise = calMD5(uploadFile); // MD5计算，返回Promise容器
            var fileInfo = {};
            fileInfo["name"] = file.name;
            fileInfo["size"] = file.size;
            fileInfo["fileType"] = file.ext;


            $("#"+file.id+"Status").html("解析本地文件中...")

            md5Promise.then(function (md5) { // MD5计算完成
                fileInfo["uploadFileMD5"] = md5;
                fileInfo["folderId"] = current_location;
                // MD5服务器校验
                $.ajax({
                    url:"DataCloud/checkUploadFile.ajax",
                    type:"POST",
                    sync:false,
                    data:fileInfo,
                    headers:{
                      "lg_token":uploaderToken
                    },
                    success:function (result){
                        fileInfo["uploadFileExists"] = result;
                        file["fileInfo"]= fileInfo;
                        switch (result){
                            case "permit":{
                                // 需要上传，继续后续流程
                                deferred.resolve();// 调用使得webuploader可以继续往下走
                                break;
                            }
                            case "exists":{
                                // 跳过文件
                                deferred.reject();
                                break;
                            }
                            case "duplicateFileName":{
                                alert("文件名重复！")
                                deferred.reject();
                                file["StatusText"] = "文件名重复！取消上传"
                                $("#"+file.id+"Status").html("文件名重复！取消上传")
                                uploader.cancelFile(file);
                            }
                        }

                    },
                    error:function (error){
                        console.log(error)
                    }
                })
            })
            return deferred.promise(); //最后需要返回Promise
        },

        sendSlice:function (block){
            // 保证在此分片上传前进行此命令处理且处理完成后才会上传。但是多线程情况会抢占共有属性例如options
            console.log("register before send")
            var options = this.options;
            var deferred = WebUploader.Deferred();
            var uploadFile = block.blob.getSource();
            var fileInfo = {} // 使用新建fileInfo的方式避免引用
            fileInfo["name"] = block.file.name;
            fileInfo["size"] = block.file.size;
            fileInfo["fileType"] = block.file.ext;
            fileInfo["uploadFileMD5"] = block.file["fileInfo"]["uploadFileMD5"];
            fileInfo["uploadFileExists"] = block.file["fileInfo"]["uploadFileExists"];
            block.file.fileInfo["chunk"] = block["chunk"];
            block.file.fileInfo["chunks"] = block["chunks"];
            fileInfo["chunk"] = block["chunk"];
            fileInfo["chunks"] = block["chunks"];

            var md5Promise = calMD5(uploadFile) // MD5计算，返回Promise容器

            md5Promise.then(function (md5){
                fileInfo["uploadFileSliceMD5"] = md5;
                // MD5服务器校验
                $.ajax({
                    url:"DataCloud/checkUploadFileSlice.ajax",
                    type:"POST",
                    sync:false,
                    data:fileInfo,
                    success:function (result){
                        fileInfo["uploadFileSliceExists"] = result;
                        block["fileInfo"] = fileInfo;
                        switch (result){
                            case "permit":{
                                deferred.resolve();// 调用使得webuploader可以继续往下走
                                break;
                            }
                            case "exists":{
                                deferred.reject();
                                break;
                            }
                        }
                        // options["formData"]["uploadFileSliceMD5"] = md5;
                        // options["formData"]["uploadFileSliceExists"] = result;

                    },
                    error:function (error){
                        console.log(error)
                    }
                })

            })
            return deferred.promise(); //最后需要返回Promise
        }
    })
    // 初始化容器
    uploader = WebUploader.create({

        // swf文件路径
        swf: '/js/datacloud/Uploader.swf',

        // 文件接收服务端。
        server: '/DataCloud/doUploadFile.ajax',

        formData:{
            // 上传文件所属文件夹
            "folderId":current_location
        },

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',

        // 不压缩image
        compress: false,

        //分片上传设置
        chunked:true,
        chunkSize:5242880 // 分片大小为5M
    });


    // 当有文件被添加进队列时执行
    uploader.on( 'fileQueued', function( file ) {
        // 文件信息展示
        let uploadFileList = document.getElementById("uploadFileList");
        let newTr = document.createElement('tr');
        newTr.setAttribute('id',file.id);
        let fileName = document.createElement('td');
        fileName.innerHTML = file.name;
        let fileSize = document.createElement('td');
        fileSize.innerHTML = file.size
        let fileStatus = document.createElement('td');
        fileStatus.setAttribute('id',file.id + "Status");
        fileStatus.innerHTML = "等待上传";
        let uploadBar = document.createElement('td');
        uploadBar.setAttribute('id',file.id + "uploadBar");
        uploadBar.innerHTML = "0%";
        newTr.appendChild(fileName)
        newTr.appendChild(fileSize)
        newTr.appendChild(fileStatus)
        newTr.appendChild(uploadBar)
        uploadFileList.appendChild(newTr)
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on( 'uploadProgress', function( file, percentage ) {
        $("#"+file.id+"Status").html("上传中...")
        $("#"+file.id+"uploadBar").html(percentage*100 + "%")
    });

    // 上传前执行
    uploader.on('uploadStart',function (file){
        // getCurrentLocation();
    })

    // 大文件分片上传前设置,在uploadStart之后
    uploader.on('uploadBeforeSend',function (object, data, header){
        console.log("分片上传")
        // 设置校验
        header['lg_token'] = uploaderToken
        // 打包分片信息
        var fileInfo = object["fileInfo"];
        data["uploadFileMD5"] = fileInfo["uploadFileMD5"];
        data["uploadFileExists"] = fileInfo["uploadFileExists"]
        data["uploadFileSliceMD5"] = fileInfo["uploadFileSliceMD5"];
        data["uploadFileSliceExists"] = fileInfo["uploadFileSliceExists"]
        data["fileType"] = fileInfo["fileType"]
    })

    // 上传成功时执行
    uploader.on('uploadSuccess', function( file ) {
        console.log("success")
    });

    // 上传失败时执行
    uploader.on('uploadError', function( file ) {
        console.log("文件/文件分片上传出错。")
        let fileInfo = file["fileInfo"]
        if(fileInfo["uploadFileExists"] === "exists" || fileInfo["uploadFileSliceExists"] === "exists")
            return true;
    });

    // 上传结束（不论成功失败）时执行
    uploader.on('uploadComplete', function( file ) {
        console.log("complete")
        let fileInfo = file["fileInfo"]
        if(fileInfo["uploadFileExists"] === "exists") { // 秒传服务
            $("#"+file.id+"Status").html("上传完成")
            $("#"+file.id+"uploadBar").html("100%")
            return true;
        }
        if(fileInfo["uploadFileExists"] === "duplicateFileName") { // 秒传服务
            $("#"+file.id+"Status").html("文件名重复！取消上传")
            $("#"+file.id+"uploadBar").html("0%")
            return true;
        }
        $.ajax({
            url:"DataCloud/completeUploadFile.ajax",
            type:"POST",
            data:{
                "folderId":current_location,
                "uploadFileMD5":fileInfo["uploadFileMD5"]
            },
            headers:{
                "lg_token":uploaderToken
            },
            success:function (result){
                switch (result){
                    case "success":{
                        $("#"+file.id+"Status").html("上传完成")
                        $("#"+file.id+"uploadBar").html("100%")
                        break;
                    }
                    case "error":{
                        $("#"+file.id+"Status").html("上传失败，请重新上传")
                        break;
                    }
                }
            },
            error:function (result){
                console.log(result)
            }
        })
    });

    $("#startUpload").on('click', function () {
        uploader.upload();
        // if (state === 'uploading') {
        //     uploader.stop();
        // } else {
        //     uploader.upload();
        //     timer = setInterval(function () {
        //         var useTime = parseInt($("#useTime").html());
        //         useTime = useTime + 1;
        //         $("#useTime").html(useTime);
        //     }, 1000);
        // }
    });
}

function calMD5(file){ // 返回Promise对象
    return new Promise((resolve, reject) => {
        let chunkSize = 5242880,                             // Read in chunks of 5MB
            chunks = Math.ceil(file.size / chunkSize),
            currentChunk = 0,
            spark = new SparkMD5.ArrayBuffer(),
            fileReader = new FileReader();
        fileReader.onload = function (e) {
            console.log('read chunk nr', currentChunk + 1, 'of', chunks);
            spark.append(e.target.result);                   // Append array buffer
            currentChunk++;
            if (currentChunk < chunks) {
                $("#"+file.id+"uploadBar").html(currentChunk/chunks*100 + "%")
                loadNext();
            } else {
                console.log('finished loading');
                $("#"+file.id+"uploadBar").html("100%")
                let md5 = spark.end();
                console.info('computed hash', md5);  // Compute hash
                resolve(md5);
            }
        };

        fileReader.onerror = function (e) {
            console.warn('oops, something went wrong.');
            reject(e); // 可能的错误地点
        };

        function loadNext() {
            let start = currentChunk * chunkSize,
                end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
            fileReader.readAsArrayBuffer(blobSlice(file, start, end));
        }

        loadNext();
    });
}

function blobSlice(blob,startByte,endByte){
    // 使用这个函数，根据传入的blob，可以兼容传入的blob是File类型的情况

    if(blob.slice){
        return  blob.slice(startByte,endByte);
    }
    // 兼容firefox
    if(blob.mozSlice){
        return  blob.mozSlice(startByte,endByte);
    }
    // 兼容webkit
    if(blob.webkitSlice){
        return  blob.webkitSlice(startByte,endByte);
    }
    return null;
}


