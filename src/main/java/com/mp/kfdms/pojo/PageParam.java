package com.mp.kfdms.pojo;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/29
 * @Time 22:53
 */
public class PageParam {

    private int currentPage=1; //当前页数
    private int PageSize=10; // 每页记录大小
    private int totalCount; // 总记录条数
    private int totalPage; // 总页数

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
