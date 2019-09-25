package iqiqiya.lanlana.handlerprojecttest3;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Author: iqiqiya
 * Date: 2019/9/25
 * Time: 12:40
 * Blog: blog.77sec.cn
 * Github: github.com/iqiqiya
 */
public class DiglettActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private TextView mResultTextView;
    private ImageView mDiglettImageView;
    private Button mStartButton;

    public static final int CODE = 123;

    // 地鼠坐标
    private int[][] mPosition = new int[][]{
            {111, 180}, {432, 880},
            {521, 256}, {429, 780},
            {456, 976}, {145, 665},
            {123, 678}, {564, 567},
            {888, 222}, {236, 459},
            {444, 333}, {222, 222},
            {222, 888}, {999, 111}
    };

    // 总共出现的次数
    private int mTotalCount;
    private int mSuccessCount;

    public static final int MAX_COUNT = 120;

    private DiglettHandler mhandler = new DiglettHandler(this);
    private Button headsetplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);// 初始化在加载布局的上边

        setContentView(R.layout.activity_diglett);

       // 加载滑稽.gif
        SimpleDraweeView huaji_img = this.findViewById(R.id.huaji_img);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                // 设置uri,加载本地的huaji.gif
                .setUri(Uri.parse("res://"+getPackageName()+"/"+R.drawable.huaji))
                .build();

        // 设置Controller
        huaji_img.setController(mDraweeController);

        initView();
        setTitle("快来打地鼠");
    }

    private void initView() {
        mResultTextView = findViewById(R.id.text_view);
        mDiglettImageView = findViewById(R.id.image_view);
        mStartButton = findViewById(R.id.start_button);
        mStartButton.setOnClickListener(this);
        mDiglettImageView.setOnTouchListener(this);

        headsetplay = findViewById(R.id.headsetplay);

        headsetplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playLocalFile();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                start();
                break;
        }
    }

    private void start() {
        // 发送消息
        mResultTextView.setText("开始啦");
        mStartButton.setText("游戏中……");
        mStartButton.setEnabled(false);
        next(0);
    }

    private void next(int delayTime) {
        // 下一次出现地鼠
        int position = new Random().nextInt(mPosition.length);

        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = position;

        mhandler.sendMessageDelayed(message,delayTime);
        mTotalCount ++;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.setVisibility(View.GONE);
        mSuccessCount ++;
        mResultTextView.setText("共"+MAX_COUNT+"只,"+"打到了"+mSuccessCount+"只.");
        if (mSuccessCount == MAX_COUNT) {
            Toast.makeText(DiglettActivity.this,"哇塞,你居然拿了满分哎！",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static class DiglettHandler extends Handler{
        public final WeakReference<DiglettActivity> mWeakReference;
        public static final int RANDOM_NUMBER = 500;

        public DiglettHandler(DiglettActivity activity){
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            DiglettActivity activity = mWeakReference.get();

            switch (msg.what){
                case CODE:
                    if (activity.mTotalCount > MAX_COUNT){
                        activity.clear();
                        Toast.makeText(activity,"没有地鼠了,游戏结束",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int position = msg.arg1;
                    activity.mDiglettImageView.setX(activity.mPosition[position][0]);
                    activity.mDiglettImageView.setY(activity.mPosition[position][1]);
                    activity.mDiglettImageView.setVisibility(View.VISIBLE);

                    int randomTime = new Random().nextInt(200) + RANDOM_NUMBER;

                    activity.next(randomTime);
                    break;
            }

        }
    }

    private void clear() {
        mTotalCount = 0;
        mSuccessCount = 0;
        mDiglettImageView.setVisibility(View.GONE);
        mStartButton.setText("点击开始");
        mStartButton.setEnabled(true);
    }

    private MediaPlayer mMediaPlayer;
    private void playLocalFile() {
    mMediaPlayer = MediaPlayer.create(this,R.raw.backmusic);

    //播放工程res目录下的raw目录中的音乐文件in_call_alarm
    try {
        mMediaPlayer.prepare();
    } catch (IllegalStateException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    mMediaPlayer.start();
    // headsetplay.setEnabled(false);
    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            //播完了接着播或者关闭mMediaPlayer
            mMediaPlayer.stop();
        }
        });
    }
}
