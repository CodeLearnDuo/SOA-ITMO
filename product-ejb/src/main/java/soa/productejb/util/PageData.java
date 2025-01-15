package soa.productejb.util;

import java.util.List;

public class PageData<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    public PageData() {
    }

    public PageData(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
