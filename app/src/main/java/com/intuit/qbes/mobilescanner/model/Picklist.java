package com.intuit.qbes.mobilescanner.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonToken;
import com.intuit.qbes.mobilescanner.DatabaseHandler;
import com.intuit.qbes.mobilescanner.MSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by pdixit on 10/3/16.
 */
public class Picklist implements Parcelable {

    private static final String LOG_TAG = "Picklist";
    private static final String JSON_TAG_RECNUM = "_id";
    private static final String JSON_TAG_NUMBER = "num";
    private static final String JSON_TAG_STATUS = "status";
    private static final String JSON_TAG_DATE = "date";
    private static final String JSON_TAG_SHIPDATE = "shipdate";
    private static final String JSON_TAG_CUSTJOB = "customerjob";
    private static final String JSON_TAG_ITEMARR = "items";
    private static DatabaseHandler db = null;

    //chandan
    @Expose(serialize = false)
    private long id;
    @Expose(serialize = false)
    private String companyId;
    @Expose(serialize = false)
    private long taskType;
    @Expose(serialize = false)
    private String name;
    @Expose(serialize = false)
    private long assigneeId;
    @Expose(serialize = false)
    private long createdById;
    @Expose(serialize = true)
    private Status status; //change to enum later
    @Expose(serialize = false)
    private long siteId;
    @Expose(serialize = false)
    private String notes;
    @Expose(serialize = false)
    private boolean showNotes;
    @Expose(serialize = true)
    private long syncToken;
    @Expose(serialize = false)
    private Date lastModified;
    @Expose(serialize = true)
    private List<LineItem> lineitems = null;
    @Expose(serialize = false)
    private boolean deleted;
    @Expose(serialize = false)
    private transient long totalitems;

    //This Constructor is for Creating Dummy PickList
    public Picklist() {
        id = -1;
        lineitems = new ArrayList<LineItem>();
    }

    public Picklist(long id,
                    String companyId,
                    long taskType,
                    String name,
                    long assigneeId,
                    long createdById,
                    Status status,
                    long siteId,
                    String notes,
                    String showNotes,
                    long syncToken,
                    String lastModified,
                    List<LineItem> lines,
                    String deleted)
    {
        try
        {
            this.id         = id;
            this.companyId  = companyId;
            this.taskType   = taskType;
            this.name            = name;
            this.assigneeId     = assigneeId;
            this.createdById    = createdById;
            this.status         = status; //change to enum later
            this.siteId         = siteId;
            this.notes           = notes;
            this.showNotes      = Boolean.valueOf(showNotes);
            this.syncToken           = syncToken;
            this.lastModified = MSUtils.yyyyMMddFormat.parse(lastModified);
            this.lineitems = lines;
            this.deleted = Boolean.valueOf(deleted);
        }
        catch (ParseException exp)
        {
            exp.printStackTrace();
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    public Picklist(Parcel in)
    {
        try {
            id = in.readLong();
            companyId = in.readString();
            taskType = in.readLong();
            name = in.readString();
            assigneeId = in.readLong();
            createdById = in.readLong();
            status = Status.valueOf(in.readString());//in.readInt(); //change to enum later
            siteId = in.readLong();
            notes = in.readString();
            showNotes = Boolean.valueOf(in.readString());
            syncToken = in.readLong();
            lastModified = (java.util.Date) in.readSerializable();
            lineitems = new ArrayList<LineItem>(Arrays.asList(in.createTypedArray(LineItem.CREATOR)));
            deleted   = Boolean.valueOf(in.readString());
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
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

    public Date getModifiedTimestamp() {
        return lastModified;
    }

    public void setModifiedTimestamp(Date modifiedTimestamp) {
        this.lastModified = modifiedTimestamp;
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

    public static List<Picklist> picklistsFromJSON(String plJsonStr )
    {
        try {
            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            Picklist[] objList = gson.fromJson(plJsonStr, Picklist[].class);
            List<Picklist> picklists = new ArrayList<>();
            for (int i = 0; i < objList.length; i++) {
                picklists.add(objList[i]);
            }
            gson = null;
            builder = null;
            return picklists;
        }
        catch (JsonParseException exp)
        {
            exp.printStackTrace();
        }

        return null;
    }

    public static Picklist picklistFromJSON(String plJsonStr )
    {
        Picklist objList = null;
        try {
            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            objList = gson.fromJson(plJsonStr, Picklist.class);
            gson = null;
            builder = null;
        }
        catch (JsonParseException exp)
        {
            exp.printStackTrace();
            objList = null;
        }
        return objList;
    }

    public static String JSONStringFromPicklist(Picklist picklist)
    {
        String jsonString = new String();
        try {
            GsonBuilder builder = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd");
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            jsonString = gson.toJson(picklist);
            builder = null;
            gson = null;
        }
        catch (JsonParseException exp)
        {
            exp.printStackTrace();
            jsonString = null;
        }

        return jsonString;
    }

    public static String JSONStringArrayFromPicklistArray(List<Picklist> picklists)
    {
        String jsonString = new String();
        try {
            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            jsonString = gson.toJson(picklists);
        }
        catch (JsonParseException exp)
        {
            exp.printStackTrace();
            jsonString = null;
        }

        return jsonString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeLong(getId());
            dest.writeString(getCompanyId());
            dest.writeLong(getTaskType());
            dest.writeString(getName());
            dest.writeLong(getAssigneeId());
            dest.writeLong(getCreatedById());
            //dest.writeInt(getStatus());
            dest.writeString((status == null) ? "" : status.name());
            dest.writeLong(getSiteId());
            dest.writeString(getNotes());
            dest.writeString(String.valueOf(isShowNotes()));
            dest.writeLong(getSyncToken());
            dest.writeSerializable(getModifiedTimestamp());
            dest.writeTypedArray(getLineitems().toArray(new LineItem[getLineitems().size()]), 0);
            dest.writeString(String.valueOf(isDeleted()));

        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }

    }

    public static final Parcelable.Creator<Picklist> CREATOR = new Parcelable.Creator<Picklist>() {
        @Override
        public Picklist createFromParcel(Parcel source) {
            return new Picklist(source);
        }

        @Override
        public Picklist[] newArray(int size) {
            return new Picklist[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        boolean ret = false;

        if (o instanceof Picklist)
        {
            Picklist rhs = (Picklist) o;
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
    /*public static void StorePickList(List<Picklist> picklists, Context context)
    {
        if(db == null)
        db = new DatabaseHandler(context);
        for(int i =0 ; i<picklists.size() ; i++)
        {
            if(db.PickListExists(picklists.get(i).getId())) {
                db.updatePickList(picklists.get(i), picklists.get(i).getId());
                UpdateLineItems(picklists.get(i).getLineitems(),picklists.get(i).getId(),context);
            }
            else
            {
                db.addPickList(picklists.get(i));
                StoreLineItem(picklists.get(i).getLineitems(), picklists.get(i).getId(), context);
            }
        }
    }

    public static void StoreLineItem(List<LineItem> lineitems, long id, Context context)
    {
        if(db == null)
        db = new DatabaseHandler(context);
        for(int i =0 ; i<lineitems.size() ; i++)
        {
                db.addLineItem(lineitems.get(i), id);
        }

    }

    public static void UpdateLineItems(List<LineItem> lineitems, long id,  Context context)
    {
        if(db == null)
            db = new DatabaseHandler(context);
        for(int i =0 ; i<lineitems.size() ; i++)
        {
            db.updateLineItems(lineitems.get(i), id);
        }
    }

    public void DeletePicklists(List<Picklist> picklists, Context context)
    {
        if(db == null)
            db = new DatabaseHandler(context);
        for(int i=0; i<picklists.size();i++)
        {
            DeleteLineItems(picklists.get(i).getId(),context);
            db.deleteOnePicklist(picklists.get(i).getId());
        }
    }

    public void DeleteLineItems(long recnum, Context context)
    {
        if(db == null)
            db = new DatabaseHandler(context);
        db.deleteLineItems(recnum);
    }*/

}
