package com.m87.xchange.xchange;

import com.m87.sdk.M87NearEntry;

/**
 * Created by kenlee on 12/4/15.
 */
public class M87NameEntry extends M87NearEntry{
    public String name;
    public M87NameEntry(String name, M87NearEntry nearEntry){
        super(nearEntry.id(),nearEntry.hopCount(),nearEntry.ttl(),nearEntry.metrics(),nearEntry.ssType());
        this.name = name;
    }
}
