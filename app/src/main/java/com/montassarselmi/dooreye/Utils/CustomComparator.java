package com.montassarselmi.dooreye.Utils;

import com.montassarselmi.dooreye.Model.EventHistory;

import java.util.Comparator;

public class CustomComparator implements Comparator<EventHistory> {
    @Override
    public int compare(EventHistory o1, EventHistory o2) {
        return o1.getEventTime().compareTo(o2.getEventTime());
    }
}
