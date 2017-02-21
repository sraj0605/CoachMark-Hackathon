package com.intuit.qbes.mobilescanner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ckumar5 on 19/02/17.
 */

public enum Status {
    @SerializedName("0")
    Open,
    @SerializedName("1")
    SentforPick,
    @SerializedName("2")
    PartiallySentforPick,
    @SerializedName("3")
    PickinProgress,
    @SerializedName("4")
    PartialPickInProgress,
    @SerializedName("5")
    PartiallyPicked,
    @SerializedName("6")
    Picked,
    @SerializedName("7")
    PartiallyInvoiced,
    @SerializedName("8")
    Invoiced,
    @SerializedName("9")
    NotPicked,
    @SerializedName("20")
    SentforReceive,
    @SerializedName("21")
    PartiallysentforReceive,
    @SerializedName("22")
    ReceiveinProgress,
    @SerializedName("23")
    PartialReceiveinProgress,
    @SerializedName("24")
    PartiallyReceived,
    @SerializedName("25")
    Received,
    @SerializedName("26")
    ReceiptCreated,
    @SerializedName("27")
    PartialReceiptCreated,
    @SerializedName("28")
    NotReceived,
    @SerializedName("100")
    Complete

}
