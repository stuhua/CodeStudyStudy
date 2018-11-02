package stuhua.codestudystudy;

import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ShellUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.Okio;

/**
 * Created by liulh on 2018/11/2.
 */
public class TestUtils {
    public static void copyFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                countTime(new CallBack() {
                    @Override
                    public void doSometing() {
                        FileUtils.copyFile("/storage/38F1-1EF3/update.img", "/sdcard/update.img", new FileUtils.OnReplaceListener() {
                            @Override
                            public boolean onReplace() {
                                return true;
                            }
                        });
                    }
                });
            }
        }).start();

    }

    public static void copyFile1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                countTime(new CallBack() {
                    @Override
                    public void doSometing() {
                        try {
                            Okio.buffer(Okio.sink(new File("/sdcard/update.img"))).writeAll(Okio.buffer(Okio.source(new File("/storage/38F1-1EF3/update.img"))));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    public static void copyFile2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                countTime(new CallBack() {
                    @Override
                    public void doSometing() {
                        ShellUtils.execCmd("cp /storage/38F1-1EF3/update.img /sdcard/", false);
                    }
                });
            }
        }).start();

    }

    public interface CallBack {
        //执行回调操作的方法
        void doSometing();
    }

    public static void countTime(CallBack callBack) {
        long startTime = System.currentTimeMillis(); //起始时间
        callBack.doSometing(); ///进行回调操作
        long endTime = System.currentTimeMillis(); //结束时间
        Log.d("test", String.format("方法使用时间 %d ms", endTime - startTime)); //打印使用时间
    }

}
