package com.loveoyh.ProxyPattern.CustomProxy;
import com.loveoyh.ProxyPattern.CustomProxy.Person;
import java.lang.reflect.*;
public class $Proxy0 implements com.loveoyh.ProxyPattern.CustomProxy.Person{
InvocationHandler h;
public $Proxy0(InvocationHandler h) { 
this.h = h;
}
public void findLove() {
try{
Method m = com.loveoyh.ProxyPattern.CustomProxy.Person.class.getMethod("findLove",new Class[]{});
this.h.invoke(this,m,new Object[]{});
}catch(Error _ex) {
}catch(Throwable e){
throw new UndeclaredThrowableException(e);
}

}
}
