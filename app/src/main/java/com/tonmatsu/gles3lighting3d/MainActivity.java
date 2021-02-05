package com.tonmatsu.gles3lighting3d;

import android.app.*;
import android.os.*;
import android.view.*;

public class MainActivity extends Activity {
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(surfaceView = new SurfaceView(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        hide();
    }

    private void hide() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}