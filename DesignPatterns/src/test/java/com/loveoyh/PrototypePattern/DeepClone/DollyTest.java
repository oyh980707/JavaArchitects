package com.loveoyh.PrototypePattern.DeepClone;

public class DollyTest {
    public static void main(String[] args) {
        try {
            Dolly dolly = new Dolly();
            Dolly clone = (Dolly) dolly.clone();
            System.out.println("深克隆："+(dolly.getIdentity() == clone.getIdentity()));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Dolly dolly = new Dolly();
        Dolly clone = (Dolly) dolly.shallowClone(dolly);
        System.out.println("浅克隆："+(dolly.getIdentity() == clone.getIdentity()));
    }
}
