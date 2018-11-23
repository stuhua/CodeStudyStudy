package stuhua.codestudystudy;

/**
 * Created by liulh on 2018/11/23.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;


/**
 * 使用RxJava实现的预加载方式
 */
public class RxPreLoaderActivity extends AppCompatActivity {

    private TextView textView;
    private RxPreLoader<String> preLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preLoad();//启动预加载
        initLayout(savedInstanceState);
        preLoader.get(observer);//展示预加载的数据
    }

    //初始化布局
    private void initLayout(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("使用RxPreLoader");
        //通过循环多次findById来模拟复杂页面布局初始化的耗时
        textView = (TextView)findViewById(R.id.textView);
    }

    //展示预加载的数据
    Subscriber<String> observer = new Subscriber<String>() {
        @Override public void onCompleted() { }

        @Override public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(String s) {
            textView.setText(s);
        }
    };

    private void preLoad() {
        preLoader = RxPreLoader.preLoad(Observable.just("result").delay(500, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preLoader.destroy();//销毁
    }
}
