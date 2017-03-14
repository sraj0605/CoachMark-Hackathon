package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 11/03/17.
 */

public final class TableDetails {

    public static String KEY_ID = "id";

    interface Tables{
        String PICKLIST = "PickListInfo";
        String LINEITEM = "LineItemInfo";
        String SERIALLOTNUMBER = "SerialNumberInfo";
        String SYNCINFO = "SyncInfo";
    }
    interface PickListInfo{

        String KEY_ID = "id";
        String KEY_COMPANYID = "companyId";
        String KEY_TASKTYPE = "taskType";
        String KEY_NAME = "name";
        String KEY_ASSIGNEDID = "assigneeId";
        String KEY_CREATEDBYID = "createdById";
        String KEY_STATUS = "Status";
        String KEY_SITEID = "siteId";
        String KEY_NOTES = "notes";
        String KEY_SHOWNOTES = "showNotes";
        String KEY_SYNCTOKEN = "syncToken";
        String KEY_CREATEDTIMESTAMP = "createdTimestamp";
        String KEY_MODIFIEDTIMESTAMP = "modifiedTimestamp";
    }

    interface LineItemInfo {
        String KEY_TASKID = "taskId";
        String KEY_EXTID = "extId";
        String KEY_NAME_LINEITEM = "itemName";
        String KEY_DESC = "itemDesc";
        String KEY_LINEITEMPOS = "lineitemPos";
        String KEY_DOCNUM = "docNum";
        String KEY_TXNID = "txnId";
        String KEY_TXNDATE = "txnDate";
        String KEY_SHIPDATE = "shipDate";
        String KEY_UOM = "uom";
        String KEY_PICKED = "qtyPicked";
        String KEY_TOPICK = "qtyToPick";
        String KEY_BARCODE = "barcode";
        String KEY_BINLOCATION = "binLocation";
        String KEY_BINEXTID = "binExtId";
        String KEY_CUSTOMFIELD = "customFields";
        String KEY_SERIANUMBER = "showSerialNo";
        String KEY_LOTNUMBER = "showLotNo";
    }

    interface SerialNumberInfo {
        String KEY_LINEITEMID = "lineitemId";
        String KEY_TYPE = "type";
        String KEY_VALUE = "value";
    }

    interface SyncInfo {
        String KEY_GUID = "guid";
        String KEY_LASTSYNCTIME = "lastSyncTime";
    }
}
