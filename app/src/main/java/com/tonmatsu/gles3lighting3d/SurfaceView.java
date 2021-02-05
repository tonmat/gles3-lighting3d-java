package com.tonmatsu.gles3lighting3d;

import android.content.*;
import android.opengl.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.*;

public class SurfaceView extends GLSurfaceView {
    private final Demo demo;

    public SurfaceView(Context context) {
        super(context);
        demo = new Demo(context.getApplicationContext());
        setEGLContextClientVersion(3);
        setRenderer(new Renderer() {
            private float delta;
            private long lastUpdateTime;

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                lastUpdateTime = System.nanoTime();
                demo.initialize();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                demo.resize(width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                final long updateTime = System.nanoTime();
                delta = 0.000000001f * (updateTime - lastUpdateTime);
                lastUpdateTime = updateTime;
                demo.update(delta);
            }
        });
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void dispose() {
        demo.dispose();
    }
}
