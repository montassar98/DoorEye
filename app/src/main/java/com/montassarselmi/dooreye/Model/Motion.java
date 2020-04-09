package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

public class Motion extends EventHistory {

    public Motion()
    {super();}

    public Motion(int id, String time, String visitorImage)
    {
        super(id,time, "Motion", null , visitorImage);
    }
    public Motion(int id, String time)
    {
        super(id,time, "Motion", null , null);
    }


}
