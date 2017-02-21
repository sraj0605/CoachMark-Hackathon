package com.intuit.qbes.mobilescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.intuit.qbes.mobilescanner.DatabaseHandler;
import com.intuit.qbes.mobilescanner.MSUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by pdixit on 10/3/16.
 */
public class Task implements Parcelable{

    private static DatabaseHandler db = null;

    //chandan
    private long id;
    private long companyId;
    private long taskType;
    private String name;
    private long assigneeId;
    private long createdById;
    private Status status; //change to enum later
    private long siteId;
    private String notes;
    private boolean showNotes;
    private long syncToken;
    private Date createdTimestamp;
    private Date modifiedTimestamp;
    private List<LineItem> lineitems = null;
    private boolean deleted;
    private transient long totalitems;

    //This Constructor is for Creating Dummy PickList
    public Task() {
        id = -1;
        lineitems = new ArrayList<LineItem>();
    }

    public Task(long id,
                long companyId,
                long taskType,
                String name,
                long assigneeId,
                long createdById,
                Status status,
                long siteId,
                String notes,
                String showNotes,
                long syncToken,
                String createdTimeStamp,
                String modifiedTimeStamp,
                List<LineItem> lines,
                String deleted) {
        try {
            this.id = id;
            this.companyId = companyId;
            this.taskType = taskType;
            this.name = name;
            this.assigneeId = assigneeId;
            this.createdById = createdById;
            this.status = status; //change to enum later
            this.siteId = siteId;
            this.notes = notes;
            this.showNotes = Boolean.valueOf(showNotes);
            this.syncToken = syncToken;
            this.createdTimestamp = MSUtils.yyyyMMddFormat.parse(createdTimeStamp);
            ;
            this.modifiedTimestamp = MSUtils.yyyyMMddFormat.parse(modifiedTimeStamp);
            ;
            this.lineitems = lines;
            this.deleted = Boolean.valueOf(deleted);
        } catch (ParseException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public Task(Parcel in) {
        try {
            id = in.readLong();
            companyId = in.readLong();
            taskType = in.readLong();
            name = in.readString();
            assigneeId = in.readLong();
            createdById = in.readLong();
            status = Status.valueOf(in.readString());//in.readInt(); //change to enum later
            siteId = in.readLong();
            notes = in.readString();
            showNotes = Boolean.valueOf(in.readString());
            syncToken = in.readLong();
            createdTimestamp = (java.util.Date) in.readSerializable();
            modifiedTimestamp = (java.util.Date) in.readSerializable();
            lineitems = new ArrayList<LineItem>(Arrays.asList(in.createTypedArray(LineItem.CREATOR)));
            deleted = Boolean.valueOf(in.readString());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getTaskType() {
        return taskType;
    }

    public void setTaskType(long taskType) {
        this.taskType = taskType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(long createdById) {
        this.createdById = createdById;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isShowNotes() {
        return showNotes;
    }

    public void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public long getSyncToken() {
        return syncToken;
    }

    public void setSyncToken(long syncToken) {
        this.syncToken = syncToken;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(Date modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public List<LineItem> getLineitems() {
        return lineitems;
    }

    public void setLineitems(List<LineItem> lineitems) {
        this.lineitems = lineitems;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(long totalitems) {
        this.totalitems = totalitems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeLong(getId());
            dest.writeLong(getCompanyId());
            dest.writeLong(getTaskType());
            dest.writeString(getName());
            dest.writeLong(getAssigneeId());
            dest.writeLong(getCreatedById());
            dest.writeString((status == null) ? "" : status.name());
            dest.writeLong(getSiteId());
            dest.writeString(getNotes());
            dest.writeString(String.valueOf(isShowNotes()));
            dest.writeLong(getSyncToken());
            dest.writeSerializable(getCreatedTimestamp());
            dest.writeSerializable(getModifiedTimestamp());
            dest.writeTypedArray(getLineitems().toArray(new LineItem[getLineitems().size()]), 0);
            dest.writeString(String.valueOf(isDeleted()));

        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }

    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        boolean ret = false;

        if (o instanceof Task)
        {
            Task rhs = (Task) o;
            ret = rhs.id == this.id;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int)this.id;
        return hash;
    }


}
