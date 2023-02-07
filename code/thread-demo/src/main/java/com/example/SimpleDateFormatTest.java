package com.example;

import java.text.SimpleDateFormat;

public class SimpleDateFormatTest {

    ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public static void main(String[] args) {

    }

}
