package com.adi.concurrency;

import org.springframework.stereotype.Component;

public class Counter {

    private int c;

    public Counter(){
        c = 0;
    }

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public int getValue() {
        return c;
    }

}
