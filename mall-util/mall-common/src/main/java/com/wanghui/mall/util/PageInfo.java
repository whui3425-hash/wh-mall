package com.wanghui.mall.util;
import java.io.Serializable;

public class PageInfo implements Serializable {

    // Page number (which page)
    private long currentpage;

    // Total records queried from database
    private long total;

    // Query 5 items per page
    private int size;

    // Next page
    private int next;

    // Last page
    private int last;

    private int lpage;

    private int rpage;

    // Start from which record
    private long start;

    // Global offset
    public int offsize = 2;

    public PageInfo() {
        super();
    }

    /****
     *
     * @param currentpage
     * @param total
     * @param pagesize
     */
    public void setCurrentpage(long currentpage, long total, long pagesize) {
        // Divisible case
        long pagecount = total / pagesize;

        // If divisible, exactly N pages; if not, N+1 pages
        int totalPages = (int) (total % pagesize == 0 ? total / pagesize : (total / pagesize) + 1);

        // Total pages
        this.last = totalPages;

        // Check if current page exceeds bounds; if so, query last page
        if (currentpage > totalPages) {
            this.currentpage = totalPages;
        } else {
            this.currentpage = currentpage;
        }

        // Calculate start
        this.start = (this.currentpage - 1) * pagesize;
    }

    // Previous page
    public long getUpper() {
        return currentpage > 1 ? currentpage - 1 : currentpage;
    }

    // Total pages, i.e., last page
    public void setLast(int last) {
        this.last = (int) (total % size == 0 ? total / size : (total / size) + 1);
    }

    /****
     * Pagination with offset setting
     * @param total
     * @param currentpage
     * @param pagesize
     * @param offsize
     */
    public PageInfo(long total, int currentpage, int pagesize, int offsize) {
        this.offsize = offsize;
        initPage(total, currentpage, pagesize);
    }

    /****
     *
     * @param total   Total records
     * @param currentpage    Current page
     * @param pagesize    Items per page
     */
    public PageInfo(long total, int currentpage, int pagesize) {
        initPage(total, currentpage, pagesize);
    }

    /****
     * Initialize pagination
     * @param total
     * @param currentpage
     * @param pagesize
     */
    public void initPage(long total, int currentpage, int pagesize) {
        // Total records
        this.total = total;
        // Items per page
        this.size = pagesize;

        // Calculate current page, DB query start value, and total pages
        setCurrentpage(currentpage, total, pagesize);

        // Pagination calculation
        int leftcount = this.offsize,    // Times to go to previous page
                rightcount = this.offsize;

        // Start page
        this.lpage = currentpage;
        // End page
        this.rpage = currentpage;

        // 2-point judgment
        this.lpage = currentpage - leftcount;            // Normal start
        this.rpage = currentpage + rightcount;        // Normal end

        // Page difference = difference between total pages and end page
        int topdiv = this.last - rpage;                // Check if exceeds max page

        /***
         * Start page
         * 1. If page difference < 0, start page = start page + page difference
         * 2. If page difference >= 0, judge start and end pages
         */
        this.lpage = topdiv < 0 ? this.lpage + topdiv : this.lpage;

        /***
         * End page
         * 1. If start page <= 0, end page = |start page| + 1
         * 2. If start page > 0, end page
         */
        this.rpage = this.lpage <= 0 ? this.rpage + (this.lpage * -1) + 1 : this.rpage;

        /***
         * If start page <= 0, set start page to first page
         * Otherwise unchanged
         */
        this.lpage = this.lpage <= 0 ? 1 : this.lpage;

        /***
         * If end page > total pages, end page = total pages
         * Otherwise unchanged
         */
        this.rpage = this.rpage > last ? this.last : this.rpage;
    }

    public long getNext() {
        return currentpage < last ? currentpage + 1 : last;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public long getCurrentpage() {
        return currentpage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getLast() {
        return last;
    }

    public long getLpage() {
        return lpage;
    }

    public void setLpage(int lpage) {
        this.lpage = lpage;
    }

    public long getRpage() {
        return rpage;
    }

    public void setRpage(int rpage) {
        this.rpage = rpage;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setCurrentpage(long currentpage) {
        this.currentpage = currentpage;
    }
}
