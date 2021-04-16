package com.loveoyh.IteratorPattern;

/**
 * @Created by oyh.Jerry to 2021/04/16 16:33
 */
public class Test {
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		Iterator<Integer> iterator = list.iterator();
		while (iterator.hasNext()){
			Integer next = iterator.next();
			System.out.println("=====> " + next);
		}
		
		Iterator<Integer> snapshotArrayIterator = list.snapshotArrayIterator();
		
		while (snapshotArrayIterator.hasNext()){
			list.add(666);
			System.out.println("------------> "+snapshotArrayIterator.next());
		}
	}
	
}
