package com.swallow.feise.regionandgifviewpager.bean;

import java.util.ArrayList;

/**
 * Created by yahui.zeng on 2018/1/25.
 */

public class AdvertInfo {

    private String adImgPath;// 图片地址
    private String adLinkUrl;// 链接地址

    private ArrayList<PageParam> adPageList;// 动态广告跳转地址列表

    public static class PageParam {
        private String adLinkUrl;// 链接地址

        public String getAdLinkUrl() {
            return adLinkUrl;
        }

        public void setAdLinkUrl(String adLinkUrl) {
            this.adLinkUrl = adLinkUrl;
        }
    }

    public String getAdImgPath() {
        return adImgPath;
    }

    public void setAdImgPath(String adImgPath) {
        this.adImgPath = adImgPath;
    }

    public String getAdLinkUrl() {
        return adLinkUrl;
    }

    public void setAdLinkUrl(String adLinkUrl) {
        this.adLinkUrl = adLinkUrl;
    }

    public ArrayList<PageParam> getAdPageList() {
        return adPageList;
    }

    public void setAdPageList(ArrayList<PageParam> adPageList) {
        this.adPageList = adPageList;
    }
}
