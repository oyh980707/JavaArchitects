package com.loveoyh.sort;

/**
 * 插入排序
 * @Created by oyh.Jerry to 2020/08/22 17:24
 */
public class InsertionSort {
	
	public static void main(String[] args) {
		int[] arr = {4,3,2,6,8,4,5,9,2,4,6,1,0};
		sort(arr);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr) {
		for(int i=0;i<arr.length-1;i++){
			for(int j=i+1;j>0;j--){
				if(arr[j-1]>arr[j]){
					// swap tow numbers
					arr[j-1] += arr[j];
					arr[j] = arr[j-1] - arr[j];
					arr[j-1] = arr[j-1] - arr[j];
				}else{
					break;
				}
			}
		}
	}
	
}
