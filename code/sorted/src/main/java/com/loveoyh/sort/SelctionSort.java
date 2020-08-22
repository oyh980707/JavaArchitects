package com.loveoyh.sort;

/**
 * 选择排序
 * @Created by oyh.Jerry to 2020/08/22 17:16
 */
public class SelctionSort {
	
	public static void main(String[] args) {
		int[] arr = {4,3,2,6,8,4,5,9,2,4,6,1,0};
		sort(arr);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr){
		for(int i=0;i<arr.length-1;i++){
			int minIndex = i;
			for(int j=i+1;j<arr.length;j++){
				if(arr[minIndex]>arr[j]){
					minIndex = j;
				}
			}
			if(minIndex != i){
				// swap tow numbers
				arr[i] += arr[minIndex];
				arr[minIndex] = arr[i] - arr[minIndex];
				arr[i] = arr[i] - arr[minIndex];
			}
		}
	}
	
}
