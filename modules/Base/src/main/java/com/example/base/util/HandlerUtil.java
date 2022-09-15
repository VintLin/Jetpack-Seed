package com.example.base.util;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author hzy 2020/12/11
 * 公用Handler工具类
 */
public class HandlerUtil {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final Map<String, List<WeakReference<Runnable>>> runnableMap = new HashMap<>();
    private static final EnqueueHandler enqueueHandler = new EnqueueHandler();
    private static final Map<String,EnqueueHandler> enqueueHandlerMap = new HashMap<>();

    /**
     * 运行在主线程 （UI线程）
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable){
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    /**
     * Post 到主线程最后执行
     * @param runnable
     */
    public static void post(Runnable runnable){
        mHandler.post(runnable);
    }

    /**
     * 延迟执行
     * @param runnable
     * @param delay 单位毫秒
     */
    public static void postTaskDelay(Runnable runnable,long delay){
        mHandler.postDelayed(runnable, delay);
    }

    /**
     * 延迟执行
     * @param runnable
     * @param delay 单位毫秒
     * @param tag 标签         取消的时候 可以根据标签取消全部
     */
    public static void postTaskDelay(Runnable runnable,long delay,String tag){
        if(runnable == null){
            return;
        }
        if(!TextUtils.isEmpty(tag)){
            List<WeakReference<Runnable>> referenceList = runnableMap.get(tag);
            if(referenceList == null){
                synchronized (HandlerUtil.class) {
                    referenceList = runnableMap.get(tag);
                    if(referenceList == null) {
                        referenceList = new ArrayList<>();
                        runnableMap.put(tag, referenceList);
                    }
                }
            }
            referenceList.add(new WeakReference<Runnable>(runnable));
        }
        mHandler.postDelayed(runnable, delay);
    }

    /**
     * Post 到主线程顺序执行，减少主线程 MessageQueue的消息数量 （降低ANR）
     * @param runnable
     */
    public static void postEnqueue(Runnable runnable){
        enqueueHandler.post(runnable);
    }

    public static void cancel(String tag){
        if(TextUtils.isEmpty(tag)){
            return;
        }
        List<WeakReference<Runnable>> referenceList = runnableMap.remove(tag);
        if(referenceList == null || referenceList.size() == 0){
            return;
        }
        for(WeakReference<Runnable> reference : referenceList){
            if(reference == null){
                continue;
            }
            Runnable runnable = reference.get();
            if(runnable == null){
                continue;
            }
            try {
                cancel(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void cancel(Runnable runnable){
        if(runnable == null){
            return;
        }
        try {
            mHandler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EnqueueHandler getEnqueueHandler(String tag){
        if(TextUtils.isEmpty(tag)){
            return enqueueHandler;
        }
        EnqueueHandler result = enqueueHandlerMap.get(tag);
        if(result == null){
            synchronized (EnqueueHandler.class){
                if(result != null){
                    return result;
                }
                result = new EnqueueHandler();
                enqueueHandlerMap.put(tag,result);
            }
        }
        return result;
    }

    public static class EnqueueHandler implements Runnable{
        CopyOnWriteArrayList<Runnable> runnableList = new CopyOnWriteArrayList<>();
        boolean isRunning = false;


        public void post(Runnable runnable){
            if(runnable == null){
                return;
            }
            runnableList.add(runnable);
            checkList();
        }

        public void remove(Runnable runnable){
            runnableList.remove(runnable);
        }

        public void clear(){
            runnableList.clear();
        }


        @Override
        public void run() {
            if(runnableList == null || runnableList.size() == 0){
                isRunning = false;
                checkList();
                return;
            }
            try {
                Runnable runnable = runnableList.remove(0);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            isRunning = false;
            checkList();
        }

        private void checkList(){
            if(isRunning){
                return;
            }
            synchronized (EnqueueHandler.class){
                if(isRunning){
                    return;
                }
                if(runnableList.size() > 0){
                    isRunning = true;
                    HandlerUtil.post(EnqueueHandler.this);
                }
            }
        }
    }

    private static class EnqueueRunnable{
        public String tag;
        public Runnable runnable;

        public EnqueueRunnable(String tag,Runnable runnable){
            this.tag = tag;
            this.runnable = runnable;
        }

        public EnqueueRunnable(Runnable runnable){
            this.runnable = runnable;
        }
    }
}
