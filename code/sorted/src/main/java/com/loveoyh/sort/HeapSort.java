package com.loveoyh.sort;

import java.util.ArrayList;

/**
 * 堆排序
 * @Created by oyh.Jerry to 2020/09/09 14:32
 */
public class HeapSort {
	
	public static void main(String[] args) {
		int[] arr = {72,6,57,88,60,42,83,73,48,85};
		sort(arr);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr){
		int temp = 0;
		MakeMinHeap(arr);
		
		for(int i=arr.length-1;i>0;i--){
			temp = arr[0];
			arr[0] = arr[i];
			arr[i] = temp;
			MinHeapFixdown(arr,0,i);
		}
	}

	public static void MakeMinHeap(int[] arr){
		for(int i=(arr.length-1)/2;i>=0;i--){
			MinHeapFixdown(arr, i, arr.length);
		}
	}
	
	private static void MinHeapFixdown(int[] arr, int i, int n) {
		int j = 2*i+1;
		int temp = 0;
		
		while(j < n){
			if(j+1<n && arr[j+1]<arr[j]){
				j++;
			}
			
			if(arr[i] <= arr[j]){
				break;
			}
			
			temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
			
			i = j;
			j = i*2+1;
		}
	}
	
}
