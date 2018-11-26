package com.example.mohammedmorsemorsefcis.reactiveprogrammingfulldemo;

import android.animation.Animator;
import android.app.DownloadManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.animation.AnimationUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohammedmorsemorsefcis.reactiveprogrammingfulldemo.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscription;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
Observable data;
Observable<String> myData;
ActivityMainBinding activityMainBinding;
Observer observer;
    CountDownTimer timer;
    String Data=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding=DataBindingUtil.setContentView(this,R.layout.activity_main);
        EventBus.getDefault().register(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AnimateIt(View view){
        Animator animator=ViewAnimationUtils.createCircularReveal(view,view.getWidth()/2,view.getHeight()/2,0, (float) Math.hypot(view.getWidth()/2,view.getHeight()/2));
        animator.start();
    }

    public void RunAsyntask(View view) {

        //Animate it First
      //  AnimateIt(view);
    //Start Counter First
        UpdateCounter(1);
        //Start the Operation
        StartProcess(1);
        //Stop Counter

    }

    public void RunRX(View view) {
        //Animate it First
     //   AnimateIt(view);
        //Start Counter First
        UpdateCounter(2);
        //Start the Operation
        StartProcess(2);
        //Stop Counter

    }
    public void UpdateCounter(final int TextviewNumber){
        timer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                   if(TextviewNumber==1){
                      activityMainBinding.AsyntaskTime.setText(String.valueOf(30000-millisUntilFinished)+"mSec");
                   }
                   else {
                       activityMainBinding.ReactiveTime.setText(String.valueOf(30000-millisUntilFinished)+"mSec");
                   }
                Log.i("Morse", "onTick: "+millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }
    //I violate a lot of Principles on OOD as Single and Open Closed so Please Don`t kill me or till me that i`m roud <3
    public void StartProcess(int NumOfOberation){
        if(NumOfOberation==1){
           AsynTaskProcess process=new AsynTaskProcess();
           process.execute();
        }
        else{
            myData=Observable.defer(new Callable<ObservableSource<? extends String>>() {
                @Override
                public ObservableSource<? extends String> call() throws Exception {
                    // Toast.makeText(MainActivity.this, Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                    Log.i("Morse", "call: "+Thread.currentThread().getName());
                    URL uri=new URL("http://api.themoviedb.org/3/movie/top_rated?api_key=107ed75bf9e25ec06bfe9fd33d042579");
                    HttpURLConnection connection= (HttpURLConnection) uri.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    InputStream stream=connection.getInputStream();
                    InputStreamReader reader=new InputStreamReader(stream);
                    Scanner scanner=new Scanner(reader);

                    while (scanner.hasNext()){
                        Data+=scanner.nextLine();
                    }
                    return Observable.just(Data);
                }
            });
            observer=new Observer() {
                @Override
                public void onSubscribe(Disposable d) {
                    Toast.makeText(MainActivity.this, Thread.currentThread().getName()+"Subscribed ya Geeks", Toast.LENGTH_SHORT).show();
                    Log.i("Morse", "onSubscribe: Done");
                }

                @Override
                public void onNext(Object o) {
                    //   Toast.makeText(MainActivity.this, Thread.currentThread().getName()+"The result is "+o.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Morse", "onNext: "+o.toString());
                    activityMainBinding.ReactiveData.setText(o.toString());
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this,Thread.currentThread().getName()+ "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("Morse", "onError: "+e.getLocalizedMessage());
                     activityMainBinding.ProcessSuccessReact.setText("Error");
                     activityMainBinding.ProcessSuccessReact.setTextColor(Color.RED);
                }

                @Override
                public void onComplete() {
                    Toast.makeText(MainActivity.this, Thread.currentThread().getName()+"The Emitt Process Finish", Toast.LENGTH_SHORT).show();
                    Log.i("Morse", "onComplete: Done");
                    activityMainBinding.ProcessSuccessReact.setText("Success");
                    activityMainBinding.ProcessSuccessReact.setTextColor(Color.GREEN);
                    timer.cancel();

                }
            };
            myData.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(observer);
        }
    }
    @Subscribe()
    public void getDataFromEventBus(String Data){
        timer.cancel();
        if(Data==null){
            activityMainBinding.AsyntaskData.setText("Empty");
            activityMainBinding.ProcessSuccess.setText("Error");
            activityMainBinding.ProcessSuccess.setTextColor(Color.RED);
        }
        else{    activityMainBinding.AsyntaskData.setText(Data);
            activityMainBinding.ProcessSuccess.setText("Success");
            activityMainBinding.ProcessSuccess.setTextColor(Color.GREEN);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    EventBus.getDefault().unregister(this);
    }
}
