package com.jpsite.srt;

/**
 * @author jiangpeng
 * @date 2021/3/912:32
 * https://www.iteye.com/blog/lc-wangchao-652749
 */
public abstract  class SubtitleDesigner {
    protected String src,des;
    public SubtitleDesigner(String src,String des){
        this.src = src;
        this.des = des;
    }
    // 提前
    public abstract boolean forward(long msecond);

    // 延迟字幕
    public abstract boolean delay(long msecond);
}
