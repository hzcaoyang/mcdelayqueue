package org.martin.caoy.service;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class ServiceThread implements Runnable{

    /**
     * wait point has been notified
     */
    private AtomicBoolean hasNotified = new AtomicBoolean(false);
    protected final CountDownLatch waitPoint = new CountDownLatch(1);

    /**
     *
     */
    private AtomicBoolean started = new AtomicBoolean(false);


    protected Thread thread;

    protected final Boolean isDaemon;

    private final String serviceName;



    public ServiceThread(String serviceName){
        this.serviceName = serviceName;
        this.isDaemon = false;
    }


    public void start() {

        if (!started.compareAndSet(false, true)) {
            return;
        }
        started.compareAndSet(false, true);
        this.thread = new Thread(this, serviceName);
        this.thread.setDaemon(isDaemon);
        this.thread.start();
    }


    @Override
    public void run() {
        if(started.get()){
            this.doRun();
        }
    }

    public void stop(){
        if(!this.started.compareAndSet(true, false)){
            return;
        }
    }

    public void wakeup(){
       if(hasNotified.compareAndSet(false, true)){
           waitPoint.countDown();
       }
    }

    protected abstract void doRun();
}
