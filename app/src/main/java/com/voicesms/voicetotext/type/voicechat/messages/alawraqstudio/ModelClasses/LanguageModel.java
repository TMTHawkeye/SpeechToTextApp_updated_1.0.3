package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses;


import com.google.errorprone.annotations.Keep;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class LanguageModel {
    public LanguageModel(String languageCode, String languageName, String countryCode) {
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.countryCode = countryCode;
    }

    public LanguageModel() {
    }

    @SerializedName("languageCode")
    @Expose
    private String languageCode;
    @SerializedName("languageName")
    @Expose
    private String languageName;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}