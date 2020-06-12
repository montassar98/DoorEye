package com.montassarselmi.dooreye.Model;

import android.media.tv.TvInputService;
import android.widget.ImageView;

import com.montassarselmi.dooreye.R;

import java.util.Date;

public class Ring extends EventHistory {


    public Ring(){
        super();
    }
    public Ring(int id, Date time, String responder, String visitorImage)
    {
        super(id,time,"Ring",responder, visitorImage);

    }
    public Ring(int id, Date time, String responder)
    {
        super(id,time,"Ring",responder, null);
    }



}
