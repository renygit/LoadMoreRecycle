package com.renygit.recycleview;

/**
 * Created by admin on 2017/6/29.
 */

public class RStyleConfig {

    private String indicatorName;
    private int indicatorColor;
    private int bgColor;
    private int textColor;
    private String tipError;
    private String tipLoading;
    private String tipEnd;

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getTipError() {
        return tipError;
    }

    public void setTipError(String tipError) {
        this.tipError = tipError;
    }

    public String getTipLoading() {
        return tipLoading;
    }

    public void setTipLoading(String tipLoading) {
        this.tipLoading = tipLoading;
    }

    public String getTipEnd() {
        return tipEnd;
    }

    public void setTipEnd(String tipEnd) {
        this.tipEnd = tipEnd;
    }

    public RStyleConfig(Build build) {
        this.indicatorName = build.indicatorName;
        this.indicatorColor = build.indicatorColor;
        this.bgColor = build.bgColor;
        this.textColor = build.textColor;
        this.tipError = build.tipError;
        this.tipLoading = build.tipLoading;
        this.tipEnd = build.tipEnd;
    }

    public static class Build{
        //赋上默认值
        private String indicatorName = "BallSpinFadeLoaderIndicator";
        private int indicatorColor = R.color.load_more;
        private int bgColor = android.R.color.transparent;
        private int textColor = R.color.load_more;
        private String tipError = "加载失败，点击重试";
        private String tipLoading = "加载中";
        private String tipEnd = "没有更多数据";

        public Build setIndicatorName(String indicatorName) {
            this.indicatorName = indicatorName;
            return this;
        }

        public Build setIndicatorColor(int indicatorColor) {
            this.indicatorColor = indicatorColor;
            return this;
        }

        public Build setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Build setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Build setTipError(String tipError) {
            this.tipError = tipError;
            return this;
        }

        public Build setTipLoading(String tipLoading) {
            this.tipLoading = tipLoading;
            return this;
        }

        public Build setTipEnd(String tipEnd) {
            this.tipEnd = tipEnd;
            return this;
        }

        public RStyleConfig build(){
            return new RStyleConfig(this);
        }
    }

}
