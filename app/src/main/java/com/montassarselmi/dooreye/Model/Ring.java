package com.montassarselmi.dooreye.Model;

import android.media.tv.TvInputService;
import android.widget.ImageView;

import com.montassarselmi.dooreye.R;

public class Ring extends EventHistory {


    public Ring(){
        super();
    }
    public Ring(int id, String time, String responder, String visitorImage)
    {
        super(id,time, R.drawable.ic_ring, "Ring",responder, visitorImage);

    }
    public Ring(int id, String time, String responder)
    {
        super(id,time, R.drawable.ic_ring, "Ring",responder, null);
    }


}
