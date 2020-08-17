package com.loveoyh.PrototypePattern.ShallowClone;

import java.util.List;

/**
 * 具体需要克隆的对象
 */
public class ConcretePrototypeA implements Prototype{
    private int age;
    private String name;
    private List hobbies;

    @Override
    public Prototype clone() {
        ConcretePrototypeA cPrototypeA = new ConcretePrototypeA();
        cPrototypeA.setAge(this.age);
        cPrototypeA.setHobbies(this.hobbies);
        cPrototypeA.setName(this.name);
        return cPrototypeA;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getHobbies() {
        return hobbies;
    }

    public void setHobbies(List hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "ConcretePrototypeA{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", hobbies=" + hobbies +
                '}';
    }
}
