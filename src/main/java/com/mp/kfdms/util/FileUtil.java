package com.mp.kfdms.util;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.pojo.FileInfo;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 14:40
 */
public class FileUtil {
    // 使用绝对路径，使用相对路径时，再multiFile.transfer时会出现文件路径找不到的错误
    // TODO: 2021/1/28 使用绝对路径提供自定义文件保存路径的功能，默认初始化为APPDATA
    private static String BASE_URL = "C:\\Users\\J\\GitHub\\kfdms\\APPDATA";

    /**
     * 根据文件总MD5返回保存对应文件分片的文件夹File，不存在此文件夹则尝试创建这个文件夹。
     * @param MD5
     * @return
     */
    public static File getSliceDirByMD5(String MD5) {
        File file = new File(BASE_URL + File.separator + MD5);
        if(!file.exists() || !file.isDirectory())
            file.mkdir();
        return file;
    }

    public synchronized static boolean deleteDirect(File sliceDir) {
        if(sliceDir.exists()){
            if(sliceDir.isDirectory()){
                File[] files = sliceDir.listFiles();
                if(files != null && files.length>0){
                    for (File file : files) {
                        if(!deleteDirect(file)){ // 没删完，返回错误
                            return false;
                        }
                    }
                }
                // 空文件夹，删除
                if(!sliceDir.delete())
                    return false;
            }
            //是文件 删除
            return sliceDir.delete();
        }
        //本就不存在，也算删除
        return true;
    }

    public synchronized static boolean deleteSliceDir(FileInfo fileInfo){
        return deleteDirect(new File(BASE_URL + File.separator + fileInfo.getMD5()));
    }


    public synchronized static File mergeSlice(FileInfo fileInfo){
        File file = new File(BASE_URL + File.separator + fileInfo.getMD5() + File.separator + fileInfo.getMD5() + ".INFO");
        if(!file.exists())
            return null;
        try{
            fileInfo = getFileInfo(file);
            // 再次验证
            File savedDir = new File(BASE_URL + File.separator + fileInfo.getMD5());
            File[] files = savedDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".slice");
                }
            });
            if(files!= null && files.length == fileInfo.getChunks()){
                File mergedFile = new File(BASE_URL + File.separator + fileInfo.getMD5() + ".block");
                mergedFile.createNewFile();
                for(int i=0;i<fileInfo.getChunks();i++){
                    File originFile = new File(BASE_URL + File.separator + fileInfo.getMD5()+File.separator+i+".slice");
                    mergeFile(mergedFile, originFile);
                }

                //完整文件md5验证
                String md5 = MD5Util.calMD5(mergedFile);
                if (!fileInfo.getMD5().equals(md5)) {
                    mergedFile.delete();
                    return null;
                }
                return mergedFile;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public synchronized static File mergeFile(File destFile, File originFile) throws Exception {
        FileChannel fileOutputStream = null;
        FileChannel fileInputStream = null;
        try{
            fileOutputStream = new FileOutputStream(destFile,true).getChannel();// 追加模式
            fileInputStream = new FileInputStream(originFile).getChannel();
            fileOutputStream.transferFrom(fileInputStream, fileOutputStream.size(),fileInputStream.size());
        }finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return destFile;
    }

    public static File saveSlice(MultipartFile uploadFile, FileInfo fileInfo) {
        String md5 = fileInfo.getMD5();
        String DirPath = BASE_URL + File.separator + md5;
        if (!getSliceDirByMD5(md5).exists()) { // 创建文件夹失败，服务器错误
            return null;
        }

        // 先保存文件，再更新文件信息
        try {
            // 储存分片
            File file = new File(DirPath + File.separator + fileInfo.getCurrentChunk() + ".slice");
            if(!file.createNewFile()) // 如果文件存在则创建失败，如果有程序占用，则删除失败
                file.delete();
            uploadFile.transferTo(file);
            String savedMD5 = MD5Util.calMD5(file);
            if (savedMD5 == null || !savedMD5.equals(fileInfo.getCurrentChunkMD5())) {
                // 保存的分片有误，报错
                file.delete();
                return null;
            }
            // 保存并验证fileInfo(目前无验证逻辑)
            fileInfo = refreshFileInfo(fileInfo);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized FileInfo refreshFileInfo(FileInfo fileInfo) throws Exception{
        File file = new File(BASE_URL + File.separator + fileInfo.getMD5() + File.separator + fileInfo.getMD5() + ".INFO");
        if(!file.exists()){
            saveFileInfo(file,fileInfo);
        }
        FileInfo localFileInfo = getFileInfo(file);
        // 更新分片值
        Map<Integer, String> chunkMD5 = localFileInfo.getChunkMD5();
        chunkMD5.put(fileInfo.getCurrentChunk(), fileInfo.getCurrentChunkMD5());
        saveFileInfo(file, localFileInfo);
        return localFileInfo;
    }

    private synchronized static boolean saveFileInfo(File file, FileInfo fileInfo) throws IOException {
        if (!file.createNewFile()) { // 创建新文件失败，则删除旧文件
            file.delete();
        }
        OutputStream fileOutput = null;
        ObjectOutput objectOutput = null;
        try {
            fileOutput = new FileOutputStream(file);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(fileInfo);
        } finally {
            if (fileOutput != null) {
                fileOutput.close();
            }
            if (objectOutput != null) {
                objectOutput.close();
            }
        }

        return true;
    }

    public synchronized static FileInfo getFileInfo(File file) throws Exception {
        FileInputStream fileInput = null;
        ObjectInputStream objectInput = null;
        FileInfo fileInfo = null;
        try {
            fileInput = new FileInputStream(file);
            objectInput = new ObjectInputStream(fileInput);
            fileInfo = (FileInfo) objectInput.readObject();
        } finally {
            if (objectInput != null) {
                objectInput.close();
            }
            if (fileInput != null) {
                fileInput.close();
            }
        }
        return fileInfo;
    }

    public synchronized static FileInfo getFileInfo(FileInfo fileInfo) throws Exception{
        File file = new File(BASE_URL + File.separator + fileInfo.getMD5() + File.separator + fileInfo.getMD5() + ".INFO");
        return getFileInfo(file);
    }

    public static String getFilePath(FileNode fileNode) {
        return BASE_URL + File.separator + fileNode.getFile_encode_name();
    }

    public static File saveFile(FileNode fileNode, MultipartFile upload_file) {
        // 按照策略保存文件
        File file = new File(getFilePath(fileNode));
        try {
            file.createNewFile();
            if (file.exists()) {
                upload_file.transferTo(file);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getMD5(File file) throws IOException {
        // 计算文件MD5值
        String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
        return md5;
    }

    public static String getDataType(String fileName) {
        if (fileName != null) {
            String[] split = fileName.split("\\.");
            if (split.length > 1) {
                return split[split.length - 1];
            }
        }
        return "file";
    }

    public static String renameFileByStrategy(String renameFileStrategy, List<FileNode> files, FileInfo fileInfo) throws Exception {
        String uploadFilename = fileInfo.getOriginFileName();
        // 根据renameFileStrategy选择重命名策略
        switch (renameFileStrategy){
            default: {
                uploadFilename = renameFileByDefault(files, uploadFilename);
            }
        }
        return uploadFilename;
    }

    private static String renameFileByDefault(List<FileNode> files, String uploadFilename) throws Exception {
        int regexIndex=0;
        if(uploadFilename != null){
            for(int i = uploadFilename.length()-1; i>=0; i--){
                if(uploadFilename.charAt(i) == '.'){
                    regexIndex = i;
                    break;
                }
            }
            String preName = uploadFilename.substring(0, regexIndex);
            String postfix = uploadFilename.substring(regexIndex);
            boolean renameFlag = true;
            while (renameFlag){
                preName = preName + "(1)";
                uploadFilename = preName + '.'+postfix;
                renameFlag=false;
                for (FileNode file : files) {
                    if(file.equals(files)){
                        renameFlag=true;
                        break;
                    }
                }
                if(uploadFilename.length()>255){
                    throw new Exception("rename error");
                }
            }
        }
        return uploadFilename;
    }

    public static File getFileEntity(FileInfo localFileInfo) {
        File file = new File(BASE_URL + File.separator + localFileInfo.getMD5() + ".block");
        if(file.exists())
            return file;
        return null;
    }
}
