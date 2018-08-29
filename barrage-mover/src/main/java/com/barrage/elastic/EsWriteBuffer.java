package com.barrage.elastic;

import java.util.ArrayList;
import java.util.List;

public class EsWriteBuffer<T> {

    public static final int DEFAULT_BUFFER_LENGTH = 1000;
    private List<EsWriteObject> buffered  = null;


    private int bufferLen = DEFAULT_BUFFER_LENGTH;

    public EsWriteBuffer(int bufferLen) {
        this.bufferLen = bufferLen;
        buffered = new ArrayList<>(bufferLen);
    }

    /**
     * cache if buffer is full
     * @param t
     * @return true if cached
     */
    public synchronized boolean cache(EsWriteObject t) {
        if(buffered.size() < DEFAULT_BUFFER_LENGTH) {
            return buffered.add(t);
        } else {
            return false;
        }
    }

    public List<EsWriteObject> get() {
        return buffered;
    }

    public void reset() {
        buffered.clear();
    }
}
