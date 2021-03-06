package com.zhkrb.netowrk.retrofit;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SchedulerProvider implements BaseSchedulerProvider {

    @NonNull
    private static SchedulerProvider sInstance;

    public SchedulerProvider() {
    }

    public static synchronized SchedulerProvider getInstance(){
        if (sInstance == null){
            synchronized (SchedulerProvider.class){
                if (sInstance == null){
                    sInstance = new SchedulerProvider();
                }
            }
        }
        return sInstance;
    }

    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    public <T> ObservableTransformer<T,T> applySchedulers(){
        return observable -> observable.subscribeOn(io())
                .observeOn(ui());
    }

    public <T> ObservableTransformer<T,T> applySchedulersAllIO(){
        return observable -> observable.subscribeOn(io())
                .observeOn(io());
    }

}

interface BaseSchedulerProvider {

    /**
     * ui线程
     * @return
     */
    Scheduler ui();

    /**
     * io线程
     * @return
     */
    Scheduler io();

}