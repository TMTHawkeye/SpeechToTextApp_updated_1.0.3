package org.smrtobjads.ads.billings;

public class PurchaseItem {
    private String itemID;
    private String itemTrailID;
    private int itemType;

    public PurchaseItem(String itemId, int type) {
        this.itemID = itemId;
        this.itemType = type;
    }

    public PurchaseItem(String itemId, String trialId, int type) {
        this.itemID = itemId;
        this.itemTrailID = trialId;
        this.itemType = type;
    }

    public String getItemId() {
        return this.itemID;
    }

    public void setItemId(String itemId) {
        this.itemID = itemId;
    }

    public String getTrialId() {
        return this.itemTrailID;
    }

    public void setTrialId(String trialId) {
        this.itemTrailID = trialId;
    }

    public int getType() {
        return this.itemType;
    }

    public void setType(int type) {
        this.itemType = type;
    }
}
