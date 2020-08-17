package com.loveoyh.PrototypePattern.DeepClone;

import java.io.*;
import java.util.Date;

public class Dolly extends Sheep implements Cloneable, Serializable {
    private Identity identity;

    public Dolly(){
        this.height = 50;
        this.weight = 50;
        this.birthday = new Date();
        this.identity = new Identity();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    public Object deepClone(){
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Dolly copy = (Dolly)ois.readObject();
            copy.birthday = new Date();
            return copy;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Object shallowClone(Dolly target){
        Dolly dolly = new Dolly();
        dolly.height = target.height;
        dolly.weight = target.weight;
        dolly.identity = target.identity;
        dolly.birthday = new Date();
        return dolly;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
