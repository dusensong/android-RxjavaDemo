package cn.dss.rxjavademo;

import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.basic_usage_btn).setOnClickListener(view -> {
            testScheduler();
        });
    }

    /**
     * 测试线程调度
     */
    private void testScheduler() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(integer -> LogUtils.d("------>doOnNext1() " + Thread.currentThread().getName()))
                .map(integer -> {
                    LogUtils.d("------>map1() " + Thread.currentThread().getName());
                    return "s" + integer;
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtils.d("------>doOnSubscribe() " + Thread.currentThread().getName());
                    }
                })
                .doOnComplete(()->{
                    LogUtils.d("------>doOnComplete() " + Thread.currentThread().getName());
                })
                .doAfterNext(s -> {
                    LogUtils.d("------>doAfterNext() " + Thread.currentThread().getName());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(obj -> LogUtils.d("------>doOnNext2() " + Thread.currentThread().getName()))
                .observeOn(Schedulers.newThread())
                .flatMap(s -> {
                    LogUtils.d("------>flatMap1() " + Thread.currentThread().getName());
                    return Observable.just(s);
                })
                .observeOn(Schedulers.single())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("------>onError()" + e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("------>onCompleted()");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.d("------>onNext() " + Thread.currentThread().getName());
                        LogUtils.d("------>onNext:" + s);
                    }
                });
    }



    private void testDoOnNext() {
        Observable.just(1)
                .doOnNext(integer -> LogUtils.d("------>doOnNext() " + integer))
                .map(integer -> {
                    LogUtils.d("------>map() " + "s" + integer);
                    return "s" + integer;
                })
                .doOnNext(obj -> LogUtils.d("------>doOnNext() " + obj))
                .flatMap(s -> {
                    LogUtils.d("------>flatMap() " + "Flat" + s);
                    return Observable.just("Flat" + s);
                })
                .subscribe(new Observer<String>() {

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("------>onError()" + e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("------>onCompleted()");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.d("------>onNext:" + s);
                    }
                });
    }

    private void testSwitchMap() {
        Observable.just("A", "B", "C", "D", "E")
                .switchMap(new Function<String, Observable<String>>() {
                    @Override
                    public Observable<String> apply(String s) throws Exception {
                        return Observable.just(s).subscribeOn(Schedulers.newThread());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("------>onError()" + e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("------>onCompleted()");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.d("------>onNext:" + s);
                    }
                });
    }

    /**
     * 点赞
     */
    private void practiceDianZan() {
        Observable<Long> observable = Observable.intervalRange(1, 3, 0, 5, TimeUnit.SECONDS);
        observable.debounce(2, TimeUnit.SECONDS)
                .zipWith(observable.startWith(-1L), (last, current) -> {
                    Log.e("MA", last + "  " + current);
                    return last + "," + current;
                })
                .subscribe(s -> {
                    Log.e("MA", "onNext=" + s);
                });
    }

}
