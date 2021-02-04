package com.elchologamer.userlogin.api;

import java.util.HashMap;

public class QuickMap<K, V> extends HashMap<K, V> {

    public QuickMap(K firstKey, V firstValue) {
        this.put(firstKey, firstValue);
    }

    public QuickMap<K, V> set(K key, V value) {
        super.put(key, value);
        return this;
    }
}
