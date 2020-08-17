package com.loveoyh.ProxyPattern.StaticProxy;

/**
 * 父亲帮儿子介绍
 */
public class Father {
    private Person son;

    public Father(Person son){
        this.son = son;
    }

    public void findLove(){
        System.out.println("父母介绍对象");
        son.findLove();
        System.out.println("双方同意，确定关系");
    }
}
