package com.cloudcore.minder.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Command {

	/* JSON Fields */

    @Expose
    @SerializedName("command")
    public String command;
    @Expose
    @SerializedName("account")
    public String account;
    @Expose
    @SerializedName("passphrase")
    public String passphrase = "";
    @Expose
    @SerializedName("cloudcoin")
    public String cloudCoinAmount = "";

    public String filename;
}
