package org.smrp.models;

public class Pager {
    
    private long total;
    private int pageNum;
    private int pageSize;
    
    public Pager(long total, int pageNum, int pageSize) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    public long getTotal() {
        return total;
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public int getLowerBound() {
        return (this.getPageNum() - 1) * this.getPageSize();
    }

    public long getUpperBound() {
        long x = (long) this.getPageNum() * (long) this.getPageSize();
        if (this.getTotal() < x) {
            x = this.total;
        }
        return x;
    }

    public int getTotalPages() {
        double v = (double) this.getTotal() / (double) this.getPageSize();
        double x = Math.ceil(v);
        return (int) x;
    }

    public void setPageSize(int pageSize) {
        if ((this.getTotal() < pageSize || pageSize < 1) && this.getTotal() > 0) {
            this.pageSize = (int) this.getTotal();
        }
        
        else {
            this.pageSize = pageSize;
        }
        
        if (this.getTotalPages() < this.getPageNum()) {
            this.pageNum = this.getTotalPages();
        }
        
        if (this.getPageNum() < 1) {
            this.pageNum = 1;
        }
    }

    public static Pager from(long total, Integer page, Integer limit) {
        Pager pg = new Pager(total, page, limit);
        pg.setPageSize(limit);
        return pg;
    }
}
