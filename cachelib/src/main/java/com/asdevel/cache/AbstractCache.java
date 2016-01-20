package com.asdevel.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractCache <Key, T>
{
	private class Node 
	{
		Node(T data, long cost)
		{ keyPtr = null; t = data; c = cost; p = null; n = null;}
		Key keyPtr = null;
		T t; 
		long c; 
		Node p,n;
	};
	Node f = null;
	Node l = null;
	
	HashMap<Key, Node> hash = new HashMap<Key, Node>();
	long mx = 0;
	long total = 0;

	private synchronized void unlink(Node n) 
	{
		if (n.p != null) n.p.n = n.n;
		if (n.n != null) n.n.p = n.p;
		if (l == n) l = n.p;
		if (f == n) f = n.n;
		total -= n.c;
		releaseObject(n.t);
		n.t = null;
		hash.remove(n.keyPtr);
	}
	
	protected abstract void releaseObject(T t);
	
	private synchronized T relink(Key key) 
	{
		Node n = hash.get(key);
		if(n == null) return null;
		if(f != n)
		{
			if (n.p != null) n.p.n = n.n;
			if (n.n != null) n.n.p = n.p;
			if (l == n) l = n.p;
			n.p = null;
			n.n = f;
			f.p = n;
			f = n;
		}
		return n.t;
	}


	public AbstractCache(long maxCost)
	{
		mx = maxCost;
	}

	public long maxCost() { return mx; }
	public void setMaxCost(long m) {mx = m; trim(mx);}
	public long totalCost() { return total; }

	public long size() { return hash.size(); }
	public long count() { return hash.size(); }
	public boolean isEmpty() { return hash.isEmpty(); }
	public synchronized Set<Key> keys() { return new HashSet<Key>(hash.keySet()); }

	public synchronized void  clear()
	{
		hash.clear(); 
		l = null;
		f= null;
		total = 0; 
	}

	public synchronized boolean insert(Key key, T object, long cost)
	{
		remove(key);
		if (cost > mx) {
			return false;
		}
		trim(mx - cost);
		Node sn  = new Node(object, cost);
		hash.put(key, sn);
		total += cost;
		Node n = sn;
		n.keyPtr = key;
		if(f != null) {f.p = n;}
		n.n = f;
		f = n;
		if (l == null) l = f;
		return true;		
	}
	public T object(Key key)
	{
		return relink(key);
	}
	
	public synchronized boolean contains(Key key) { return hash.containsKey(key); }
	
	public synchronized T take(Key key)
	{
		Node n = hash.get(key);
		if(n == null) return null;
		T t = n.t;
		n.t = null;
		unlink(n);
		return t;		
	}
	
	public synchronized boolean remove(Key key)
	{
		Node n = hash.get(key);
		if(n == null) return false;
		unlink(n);
		return true;
	}

	private void trim(long m)
	{
		Node n = l;
		while (n!=null && total > m) 
		{
			Node u = n;
			if(n != null)
			{
				n = n.p;
				if(n != null)
				{
					unlink(u);
				}
			}
		}
	}
	//private ReentrantLock m_Guard;
}
