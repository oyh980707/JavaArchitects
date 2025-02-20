# Leetcode 算法题总结

## x的平方根

1. 常用的暴力法

2. 袖珍计算器算法

   ```text
   用指数函数 exp 和对数函数 ln 代替平方根函数的方法
   ```

3. 二分查找法

   ```text
   思路：
   1. 设定下界为0，上界为x
   2. 进行二分查找，循环条件为下界必须小于等于上界
   3. 在二分查找的每一步，取中值mid[mid=l+(l-r)/2]，判断mid与x的大小关系
   	1）如果mid小于x，则下界设置为mid
   2）如果mid等于x，则返回结果
   	3）如果mid大于x，则上界设置为mid
   4. 进行下一次循环
   ```

4. 牛顿迭代法

   ```text
   牛顿迭代法是一种可以用来快速求解函数零点的方法。
   用 C 表示待求出平方根的那个整数 y=f(x)=x2−C
   确定初始值 x0 = C
   通过函数的变换，新的迭代结果为xi+1 = 0.5*(xi + c/xi)
   迭代到差值小于某个数时结束，这个数是非负数一般可以取10-6等
   得到的结果就和根接近
   ```

## 爬楼梯

n阶台阶每次走一步或者两步，总共多少种走法，本质上就是一个斐波那契数的求法。

求解方法有很多种：

1. 暴力法：通过递归的方式求解，但是他的时间复杂度O(2的n次方)，空间复杂度是O(n)
2. 对递归方式优化一个记忆数组：采用一个n+1大小的数组保存每次求的值，求过了的值就不用再求了
3. 动态规划：其实就相当于一个滚动数组，运用三个变量依次保存进三次的值来求下一次的值
4. 神奇的让我一头雾水的数学方法求解

总结跳台阶问题的变体题目

- 可一次跳三阶但仅能用一次

解题思路：

1. 给定三个数组

   ```text
   动态规划数组res:res[i]表示到第i层共有多少种合法的走法(“合法的走法”是值至多使用一次一步跳三阶的走法)
   斐波那契数组only2:only2[i]表示完全不用一次跨三阶的走法到第i层有多少种走法(必然全为合法走法)
   特例数组ct3:ct3[i]表示到第i层时一定使用了一次走三阶的所有合法走法
   ```

2. `res[i] = res[i-1] + res[i-2]`动态规划出来的是直接跨一阶和跨两阶的走法，`res[i-3]`表示的是所有的合法走法包括跳一次三阶和不跳三节的走法。`ct3[i-3]`表示的是到i-3层必然跳一次三阶的走法，所以我们现在需要的是到从i-3层到i层为跳一次三阶，所以这个结果为`res[i-3] - ct3[i-3]`

   所以得到最后的动态方程为 `res[i] = res[i-1] + res[i-2] + (res[i-3] - ct3[i-3])`

3. 最后的问题变成推到出ct3，推到出必然有一次跳三阶的次数
   结果为 `ct3[i] = ct3[i-1] + ct3[i-2] + only2[i-3]`
   `only2[i] = only2[i-1] + only[i-2]`

- 一只青蛙一次可以跳上1级台阶，也可以跳上2级……它也可以跳上n级。 

解题思路：

同理可以分析得出：f(n) = f(n-1) + f(n-2) + ... + f(1) 和 f(n-1) = f(n-2) + f(n-3) + ... + f(1)

f(n) = 2f(n-1)

## 杨辉三角

就是一个简单粗暴的解决方法，思路也比较明确，就是对于编程的思维有点死板，思维不灵活，导致做一个简单的算法题需要耗时进一个小时。

杨辉三角只求第k行的结果，就不要额外存储之前的结果了，有以下方法求解：

1. 老办法暴力递推，采用list嵌套list一行一行求出
2. 优化上个方案，只使用两个list保存当前和上一行的list
3. 进一步优化，只使用一个list，通过本身内部的值相互递推出结果(倒着计算当前行的第i项)
4. 线性递推，通过组合通项公式(C<sub>n</sub><sup>m</sup> = C<sub>n</sub><sup>m-1</sup> * (n-m+1/m)，从第n行直接计算整行，时间复杂度为O(n)，空间复杂度O(1)，不包括返回结果

## 买卖股票的最佳时机 II

和买卖股票的最佳时机 I不同的是可以买卖多次，可以采用的方法有以下几种

1. 暴力法（两种买卖股票题都适用）

2. 动态规划（两种买卖股票题都适用，区别点：买卖股票的最佳时机 I：只能买卖一次，所以在每次入股时都是昨天买入股票后手中的现金和当天的股价相反数比较。买卖股票的最佳时机 II：能卖买多次，所以可以和这次买不买股票就看昨天没买股票减去当天股价和昨天买了股票，取大的）

3. 优化动态规划，由于不需要保存很早的历史数据，只需要最近的一次历史数据，可以用滚动数组或者几个变量来滚动保存数据。最终推出结果

   ```text
   买卖股票的最佳时机 II的动态规划思路：
   1. 定义状态
   2. 思考状态转移方程
   3. 确定初始值
   4. 确定输出值
   5. 考虑优化空间
   ```

4. 贪心算法

总结：动态规划有两种求解形式：

- 自顶向下：也就是记忆化递归，求解过程会遇到重复子问题，所以需要记录每一个子问题的结果；

- 自底向上：通过发现一个问题最开始的样子，通过「递推」一步一步求得原始问题的解。

在「力扣」上的绝大多数问题都可以通过「自底向上」递推的方式去做。

## 相交链表

也就是找到两个单链表相交的起始节点。

总结一下方法：

1. 暴力法，直接遍历，时间复杂度O(m*n)
2. 哈希表法，遍历一个链表后保存，然后遍历另一个链表然后哈希表中找有没有存在，存在即是。
3. 最常用的是双指针法，两个指针同时遍历当一指针走到头就指向另一个链表头继续遍历，知道遍历两只节点相等则就是结果，如果没有交叉点，最后两个指针会同时为null。

与此同时也做了几个可以使用双指针的算法题([11. 盛最多水的容器](https://leetcode-cn.com/problems/container-with-most-water/)、[240. 搜索二维矩阵 II](https://leetcode-cn.com/problems/search-a-2d-matrix-ii/))

搜索二维矩阵 II
搜索 m x n 矩阵 matrix 中的一个目标值 target。每行的元素从左到右升序排列。每列的元素从上到下升序排列

1. 暴力法：遍历所有位置进行比较
2. 二分法搜索：以尽可能的对角线为遍历方向，每次遍历中进行两次二分遍历(行和列)
3. 搜索空间的缩减：类似于二分，将矩阵拆分成四小块，可以知道左下角和右上角肯定不包含target，所以直接排除，直接寻找左上角和右下角的数据。
4. 

## 多数元素

此算法题的解法有很多，但很多方法的时间空间复杂度都是很高
题目：[169. 多数元素](https://leetcode-cn.com/problems/majority-element/)

解题方法：

1. 暴力法：直接统计数组中每个数的数量，时间复杂度O(n^2)
2. 哈希表法：利用哈希表的key保存数，value保存出现的次数，时间空间复杂度都是O(n)
3. 排序：排好序直接取中间数
4. 随机化：随机取个数，然后验证，理论上最坏情况下的时间复杂度为O(∞)
5. 分治：就问题分解成小问题挨个解决，依次分左右两部分统计两边的众数，如果一样则就需要在整个区间进行计算
6. Boyer-Moore 投票算法（推荐使用的算法）：时间复杂度O(n) 空间复杂度O(1)
   1. 我们维护一个候选众数 candidate 和它出现的次数 count。初始时 candidate 可以为任意值，count 为 0；
   2. 我们遍历数组 nums 中的所有元素，对于每个元素 x，在判断 x 之前，如果 count 的值为 0，我们先将 x 的值赋予 candidate，随后我们判断 x：
      1）如果 x 与 candidate 相等，那么计数器 count 的值增加 1；
      2）如果 x 与 candidate 不等，那么计数器 count 的值减少 1。
   3. 在遍历完成后，candidate 即为整个数组的众数。

## 快乐数

「快乐数」定义为：
对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。然后重复这个过程直到这个数变为 1，也可能是无限循环 但始终变不到 1。如果 可以变为 1，那么这个数就是快乐数。

我在开始考虑是直接寻找，但是如果不是快乐数我无法终止循环
初步的探索，猜测会有以下三种可能。

1. 最终会得到 1。
2. 最终会进入循环。
3. 值会越来越大，最后接近无穷大。

但是第三种情况我任务不可能发生，在int范围内计算了下最大也不会超过int，所以不会越来越大益处。

题解方法：
1. 哈希集合检测循环：利用Set集合来检测每次计算下一个数后，判断数是否被计算过，如果计算过就会导致循环
2. 快慢指针法：这个和链表检测是否有环一样
3. 数学的方法：统计发现只会最终形成一个环链“4→16→37→58→89→145→42→20→4”，所以只需判断形成的数是否存在这些数中，存在就会存在环，否则就可以形成快乐数



## 质数

质数的定义：在大于 1 的自然数中，除了 1 和它本身以外不再有其他因数的自然数。

1. 枚举

```text
遍历每个数，判断是否是质数。（由于判断x是否是质数，只需要判断小于等于根号x的数是否有因数即可,这样单次检查的时间复杂度从O(n)降低至了 O(根号n)。）
这种方法时间复杂度很高，可能超时。
```

2. 埃氏筛( 厄拉多塞 )

```text
如果x是质数，那么大于x的x的倍数 2x,3x,… 一定不是质数
时间复杂度：O(nloglogn),空间复杂度：O(n)
```

## 二叉树遍历

### 中序遍历

1. 递归

```text
访问左子树—根节点—右子树
每一个递归中，都是将左子树递归遍历完，然后将自己加入结果集，然后递归遍历右子树
```

2. 迭代法

```text
通过维护一个栈来迭代
每次迭代中
1. 将全部的左节点加入到栈中，循环条件为栈不为空
2. 弹出一个节点，并将其值加入结果集
3. 将根节点赋值为该节点的右节点
4. 继续迭代
```

3. Morris 中序遍历

```text
它能将非递归的中序遍历空间复杂度降为O(1)。

Morris 遍历算法整体步骤如下（假设当前遍历到的节点为 x）：
1. 如果 x 无左孩子，先将 x 的值加入结果集，再访问 x 的右孩子，即 x = x.right
2. 如果 x 有左孩子，则找到 x 左子树上最右的节点（即左子树中序遍历的最后一个节点，x 在中序遍历中的前驱节点），我们记为 predecessor。根据 predecessor 的右孩子是否为空，进行如下操作。
 2.1 如果 predecessor 的右孩子为空，则将其右孩子指向 x，将x的左孩子赋值为null，在此之前访问 x 的左孩子，即 x = x.left。
 2.2 如果 predecessor 的右孩子不为空，则此时其右孩子指向 x，说明我们已经遍历完 x 的左子树，我们将 predecessor 的右孩子置空，将 x 的值加入答案数组，然后访问 x 的右孩子，即 x = x.right。
3. 重复上述操作，直至访问完整棵树。

这种方法类似于事先将所有节点改成一个链表，依次访问
```

### 后续遍历

1. 递归

```text
访问左子树—右子树—根节点
```

2. 迭代法

```text
通过维护一个栈来迭代
每次迭代中
1. 将全部的左节点加入到栈中，循环条件为栈不为空
2. 弹出一个节点，判断是否有右节点
	如果没有或者右节点为上一次操作的节点则就将其值加入结果集，并将根节点赋值为null
	如果有，则继续推进栈中，将根节点赋值为该节点的右节点
4. 继续迭代
```

3. Morris 后序遍历

// 待了解

## 用队列实现栈

1. 使用两个队列

```text
保存数据使用队列1
入栈将入到队列2中，然后将队列1的数据全部出队，入队到队列2中，两个队列调换
```

2. 使用一个队列

```text
入栈 入队列后将前面的数据全部出队然后依次入队
```

3. 使用List集合



## 用栈实现队列

1. 使用两个栈

```text
inStack outStack
方法1
每次入栈就将所有数据出栈到临时栈，然后入栈元素，将临时栈全部数据入栈，就将最后一个入队的数据放到栈底

方法2
每次入栈直接入栈到inStack,每次操作pop，peek，empty时，都要判断outStack是否为空，为空则就需要将inStack的数据出栈入栈到outStack中，保证了先进先出顺序。
```

## 回文

给你一个字符串 `s`，找到 `s` 中最长的回文子串

1. 动态规划

```text
1. 初始化二维数组dp[][]，记录字符串i->j位置是否为回文 dp[i][j]
2. 枚举子串长度，从长度为2开始枚举直到长度为字符串长度。
3. 每次枚举都会遍历字符串
4. 每次遍历都会检查长度两端的字符是否相等，如果相等判断去除两端字符后是否为回文，若是就是回文，标记true，还需判断是否长度最大，最大则记录最大。
```

2. 中心扩展法

```text
该方法和我首次想到的一致，但我的实现超时

1. 遍历字符串的每个字符
2. 根据这个字符，想两边扩展，看是否是回文，此处分两种情况扩展
	- 一种是回文字符串字符为偶数时，需要从第i和i+1两个位置向两边扩展
	- 一种是回文字符串字符为奇数时，需要从第i位置向两边扩展
```

## 无重复字符的最长子串

滑动窗口有两种实现

1. 我的实现，采用队列的方式

```text
1. 循环遍历字符串的每个字符
2. 先判断队列中是否存在该字符，如果存在就依次pop，知道将存在的这个字符pop出来。
3. 然后offer进这个字符。并判断当前队列的字符数是否是最大长度的字符串，如果是最长，则保存
4. 最后返回最大长度
```

2. 官网提供解题思路

```text
1. 使用Set来保存窗口内的字符
2. 循环遍历每个字符，并使用额外的一个变量保存滑动窗口的最右位置
3. 每次循环遍历，删除Set中的最左端元素。
4. 然后最右端向前扩展，扩展到最右端字符在集合中存在就停止
5. 判断Set中的元素数量是否最大（是否最大字符子串长度）
6. 循环结束，返回结果
```

## Z字形变换

[6. Z 字形变换](https://leetcode-cn.com/problems/zigzag-conversion/)

我采用的方式是：按行排序（按字符串字符访问）

```text
依次访问字符串的每个字符，找到字符对应的行，添加进去，最后将所有的行进行汇总。具体的方案图如下：
```

![](./images/Z字形变换-按行排序.png)

方法2：按行访问

```text
首先访问 行0中的所有字符，接着访问行1，然后行2，依此类推...
对于所有整数索引i，k行
行1中的字符位于 i*2(numRows-1)
行numRows中的字符位于 i*2(numRows-1)+numRows-1

中间行 k 中的字符位于 i*2(numRows-1) + k-1 以及 (i+1)*2(numRows-1) - (k-1)

我的实现代码如下：

public String convert(String s, int numRows) {
    if(numRows == 1) return s;
    StringBuilder sb = new StringBuilder();

    for(int k=1;k<=numRows;k++){
        for(int i=0;i<s.length();i++){
            if(k == 1){
                if((i%(2*(numRows-1))) == 0){
                sb.append(s.charAt(i));
                }
            }

            if(k == numRows){
                if(i%(2*(numRows-1)) == numRows-1){
                sb.append(s.charAt(i));
                }
            }

            if(k>1 && k<numRows && (i%(2*(numRows-1)) == k-1 
            			|| i%(2*(numRows-1)) == 2*(numRows-1)-(k-1))){
            sb.append(s.charAt(i));
            }
        }
    }

    return sb.toString();
}
```

## 排列

[46. 全排列](https://leetcode-cn.com/problems/permutations/)

我采用的是回朔法，回朔法有两种实现思路

```text
第一种方式：回朔函数参数有：以选中的数列，所有参与全排列数集合，结果集，将要处理什么位置的排列数
每次回朔都会判断是否存在选中的数列中，不存在则添加，然后递归调用，处理下一个位置。

public List<List<Integer>> permute(int[] nums) {
    List<Integer> selectableNums = new ArrayList<>();
    for(int num : nums){
    	selectableNums.add(num);
    }
    List<List<Integer>> res = new ArrayList<>();
    backtrack(new ArrayList<>(), selectableNums, res, 0);
    return res;
    }

public void backtrack(List<Integer> selectNums, List<Integer> selectableNums, List<List<Integer>> res, int n){
    if(selectableNums.size() == n){
        res.add(new ArrayList<Integer>(selectNums));
        return ;
    }

    for(int i=0;i<selectableNums.size();i++){
        if(selectNums.contains(selectableNums.get(i))) continue;
        selectNums.add(selectableNums.get(i));
        backtrack(selectNums, selectableNums, res, n+1);
        selectNums.remove(selectableNums.get(i));
    }
}

另一种实现就是，将选中的数列和将要选的数放一个数组内，只是采用交换位置数来处理。
public List<List<Integer>> permute(int[] nums) {
    List<Integer> selectableNums = new ArrayList<>();
    for(int num : nums){
    	selectableNums.add(num);
    }
    List<List<Integer>> res = new ArrayList<>();
    backtrack(selectableNums.size(), selectableNums, res, 0);
    return res;
}

public void backtrack(int len, List<Integer> selectableNums, List<List<Integer>> res, int n){
    if(n == len){
        res.add(new ArrayList<Integer>(selectableNums));
        return ;
    }

    for(int i=n;i<len;i++){
        Collections.swap(selectableNums, i, n);
        backtrack(len, selectableNums, res, n+1);
        Collections.swap(selectableNums, n, i);
    }
}

```

[31. 下一个排列](https://leetcode-cn.com/problems/next-permutation/)

```text
对于一个数组，找到此时对应的下一个排列的最小
首先需要从右到左找到一个较小的数，和最右边其他最小的而且比该数大的数交换，交换后右边的排序成最小数列即可

public void nextPermutation(int[] nums){
    int i = nums.length-2;
    // 找到最右边数序列中较小的一个，第一个满足nums[i]<nums[i+1]
    while(i >= 0 && nums[i]>=nums[i+1]){
    	i--;
	}
	// 若没有说明是最大值，直接反转
    if(i<0){
        reverse(nums, 0, nums.length-1);
        return ;
    }
    // 找到较小的那个数，找到比这个数小的最大数
    int j = nums.length-1;
    while(j>=i && nums[j]<=nums[i]){
    	j--;
    }
    // 交换
    swap(nums,i,j);
	// 反转
    reverse(nums, i+1, nums.length-1);
}
private void swap(int[] nums, int var1, int var2){
    nums[var1] = nums[var1]+nums[var2];
    nums[var2] = nums[var1]-nums[var2];
    nums[var1] = nums[var1]-nums[var2];
}

private void reverse(int[] nums, int begin, int end){
    while(begin < end){
        swap(nums,begin,end);
        begin++;
        end--;
    }
}


```

[47. 全排列 II](https://leetcode-cn.com/problems/permutations-ii/)

```text
思路和全排列一致，方法为回朔+剪枝

    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> selectNums = new ArrayList<>();
        for(int num : nums){
            selectNums.add(num);
        }
        backtrack(nums.length, selectNums, res, 0);
        return res;
    }

    public void backtrack(int n,List<Integer> selectNums, List<List<Integer>> res, int index){
        if(index == n){
            res.add(new ArrayList<>(selectNums));
            return;
        }
        // 保存这次该位置可以放置的数，要求不重复
        Set<Integer> set = new HashSet<>();
        set.add(selectNums.get(index));
        for(int i=index;i<n;i++){
        	// 该位置原来的值不检查，检查后面需要放置该位置的数是否重复
            if(i!=index && set.contains(selectNums.get(i))){
                continue;
            }
            set.add(selectNums.get(i));
            Collections.swap(selectNums, i, index);
            backtrack(n, selectNums, res, index+1);
            Collections.swap(selectNums, index, i);
        }
    }
```
## 组合

 [77. 组合](https://leetcode-cn.com/problems/combinations/)

 1. 递归+剪枝

 ```text
      public void dfs(int cur, int n, int k) {
        // 剪枝：temp 长度加上区间 [cur, n] 的长度小于 k，不可能构造出长度为 k 的 temp
        if (temp.size() (n - cur 1) < k) {
            return;
        }
        // 记录合法的答案
        if (temp.size() == k) {
            ans.add(new ArrayList<Integer>(temp));
            return;
        }
        // 考虑选择当前位置
        temp.add(cur);
        dfs(cur 1, n, k);
        temp.remove(temp.size() - 1);
        // 考虑不选择当前位置
        dfs(cur 1, n, k);
    }
 ```

 2. 字典序法

 ```text
 二进制列举法：
 二进制数              方案
 00111         3,2,1
 01011         4,2,1
 01101         4,3,1
 01110         4,3,2
 10011         5,2,1
 10101         5,3,1
 10110         5,3,2
 11001         5,4,1
 11010         5,4,2
 11100         5,4,3
 
思路就是给定固定长度k+1个元素，最后一个元素是哨兵。然后挨个比较不满足temp.get(j) 1 == temp.get(j 1)时就讲该元素加一，效果就是k个元素从第k个元素到第一个元
素每次每个元素逐级加一并加入结果集，直到满足第k的元素比k+1的元素少1时，所有结果
都列举完。
class Solution {
  List<Integer> temp = new ArrayList<Integer>();
  List<List<Integer>> ans = new ArrayList<List<Integer>>();

  public List<List<Integer>> combine(int n, int k) {
      List<Integer> temp = new ArrayList<Integer>();
      List<List<Integer>> ans = new ArrayList<List<Integer>>();
      // 初始化
      // 将 temp 中 [0, k - 1] 每个位置 i 设置为 i 1，即 [0, k - 1] 存 [1, k]
      // 末尾加一位 n 1 作为哨兵
      for (int i = 1; i <= k; ++i) {
          temp.add(i);
      }
      temp.add(n 1);
      
      int j = 0;
      while (j < k) {
          ans.add(new ArrayList<Integer>(temp.subList(0, k)));
          j = 0;
          // 寻找第一个 temp[j] 1 != temp[j 1] 的位置 t
          // 我们需要把 [0, t - 1] 区间内的每个位置重置成 [1, t]
          while (j < k && temp.get(j) 1 == temp.get(j 1)) {
              temp.set(j, j 1);
              ++j;
          }
          // j 是第一个 temp[j] 1 != temp[j 1] 的位置
          temp.set(j, temp.get(j) 1);
      }
      return ans;
  }
}
 ```

```text
思路和全排列一致，方法为回朔+剪枝

    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> selectNums = new ArrayList<>();
        for(int num : nums){
            selectNums.add(num);
        }
        backtrack(nums.length, selectNums, res, 0);
        return res;
    }

    public void backtrack(int n,List<Integer> selectNums, List<List<Integer>> res, int index){
        if(index == n){
            res.add(new ArrayList<>(selectNums));
            return;
        }
        // 保存这次该位置可以放置的数，要求不重复
        Set<Integer> set = new HashSet<>();
        set.add(selectNums.get(index));
        for(int i=index;i<n;i++){
              // 该位置原来的值不检查，检查后面需要放置该位置的数是否重复
            if(i!=index && set.contains(selectNums.get(i))){
                continue;
            }
            set.add(selectNums.get(i));
            Collections.swap(selectNums, i, index);
            backtrack(n, selectNums, res, index+1);
            Collections.swap(selectNums, index, i);
        }
    }
```
## 三数问题

[15. 三数之和](https://leetcode-cn.com/problems/3sum/)

```text
1. 暴力，枚举每种情况
2. 排序+双指针法：排序后最外层循环遍历每个不重复的数，内层使用两个指针指向下一个数和最后一个数，依次逼近target数，判断没有提前结束。双指针移动也需要判断是否重复，跳过重复数据。
```

[16. 最接近的三数之和](https://leetcode-cn.com/problems/3sum-closest/)

```text
1. 暴力，枚举每种情况，排序后三层循环
2. 排序+双指针法：与三数之和类似，只需要加几个变量记录组接近的数即可。
```

## LRU

[146. LRU 缓存机制](https://leetcode-cn.com/problems/lru-cache/)

实现的方式有两种

1. 通过类继承LinkedHashMap实现，由于LinkedHashMap是链表形式的HashMap，满足实现LRU的条件。将访问顺序accessOrder设置为true，意思是每次访问之后都会将该节点放到尾结点去。

```java
class LRUCache extends LinkedHashMap<Integer, Integer>{
    private int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    public int get(int key) {
        return super.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        super.put(key, value);
    }

    // 在每次插入一个节点后，需要处理插入后的操作，也就是当前判断是否删除最老的节点，也就是头结点，最新被访问的节点位于尾结点，每次访问都会
    // 将访问的节点放置于最后的节点，设置该操作的值为accessOrder=true;
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity; 
    }
}
```

2. 通过参考类似于LinkedHashMap的实现原理，自己实现一个类保存每个节点，然后借助HashMap的快速检索来保存key对应的节点

```java
具体实现原理参考HashMap底层原理
```



## 最长公共子序列

[1143. 最长公共子序列](https://leetcode-cn.com/problems/longest-common-subsequence/)

本题解法最简单的是使用动态规划的方法解决：

```text
给定两个字符串text1和text2，返回这两个字符串的最长公共子序列的长度。
假设字符串text1的长度为m，text2的长度为n，则要维护一个dp[m+1][n+1]的二维数组，dp[i][j]表示的含义是：text1前i个字符的字符串和text2前i个字符的字符串的最长公共子序列。

边界考虑，当i或者j等于0时最长公共子序列都为零

当i>0且j>0时：
考虑dp[i][j]的计算
当text1的第i个字符和text2的第j个字符一样时：dp[i][j]=dp[i-1][j-1] + 1
当text1的第i个字符和text2的第j个字符不一样时：dp[i][j]=Math.max(dp[i-1][j-1], dp[i-1][j-1]);
最后返回dp[m][n]即可
```

反转没对括号间的子串

[1190. 反转每对括号间的子串](https://leetcode-cn.com/problems/reverse-substrings-between-each-pair-of-parentheses/)

1. 我开始采用递归的方式实现，思路明确，可能代码能力差了点，所以未能实现

```text
思路是每次遇到左括号就递归处理子串，最终返回一个反转的子串
```

2. 采用栈的方式实现，思路简单明确

```java
class Solution {
    public String reverseParentheses(String s) {
        Stack<String> stack = new Stack<>();

        StringBuilder sb = new StringBuilder();
        for(int i=0;i<s.length();i++){
            char c = s.charAt(i);
            // 每当遇到"("时，将当前 前面添加的字符push进栈，然后清空sb
            if('(' == c){
                stack.push(sb.toString());
                sb.setLength(0);
            // 每当遇到")"时，就需要将字符反转，然后将前一次的栈的内容pop 添加到sb的头部
            }else if(')' == c) {
                sb.reverse();
                sb.insert(0, stack.pop());
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
```

3. 预处理括号

```java
public String reverseParentheses(String s) {
    int n = s.length();
    int[] pair = new int[n];
    
    // 先保存没对括号的对应位置
    Stack<Integer> stack = new Stack<>();
    for(int i=0;i<n;i++){
        if(s.charAt(i) == '(') {
            stack.push(i);
        }
        else if(s.charAt(i) == ')') {
            int j = stack.pop();
            pair[i] = j;
            pair[j] = i;
        }
    }
    
    // 
    int i = 0;
    int step = 1;
    StringBuilder sb = new StringBuilder();
    while(i<n) {
        if(s.charAt(i) == '(' || s.charAt(i) == ')') {
            i = pair[i];
            step = -step;
        }
        else {
            sb.append(s.charAt(i));
        }
        i += step;
    }
    return sb.toString();
}
```









































