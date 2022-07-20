package com.fencer.updatesdk;

import java.util.List;

public class VersionBean {

    private List<Version> list ;

    private String error ;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Version> getList() {
        return list;
    }

    public void setList(List<Version> list) {
        this.list = list;
    }

    public static class  Version{

        public Version() {
        }

        private String  createTime;
        private String fileUrl;
        private int forceUpdate;
        private int id;
        private String updateDescription;
        private int versionCode;
        private String versionName;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public int getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(int forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUpdateDescription() {
            return updateDescription;
        }

        public void setUpdateDescription(String updateDescription) {
            this.updateDescription = updateDescription;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }


}
