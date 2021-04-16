package com.loveoyh.IteratorPattern;

/**
 * 迭代器接口
 * @Created by oyh.Jerry to 2021/04/16 16:16
 */
public interface Iterator<T> {

	boolean hasNext();

	T next();
	
}
