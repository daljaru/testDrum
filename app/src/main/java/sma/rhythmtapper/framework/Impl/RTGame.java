package sma.rhythmtapper.framework.Impl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import sma.rhythmtapper.framework.Audio;
import sma.rhythmtapper.framework.FileIO;
import sma.rhythmtapper.framework.Game;
import sma.rhythmtapper.framework.Graphics;
import sma.rhythmtapper.framework.Input;
import sma.rhythmtapper.framework.Screen;

//from cj
//broadcastReceiver추가
//from cj
public class RTGame extends Activity implements Game {
    RTFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;

    //from cj
    public String drumPadNumber;
    //from cj

    FileIO fileIO;
    Screen screen;
    PowerManager.WakeLock wakeLock;

    @Override
    public void goToActivity(Class<?> activity) {
        Intent i = new Intent(this, activity);
        // add flag, when activity already runs,
        // use it instead of launching a new instance
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //*from cj
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("DrumHitNumber"));
        //from cj

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int frameBufferWidth = 1080;
        int frameBufferHeight = 1920;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Bitmap.Config.RGB_565);

        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new RTFastRenderView(this, frameBuffer);
        graphics = new RTGraphics(getAssets(), frameBuffer);
        fileIO = new RTFileIO(this);
        audio = new RTAudio(this);
        input = new RTInput(this, renderView, scaleX, scaleY);

        screen = getInitScreen();
        setContentView(renderView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "RhythmTapper");
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }


    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Vibrator getVibrator() {
        return (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public Screen getInitScreen() {
        return null;
    }

    public String getPadNumber(){
        return drumPadNumber;
    }

    public void setPadNumber(String padNumber){
        drumPadNumber = padNumber;
    }

    //*from cj
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drumPadNumber= intent.getStringExtra("DrumPadNumber");
            Toast.makeText(getApplicationContext(), drumPadNumber, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
    //from cj
}
