package me.gumenniy.arkadiy.vkmusic.app.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by Arkadiy on 06.04.2016.
 */
public class AsyncExecutor extends HandlerThread {

    private Handler workerHandler;

    public AsyncExecutor(String name) {
        super(name);
    }

    public void postTask(int what, Runnable workTask, boolean removeWhats, boolean postAtFront) {
        Log.e("handler", "postTask()");
        if (removeWhats) {
            workerHandler.removeMessages(what);
        }
        Message msg = workerHandler.obtainMessage(what, workTask);

        if (postAtFront) {
            workerHandler.sendMessageAtFrontOfQueue(msg);
        } else {
            workerHandler.sendMessage(msg);
        }
    }


    public void prepareHandler() {
        workerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.e("handler", "handleMessage() " + Thread.currentThread());
                Runnable task = (Runnable) msg.obj;
                task.run();
                return true;
            }
        });
    }
}
