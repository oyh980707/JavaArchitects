package com.loveoyh.DelegatePattern;

/**
 * @Created by oyh.Jerry
 * @Date 2020/02/26 19:49
 * Boss类
 */
public class Boss {
    public void command(String command,Leader leader){
        leader.doing(command);
    }
}
