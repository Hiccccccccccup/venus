package com.jozz.venus.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SyncThread implements Runnable {

    @Override
    public void run() {
        syncMethod();
    }

    private void syncMethod() {
        log.info("thread in");
        synchronized (this) {
            try {
                log.info("thread start");
                TimeUnit.SECONDS.sleep(2);
                log.info("thread end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Thread(new SyncThread1(), "thread-1").start();
//        new Thread(new SyncThread1(), "thread-2").start();
        SyncThread syncThread = new SyncThread();
//        SyncThread syncThread1 = new SyncThread();
        new Thread(syncThread, "thread-1").start();
//        new Thread(syncThread1, "thread-2").start();
    }
}
