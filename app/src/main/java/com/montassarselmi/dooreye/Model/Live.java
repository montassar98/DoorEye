package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

import java.util.Date;

public class Live extends EventHistory {

    public Live(){super();}
    public Live(int id, Date time, String responder)
    {
        super(id,time, "Door Check", responder , null);
    }



}
