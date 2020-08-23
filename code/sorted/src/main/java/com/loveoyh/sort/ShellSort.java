package com.loveoyh.sort;

/**
 * 希尔排序
 * @Created by oyh.Jerry to 2020/08/23 19:55
 */
public class ShellSort {
	
	public static void main(String[] args) {
		int[] arr = {59,20,17,13,28,14,23,83,10};
		sort(arr);
		for (int i : arr) {
			System.out.print(i+" ");
		}
	}
	
	public static void sort(int[] arr){
		int incre = arr.length;
		while (incre > 1){
			incre = incre / 2;
			for(int i=0;i<incre;i++){
				for(int j=i+incre;j<arr.length;j+=incre){
					for(int k=j;k>i;k-=incre){
						if(arr[k]<arr[k-incre]){
							// swap two numbers
							arr[k] = arr[k] + arr[k-incre];
							arr[k-incre] = arr[k] - arr[k-incre];
							arr[k] = arr[k] - arr[k-incre];
						}else{
							break;
						}
					}
				}
			}
		}
	}

}
