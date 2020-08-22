package com.loveoyh.sort;

/**
 * 冒泡排序
 * @Created by oyh.Jerry to 2020/08/22 16:17
 */
public class BubbleSort {
	
	public static void main(String[] args) {
		int[] arr = {4,3,2,6,8,4,5,9,2,4,6,1,0};
		sort(arr);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr){
		// 把小的往前面排
//		for(int i=0;i<arr.length-1;i++){
//			for(int j=arr.length-1;j>i;j--){
//				if(arr[i] > arr[j]){
//					// swap two numbers
//					arr[i] += arr[j];
//					arr[j] = arr[i] - arr[j];
//					arr[i] = arr[i] - arr[j];
//				}
//			}
//		}
		// 从前往后 两两交换 将最大的冒到最后
//		for(int i=0;i<arr.length-1;i++){
//			for(int j=0;j<arr.length-i-1;j++){
//				if(arr[j]>arr[j+1]){
//					// swap two numbers
//					arr[j] += arr[j+1];
//					arr[j+1] = arr[j] - arr[j+1];
//					arr[j] = arr[j] - arr[j+1];
//				}
//			}
//		}
		
		// 从前往后 两两交换 将最大的冒到最后
		for(int i=0;i<arr.length-1;i++){
			boolean flag = false;
			for(int j=0;j<arr.length-i-1;j++){
				if(arr[j]>arr[j+1]){
					// swap two numbers
					arr[j] += arr[j+1];
					arr[j+1] = arr[j] - arr[j+1];
					arr[j] = arr[j] - arr[j+1];
					flag = true;
				}
			}
			if(!flag) {
				break;
			}
		}
	}
	
}
