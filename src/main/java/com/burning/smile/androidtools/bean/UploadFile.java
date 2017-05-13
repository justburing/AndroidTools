package com.burning.smile.androidtools.bean;

import java.io.Serializable;

/**
 * 作者    Burning
 * 时间    2015/10/23 13:44
 * 描述
 */
public class UploadFile implements Serializable {
    public String  mType;
    public String  mName;
    public byte[]  mFile;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public byte[] getmFile() {
        return mFile;
    }

    public void setmFile(byte[] mFile) {
        this.mFile = mFile;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
