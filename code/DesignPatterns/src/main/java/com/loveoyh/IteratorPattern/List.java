package com.loveoyh.IteratorPattern;

/**
 * List接口
 * @Created by oyh.Jerry to 2021/04/16 16:18
 */
public interface List<T> {
	
	int size();
	
	boolean isEmpty();
	
	Iterator<T> iterator();
	
	Iterator<T> snapshotArrayIterator();
	
	boolean add(T e);
	
	T get(int index);

}
