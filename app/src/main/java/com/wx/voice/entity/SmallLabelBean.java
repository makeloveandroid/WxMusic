package com.wx.voice.entity;



public  class SmallLabelBean {
    /**
     * small_label_id : 5
     * labelid : 43
     * biglabel_id : 1
     * label : 全部语种
     */

    private int smallLabelId;

    private int labelid;

    private int biglabelId;
    private String label;

    public int getSmallLabelId() {
        return smallLabelId;
    }

    public void setSmallLabelId(int smallLabelId) {
        this.smallLabelId = smallLabelId;
    }

    public int getLabelid() {
        return labelid;
    }

    public void setLabelid(int labelid) {
        this.labelid = labelid;
    }

    public int getBiglabelId() {
        return biglabelId;
    }

    public void setBiglabelId(int biglabelId) {
        this.biglabelId = biglabelId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}