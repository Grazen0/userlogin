package com.elchologamer.userlogin.util

class QuickMap<K, V>(firstKey: K, firstValue: V) : HashMap<K, V>() {
    init {
        put(firstKey, firstValue)
    }

     operator fun set(key: K, value: V): QuickMap<K, V> {
        super.put(key, value)
        return this
    }
}