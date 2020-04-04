package com.montassarselmi.dooreye.Model;

import com.montassarselmi.dooreye.R;

public class Motion extends EventHistory {



    public Motion(int id, String time, String visitorImage)
    {
        super(id,time, R.drawable.ic_motion, "Motion", null , visitorImage);
    }
    public Motion(int id, String time)
    {
        super(id,time, R.drawable.ic_motion, "Motion", null , null);
    }


}
