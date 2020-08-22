# 排序算法



## 冒泡算法(Bubble Sort)

 **基本思想：**两个数比较大小，较大的数下沉，较小的数冒起来。

 **平均时间复杂度：**O(n2) 

简单实现代码：

```java
public void sort(int[] arr){
    // 从前往后 两两交换 将最大的冒到最后
    for(int i=0;i<arr.length-1;i++){
        for(int j=0;j<arr.length-i-1;j++){
            if(arr[j]>arr[j+1]){
                // swap two numbers
                arr[j] += arr[j+1];
                arr[j+1] = arr[j] - arr[j+1];
                arr[j] = arr[j] - arr[j+1];
            }
        }
    }
}
```

优化：

设置标志位flag，如果发生了交换flag设置为true；如果没有交换就设置为false。
这样当一轮比较结束后如果flag仍为false，即：这一轮没有发生交换，说明数据的顺序已经排好，没有必要继续进行下去。 

```java
public void sort(int[] arr){
    // 从前往后 两两交换 将最大的冒到最后
    for(int i=0;i<arr.length-1;i++){
        // 每次遍历标志位都要先置为false，才能判断后面的元素是否发生了交换
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

```



## 选择排序(Selection Sort)

**基本思想：**

```text
在长度为N的无序数组中，第一次遍历n-1个数，找到最小的数值与第一个元素交换；
第二次遍历n-2个数，找到最小的数值与第二个元素交换；
。。。
第n-1次遍历，找到最小的数值与第n-1个元素交换，排序完成。
```

**平均时间复杂度：**O(n2) 

 代码实现：

```java
public void sort(int[] arr){
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
```

## 插入排序(Insertion Sort)

**基本思想：**

```text
在要排序的一组数中，假定前n-1个数已经排好序，现在将第n个数插到前面的有序数列中，使得这n个数也是排好顺序的。如此反复循环，直到全部排好顺序。
```

**平均时间复杂度：**O(n2) 

代码实现：

```java
public void sort(int[] arr) {
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
```

