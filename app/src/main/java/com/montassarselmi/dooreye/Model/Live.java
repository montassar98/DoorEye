package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

public class Live extends EventHistory {

    public Live(){super();}
    public Live(int id, String time, String responder)
    {
        super(id,time, "Door Check", responder , null);
    }



}
