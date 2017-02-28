package com.intuit.qbes.mobilescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by ckumar5 on 14/02/17.
 */

public class SerialLotNumber implements Parcelable {

    @Expose(serialize = false)
    private long id;
    @Expose(serialize = true)
    private long lineitemId;
    @Expose(serialize = true)
    private int type;
    @Expose(serialize = true)
    private String value;

    public long getLineitemId() {
        return lineitemId;
    }

    public void setLineitemId(long lineitemId) {
        this.lineitemId = lineitemId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public SerialLotNumber(long id,long lineitemId,int type,String value)
    {
        this.id = id;
        this.lineitemId = lineitemId;
        this.type = type;
        this.value= value;
    }

    public SerialLotNumber()
    {

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(getId());
        dest.writeLong(getLineitemId());
        dest.writeInt(getType());
        dest.writeString(getValue());

    }

    public static final Parcelable.Creator<SerialLotNumber> CREATOR = new Parcelable.Creator<SerialLotNumber>() {
        @Override
        public SerialLotNumber createFromParcel(Parcel source) {
            return new SerialLotNumber(source);
        }

        @Override
        public SerialLotNumber[] newArray(int size) {
            return new SerialLotNumber[size];
        }
    };

    public SerialLotNumber(Parcel in)
    {
        id = in.readLong();
        lineitemId = in.readLong();
        type = in.readInt();
        value = in.readString();
    }
}