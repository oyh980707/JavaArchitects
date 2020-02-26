package com.loveoyh.DelegatePattern;

import java.util.HashMap;
import java.util.Map;

/**
 * @Created by oyh.Jerry to 2020/02/26 19:55
 */
public class Leader implements IEmployee{
    
    private static Map<String, IEmployee> EMPLOYEE_REGISTER = new HashMap<>();
    
    static {
        EMPLOYEE_REGISTER.put("架构",new IEmployeeA());
        EMPLOYEE_REGISTER.put("测试",new IEmployeeB());
    }
    
    @Override
    public void doing(String command){
        EMPLOYEE_REGISTER.get(command).doing(command);
    }
    
}
