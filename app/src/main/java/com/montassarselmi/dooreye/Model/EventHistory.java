package com.montassarselmi.dooreye.Model;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.montassarselmi.dooreye.R;

import java.util.Date;

public class EventHistory {

    private int id;
    private Date eventTime;
    private  int icon;
    private String status;
    private String responder;
    private String visitorImage;

    public EventHistory( int id, Date eventTime, String status, @Nullable String responder, @Nullable String visitorImage) {
        this.id = id;
        this.eventTime = eventTime;
        this.status = status;
        if (responder != null)
            this.responder = responder;
        if (visitorImage != null)
            this.visitorImage = visitorImage;
        switch (status)
        {
            case "Ring":
                icon = R.drawable.ic_ring;
                break;
            case "Motion":
                icon = R.drawable.ic_motion;
                break;
            case "Door Check":
                icon = R.drawable.ic_live;
                break;
        }
    }

    public EventHistory() {

    }

    public void setupIcon(String status)
    {
        switch (status)
        {
            case "Ring":
                icon = R.drawable.ic_ring;
                break;
            case "Motion":
                icon = R.drawable.ic_motion;
                break;
            case "Door Check":
                icon = R.drawable.ic_live;
                break;
        }
    }

    public int getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(@Nullable String responder) {
        if (responder != null)
        this.responder = responder;
    }

    public String getVisitorImage() {
        return visitorImage;
    }

    public void setVisitorImage(String visitorImage) {
        if (visitorImage != null)
            this.visitorImage = visitorImage;
    }

    @Override
    public String toString() {
        return "EventHistory{" +
                "id=" + id +
                ", eventTime='" + eventTime + '\'' +
                ", icon=" + icon +
                ", status='" + status + '\'' +
                ", responder='" + responder + '\'' +
                ", visitorImage='" + visitorImage + '\'' +
                '}';
    }
}
