package com.asdevel.cache.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.SystemClock;

public class SingleShot
{
	Handler mHandler = null;//new Handler();
	long mLastFire = 0;
	long mDelay = 10;
	Runnable mSlot = null;
	boolean mProlongatedDelay = false;
	
	public SingleShot(long delay, Runnable slot, boolean prolongateDelay)
	{
		init(delay, slot, prolongateDelay);
	}
	public SingleShot(long delay, Runnable slot)
	{
		init(delay, slot, true);
	}
	public SingleShot()
	{
		init(0, null, true);
	}
	
	public SingleShot(long delay)
	{
		init(delay, null, true);
	}
	
	private void init(long delay, Runnable slot, boolean prolongateDelay)
	{
		if(delay <= 0) delay = 0;
		mDelay = delay;
		mHandler = new Handler();
		mSlot = slot;
		mProlongatedDelay =  prolongateDelay;
	}
	
	public void cancel()
	{
		if(mSlot != null)
			mHandler.removeCallbacks(mSlot);
		
	}
	
	public boolean fire(long delay, Runnable slot)
	{
		if(slot == null) return false;
		if(delay < 0) delay = 0;
		if(mSlot != null) mHandler.removeCallbacks(mSlot);
		long realDelay = delay;
		if(mProlongatedDelay == false)
		{
			long ct = SystemClock.currentThreadTimeMillis();
			long delta = ct - mLastFire;
			if(delta >= 0 && delta < delay)
			{
				realDelay = delay - delta;
			}
			mLastFire = ct;
		}
		mDelay = delay;
		mSlot = slot;
		mHandler.postDelayed(mSlot, realDelay);
		return true;
	}
	
	public boolean fire()
	{
		if(mSlot == null) return false;
		return fire(mDelay, mSlot);
	}
}
