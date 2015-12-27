package com.buthmathearo.articlemanagement.util;

/**
 * Created by buthmathearo on 12/23/15.
 */
public class Pagination {
    private int rowCount = 6;  // Number of Records per Page
    private int pageCount = 1; // Current Page

    private int totalRecords;
    private int totalPages;
    private int remainOfRecords;

    /* Default Constructor */
    public Pagination() {

    }

    public Pagination(int rowCount) {
        this.rowCount = rowCount;
    }

    /* Getter & Setter Methods */
    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /* Calculate total pages for pagination */
    public int getTotalPages() {
        if (totalRecords >= rowCount ) {
            totalPages = totalRecords / rowCount;
            remainOfRecords = totalRecords % rowCount;
            if (remainOfRecords > 0) totalPages++;
        } else {
            totalPages = 1;
        }
        return totalPages;
    }

}
