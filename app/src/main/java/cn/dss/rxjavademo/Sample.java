package cn.dss.rxjavademo;

import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;

/**
 * Created by BG241996 on 2020/5/20.
 */
public class Sample {
    /**
     * 示例1：Rxjava基本使用
     */
    private void sample1() {
        Observable.create(
                (ObservableOnSubscribe<String>) e -> {
                    e.onNext("Hello");
                    e.onNext("World");
                    e.onNext("！！！");
                    e.onComplete();
                })
                .take(2)
                .map(s -> s.toUpperCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        LogUtils.d("------>onNext:" + s);
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                        LogUtils.d("------>onComplete:");
                    }
                });
    }


    /**
     * 示例2：Rxjava实现
     * 首先，请求网络获取Token
     * 然后，请求网络获取User
     * 然后，将User保存本地
     * 最后，显示User
     */
    private void sample2_1() {
        String userId = "1";
        getToken()
                .flatMap(token -> getUser(token, userId))
                .doOnNext(user -> saveUser(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> LogUtils.d("------>onNext:" + user),
                        throwable -> LogUtils.d("------>onError()" + throwable)
                );
    }

    /**
     * 示例2：传统实现
     */
    private void sample2_2() {
        String userId = "1";
        getToken(new Callback<String>() {
            @Override
            public void onNext(String token) {
                getUser(token, userId, new Callback<String>() {
                    @Override
                    public void onNext(String user) {
                        new Thread() {
                            @Override
                            public void run() {
                                saveUser(user);
                                runOnUiThread(() -> LogUtils.d("------>onNext:" + user));
                            }
                        }.start();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }
        });

    }

    /**
     * 复杂的线程切换
     */
    private void sample3() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .doOnNext(integer -> LogUtils.d("------>doOnNext1() " + Thread.currentThread().getName()))
                .map(integer -> {
                    LogUtils.d("------>map1() " + Thread.currentThread().getName());
                    return "s" + integer;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(obj -> LogUtils.d("------>doOnNext2() " + Thread.currentThread().getName()))
                .observeOn(Schedulers.newThread())
                .flatMap(s -> {
                    LogUtils.d("------>flatMap1() " + Thread.currentThread().getName());
                    return Observable.just(s);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> LogUtils.d("------>onNext() " + Thread.currentThread().getName()),
                        throwable -> LogUtils.d("------>onError()" + throwable)
                );
    }

    /**
     * 优化搜索提示
     * 需求：
     * 1.防止用户输入过快，触发过多网络请求，需要对输入事件做一下防抖动。
     * 2.用户在输入关键词过程中可能触发多次请求，那么，如果后一次请求的结果返回时，前一次请求的结果尚未返回的情况下，就应该取消前一次请求。
     */
    private void sample4() {
        RxTextView.textChanges(etSearch)
                .debounce(500, TimeUnit.MILLISECONDS)
                .switchMap(text -> queryKeyword(text.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                    // handle results
                });

        /*
        debounce：一定的时间内没有操作就会发送事件（只会发送最后一次操作的事件）
        switchMap 这个操作符与 flatMap 操作符类似，但是区别是如果原 Observable 中的两个元素，
            通过 switchMap 操作符都转为 Observable 之后，如果后一个元素对应的 Observable 发射元素时，
            前一个元素对应的 Observable 尚未发射完所有元素，那么前一个元素对应的 Observable 会被自动取消订阅
         */
    }

    private Observable<String> queryKeyword(String input) {
        // 模拟网络请求
        return Observable.just("查询结果").delay(3, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread());
    }

    public interface Callback<T> {
        void onNext(T data);

        void onError(Throwable e);
    }

    private Observable<String> getToken() {
        // 模拟网络请求
        return Observable.just("token");
    }

    private void getToken(Callback<String> callback) {
        // 模拟网络请求
    }

    private Observable<String> getUser(String token, String userId) {
        // 模拟网络请求
        return Observable.just("user");
    }

    private void getUser(String token, String userId, Callback<String> callback) {
        // 模拟网络请求
    }

    private void saveUser(String user) {
        // 将user保存到数据库
    }

    EditText etSearch;
}
