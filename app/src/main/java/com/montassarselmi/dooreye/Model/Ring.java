package com.montassarselmi.dooreye.Model;

import android.widget.ImageView;

import com.montassarselmi.dooreye.R;

public class Ring extends EventHistory {

    private final int icon = R.drawable.ic_ring;
    private final String status = "Answer";
    private String visitorImage;
    private String responder;

    public Ring(int id, String time, String visitorImage, String responder)
    {
        super(id,time);
        this.visitorImage = visitorImage;
        this.responder = responder;
    }
    public Ring(int id, String time, String responder)
    {
        super(id,time);
        this.responder = responder;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
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
