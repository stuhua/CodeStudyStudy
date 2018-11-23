package stuhua.codestudystudy;

/**
 * Created by liulh on 2018/11/23.
 */

import android.support.annotation.NonNull;

import java.util.LinkedList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


/**
 * 预加载
 * preLoader = RxPreLoader.preLoad(observable);
 * preLoader.get(observer1);
 * preLoader.get(observer2);
 * preLoader.reload();
 * preLoader.destroy()
 *
 * @author billy.qi
 */
public class RxPreLoader<T> {
    private BehaviorSubject<T> subject;
    private Observable<T> observable;
    private Subscription subscription;
    private final LinkedList<Subscription> allObserver = new LinkedList<>();


    private RxPreLoader(Observable<T> observable) {
        //注意的是由于onCompleted也是数据流中的一个
        //如果直接observer.subscribeOn(Schedulers.io()).subscribe(subject);
        //会导致subject只能缓存onCompleted
        //所以此处新建一个OnSubscribe，通过调用subject.onNext(t)的方式来缓存数据
        this.observable = observable;
        subject = BehaviorSubject.create();
        subscription = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                performLoad();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(subject);
    }

    public static <R> RxPreLoader<R> preLoad(@NonNull Observable<R> observable) {
        return new RxPreLoader<R>(observable);
    }

    public void reload() {
        performLoad();
    }

    public Subscription get(Observer<T> observer) {
        Subscription subscription = subject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        allObserver.add(subscription);
        return subscription;
    }


    private void performLoad() {
        observable.subscribeOn(Schedulers.io())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if (subject != null) {
                            subject.onNext(t);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void destroy() {
        synchronized (allObserver) {
            while(!allObserver.isEmpty()) {
                unsubscribe(allObserver.removeFirst());
            }
        }
        unsubscribe(subscription);
        subscription = null;
        subject = null;
    }

    private void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
