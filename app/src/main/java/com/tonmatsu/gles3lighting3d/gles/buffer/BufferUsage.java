package com.tonmatsu.gles3lighting3d.gles.buffer;

import static android.opengl.GLES31.*;

public enum BufferUsage {
    STATIC(GL_STATIC_DRAW),
    DYNAMIC(GL_DYNAMIC_DRAW);

    public final int usage;

    BufferUsage(int usage) {
        this.usage = usage;
    }
}
