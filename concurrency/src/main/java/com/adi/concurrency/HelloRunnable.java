package com.adi.concurrency;

public class HelloRunnable implements Runnable{

    @Override
    public void run() {
        System.out.println("Hello from a thread!");
    }
}
