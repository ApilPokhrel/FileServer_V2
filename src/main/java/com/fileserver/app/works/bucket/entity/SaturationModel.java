package com.fileserver.app.works.bucket.entity;

public class SaturationModel {
    private Integer gap;
    private Integer stat = 1;
    private Integer file_count_stat = 0;


    public SaturationModel(Integer gap, Integer stat, Integer file_count_stat) {
        this.gap = gap;
        this.stat = stat;
        this.file_count_stat = file_count_stat;
    }

    public SaturationModel() {
    }

    public Integer getGap() {
        return gap;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public Integer getFile_count_stat() {
        return file_count_stat;
    }

    public void setFile_count_stat(Integer file_count_stat) {
        this.file_count_stat = file_count_stat;
    }
}
