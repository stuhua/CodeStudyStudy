package stuhua.codestudystudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import okio.AsyncTimeout;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity {
    private BufferedSink mSink;
    private BufferedSource mSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("10.0.2.2", 12306);
                    Log.d("MainActivity","start conn");
                     mSink = Okio.buffer(Okio.sink(socket));
                     mSource = Okio.buffer(Okio.source(socket));

//                    mSource.timeout().timeout(10000L, TimeUnit.MILLISECONDS);

                /*    String sendMsg = "成功连接服务器" + "(服务器发送)";
                    mSink.writeUtf8(sendMsg + "\n");
                    mSink.flush();*/
                    AsyncTimeout timeout = new AsyncTimeout();
                    timeout.timeout(1000L, TimeUnit.MILLISECONDS);
                    timeout.enter();

                    String read = mSource.readUtf8();
                    Log.d("MainActivity", "read = " + read);
                    timeout.exit(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("MainActivity", "e = " + e);
                }
            }
        }).start();

    }
}
