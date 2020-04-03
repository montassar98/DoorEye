package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

public class Motion extends EventHistory {

    private final int icon = R.drawable.ic_motion;
    private final String status = "Motion";
    private String visitorImage;


    public Motion(int id, String time, String visitorImage)
    {
        super(id,time);
        this.visitorImage = visitorImage;
    }
    public Motion(int id, String time)
    {
        super(id,time);
    }

    public int getIcon() {
        return icon;
    }

    public String getStatus() {
        return status;
    }

    public String getVisitorImage() {
        return visitorImage;
    }

    public void setVisitorImage(String visitorImage) {
        this.visitorImage = visitorImage;
    }
}
