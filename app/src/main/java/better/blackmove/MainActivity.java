package better.blackmove;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements GPSSpeedTracker.SpeedListener {

    static int intSecs = 361;
    String strSecs;
    private Chronometer chronometerCountDown;
    private GPSSpeedTracker gpsSpeedTracker;
    TextView speedTextView;
    int nowSpeed = 0;
    int backSpeed = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        chronometerCountDown = findViewById(R.id.chronometerCountDown);

        final ImageButton vUp = findViewById(R.id.add_time_up);
        vUp.setOnClickListener(v -> {
            intSecs += 61;
            strSecs = intSecs + "";
            chronometerCountDown.setText(strSecs);
        });
        speedTextView = findViewById(R.id.textSpeed);
        speedTextView.setText(""+backSpeed);

        final ImageButton vReturn = findViewById(R.id.return2box);
        vReturn.setOnClickListener(v -> return2BlackCam());

        final ImageButton vExit = findViewById(R.id.exit_app);
        vExit.setOnClickListener(v -> exit_app());
        Degree.start(getApplicationContext());

        final ImageButton sUp = findViewById(R.id.speed_up);
        sUp.setOnClickListener(v -> {
            backSpeed += 10;
            speedTextView.setText(""+backSpeed);
        });

        final ImageButton sDn = findViewById(R.id.speed_down);
        sDn.setOnClickListener(v -> {
            backSpeed -= 10;
            speedTextView.setText(""+backSpeed);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        strSecs = intSecs + "";
        chronometerCountDown.setText(strSecs);
        chronometerCountDown.start();
        chronometerCountDown.setOnChronometerTickListener(chronometer -> onChronometerTickHandler());
        showDegree();
        gpsSpeedTracker = new GPSSpeedTracker(this);
        gpsSpeedTracker.setSpeedListener(this);
        gpsSpeedTracker.startTracking();
    }

    private void onChronometerTickHandler()  {
        if(intSecs < 1) {
            return2BlackCam();
        }
        strSecs = intSecs + "";
        chronometerCountDown.setText(strSecs);
        intSecs--;
        if (intSecs % 20 == 0) {
            showDegree();
        }
    }

    private void showDegree() {
        TextView tv = findViewById(R.id.temperature);
        int degree = Degree.get();
        tv.setText(degree+"");
        tv.setTextColor((degree<37)? Color.WHITE:((degree<42)? Color.YELLOW:Color.RED));
    }

    void return2BlackCam() {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(() -> {
            Intent sendIntent = this.getPackageManager().getLaunchIntentForPackage("better.blackcam");
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sendIntent);
            System.exit(0);
            Process.killProcess(Process.myPid());
        }, 1000);
    }

    void exit_app() {
        gpsSpeedTracker.stopTracking();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    long keyOldTime = 0, keyNowTime = 0;
    @Override
    public boolean onKeyDown(final int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            intSecs += 61;
            strSecs = intSecs + "";
            chronometerCountDown.setText(strSecs);
            keyNowTime = System.currentTimeMillis();
            if (keyOldTime == 0) {  // first Time
                keyOldTime = keyNowTime;
            } else if (keyNowTime - keyOldTime < 2000 && keyNowTime - keyOldTime > 300) {
                return2BlackCam();
            } else
                keyOldTime = 0;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSpeedUpdated(int speed) {
        if (speed != nowSpeed) {
            nowSpeed = speed;
            if (nowSpeed > backSpeed)
                return2BlackCam();
        }
    }
}