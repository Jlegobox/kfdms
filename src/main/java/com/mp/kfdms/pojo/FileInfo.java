package com.mp.kfdms.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传文件信息的保存
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/18
 * @Time 11:00
 */
public class FileInfo implements Serializable {
    private String id;
    private String encodedName;
    private String originFileName;
    private long fileSize;
    private String fileType;
    private String lastModifiedDate;
    private int chunks;
    private int currentChunk;
    private String MD5;
    private String currentChunkMD5;
    private Map<Integer,String> ChunkMD5 = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEncodedName() {
        return encodedName;
    }

    public void setEncodedName(String encodedName) {
        this.encodedName = encodedName;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(int currentChunk) {
        this.currentChunk = currentChunk;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getCurrentChunkMD5() {
        return currentChunkMD5;
    }

    public void setCurrentChunkMD5(String currentChunkMD5) {
        this.currentChunkMD5 = currentChunkMD5;
    }

    public Map<Integer, String> getChunkMD5() {
        return ChunkMD5;
    }

    public void setChunkMD5(Map<Integer, String> chunkMD5) {
        ChunkMD5 = chunkMD5;
    }
}
