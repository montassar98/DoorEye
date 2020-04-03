package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

public class Live extends EventHistory {

    private final int icon = R.drawable.ic_live;
    private final String status = "Live";
    private String responder;

    public Live(int id, String time, String responder)
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
}
