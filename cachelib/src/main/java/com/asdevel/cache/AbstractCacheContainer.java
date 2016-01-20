package com.asdevel.cache;

import java.lang.ref.WeakReference;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 9:19
 */
public abstract class AbstractCacheContainer<E> {
    protected E container;

    boolean recycled = false;
    int counter = 0;
    private long lastUpdateTime = 0;


    public AbstractCacheContainer(E object)
    {
        container = object;
    }

    public synchronized void setLastUpdateTime(long time)
    {
        lastUpdateTime = time;
    }

    public synchronized long getLastUpdateTime() {return lastUpdateTime;}

    public synchronized void addReference() throws IllegalAccessException {
        if(recycled)
        {
            throw new IllegalAccessException("Container already recycled");
        }
        counter++;
    }

    public synchronized void removeReference()
    {
        counter--;
        if(recycled && counter <= 0)
        {
            recycle();
        }
    }

    public E get()
    {
        return container;
    }

    public synchronized boolean isRecycled()
    {
        return recycled || (container == null);
    }

    public synchronized void recycle()
    {
        recycled = true;
        if(counter > 0)
        {
            //container used somewhere, so wait drop reference
            return;
        }
        objectSpecificRecycle();
        container = null;
    }


    protected abstract long getCost();
    protected abstract void objectSpecificRecycle();

}
