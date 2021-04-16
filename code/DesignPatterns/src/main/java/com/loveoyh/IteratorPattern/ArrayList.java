package com.loveoyh.IteratorPattern;

import java.util.Arrays;

/**
 * @Created by oyh.Jerry to 2021/04/16 16:20
 */
public class ArrayList<T> implements List<T> {
	
	private int size;
	
	private Object[] elementData = {};

	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public Iterator<T> iterator() {
		return new ListItr();
	}
	
	public Iterator<T> snapshotArrayIterator() {
		return new SnapshotArrayIterator();
	}
	
	public boolean add(T e) {
		if(elementData.length < size+1){
			int oldCapacity = elementData.length;
			int newCapacity = size+1 + (size+1 >> 1);
			elementData = Arrays.copyOf(elementData, newCapacity);
			System.err.println("========>扩容");
		}
		elementData[size++] = e;
		return true;
	}
	
	public T get(int index) {
		return (T) elementData[index];
	}
	
	private class ListItr implements Iterator<T> {
		
		int currentIndex = 0;
		
		public boolean hasNext() {
			if(currentIndex == ArrayList.this.size){
				return false;
			}
			return true;
		}
		
		public T next() {
			return (T) ArrayList.this.elementData[currentIndex++];
		}
	}
	
	private class SnapshotArrayIterator<T> implements Iterator<T> {
		
		private List<T> snapshotData = new ArrayList<>();
		
		public SnapshotArrayIterator(){
			for(int i=0;i<ArrayList.this.size;i++){
				snapshotData.add((T) ArrayList.this.elementData[i]);
			}
		}
		
		int currentIndex = 0;
		
		public boolean hasNext() {
			if(currentIndex == snapshotData.size()){
				return false;
			}
			return true;
		}
		
		public T next() {
			return (T) snapshotData.get(currentIndex++);
		}
	}
}
