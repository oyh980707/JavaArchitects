# HashMap

## HashMap底层原理

推荐好文：

[Jdk1.8中的HashMap实现原理](https://www.cnblogs.com/chengdabelief/p/7419776.html)

[HashMap底层实现原理/HashMap与HashTable区别/HashMap与HashSet区别](https://www.cnblogs.com/beatIteWeNerverGiveUp/p/5709841.html)

[总结HashMap实现原理分析](https://blog.csdn.net/hefenglian/article/details/79763634)

### HashMap的put底层原理

JDK 8的HashMap的put的实现步骤

1. 对key的hashCode()做hash，然后再计算index

2.  如果没碰撞直接放到bucket里

3. 如果碰撞了，看第一个节点收否相等，相等则替换。判断依据(hashCode是否相同，且key是否相等)
   否则看是否为红黑树，如果是则调用putTreeVal
   若都不是就当链表处理，遍历链表每个元素

   判断是否最后一个元素，是则创建一个新的节点添加到最后，然后判断节点数大于等于8个，则转换为红黑树
   否则判断hashCode是否相同，且key是否相等，相等则替换

4. 判断实际存在的键值对数量size是否超多了最大容量threshold，如果超过，进行扩容

详细代码如下:

```java
// 计算hash值
// 在JDK1.8的实现中，优化了高位运算的算法，通过hashCode()的高16位异或低16位实现的
// 主要是从速度、功效、质量来考虑的，这么做可以在数组table的length比较小的时候
// 也能保证考虑到高低Bit都参与到Hash的计算中，同时不会有太大的开销。
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
} 

public V put(K key, V value) {
	return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 判断是否存在或者长度是否为零，否则resize
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // 通过计算的hash值取模运算，找到对应的桶位置
    // hash & (n - 1) 与 hash % n 类似，但是前者速度快
    // 如果该位置不存在(未发生碰撞)元素则创建
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        // 判断hash值是否相等，且key也相等。若是，则替换原来的值
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        // 判断是否为红黑树
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        // 否则为链表
        else {
            // 遍历所有元素
            for (int binCount = 0; ; ++binCount) {
                // 如果发现是最后一个则创建一个新的节点添加到最后
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 如果节点数大于等于8个，则转换为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            // onlyIfAbsent: if true, don't change existing value
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    // 被修改的次数 -- fail-fast机制
    ++modCount;
    // 插入成功后，判断实际存在的键值对数量size是否超多了最大容量threshold，如果超过，进行扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```



### HashMap的get底层原理

JDK 8的HashMap的get的实现步骤

1. 判断表是否存在，大小是否大于零，计算桶的位置是否有对应的值
2. 是否命中第一个节点
3. 未命中则从链表或者树中获取

详细代码如下:

```java
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    // 判断表是否存在，大小是否大于零，计算桶的位置是否有对应的值
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        // 总是检查链表第一个元素是否是对应的值
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            // 是否为红黑树
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            // 否则遍历链表所有搜索发现值
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```



## JDK 8 对 JDK 7 的HashMap的优化

### 扩容机制

JDK 7  使用了单链表的头插入方式，同一位置上新元素总会被放在链表的头部位置；这样先放在一个索引上的元素终会被放到Entry链的尾部(如果发生了hash冲突的话），这一点和Jdk1.8有区别

在 JDK 8 中我们使用的是2次幂的扩展(指长度扩为原来2倍)，所以，元素的位置要么是在原位置，要么是在原位置再移动2次幂的位置。也就是说元素在重新计算hash之后，因为n变为2倍，那么n-1的mask范围在高位多1bit，因此，**我们在扩充HashMap的时候，不需要像JDK1.7的实现那样重新计算hash，只需要看看原来的hash值在新增的那个bit位是1还是0就好了，是0的话索引没变，是1的话索引变成“原索引+oldCap**

这个设计确实非常的巧妙，既省去了重新计算hash值的时间，而且同时，由于新增的1bit是0还是1可以认为是随机的，因此resize的过程，均匀的把之前的冲突的节点分散到新的bucket了。这一块就是JDK1.8新增的优化点。有一点注意区别，JDK1.7中rehash的时候，旧链表迁移新链表的时候，如果在新表的数组索引位置相同，则链表元素会倒置