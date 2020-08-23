package com.loveoyh.sort;

/**
 * 快排
 * @Created by oyh.Jerry to 2020/08/23 20:16
 */
public class QuickSort {
	
	public static void main(String[] args) {
		int[] arr = {59,20,17,13,28,14,23,83,10};
		sort(arr,0,arr.length-1);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr, int left, int right){
		if(left > right){
			return ;
		}
		int i = left;
		int j = right;
		
		int key = arr[i];
		
		while (i < j){
			while (i < j && arr[j]>=key){
				j--;
			}
			
			if (i < j){
				arr[i] = arr[j];
				i++;
			}
			
			while (i < j && arr[i] <= key){
				i++;
			}
			
			if (i < j){
				arr[j] = arr[i];
				j--;
			}
		}
		
		// i==j
		arr[i] = key;
		sort(arr, left, i-1);
		sort(arr, i+1, right);
	}
	
}
