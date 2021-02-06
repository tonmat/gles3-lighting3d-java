package com.tonmatsu.gles3lighting3d.gles.buffer;

import java.nio.*;

import static android.opengl.GLES31.*;

public class PixelPackBuffer {
    private static PixelPackBuffer binded;
    private final int[] buffer;

    public PixelPackBuffer(int size) {
        glGenBuffers(1, buffer = new int[1], 0);
        bind();
        glBufferData(GL_PIXEL_PACK_BUFFER, size, null, GL_DYNAMIC_READ);
    }

    public void dispose() {
        glDeleteBuffers(1, buffer, 0);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_PIXEL_PACK_BUFFER, buffer[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_PIXEL_PACK_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(ByteBuffer data) {
        bind();
        glBufferSubData(GL_PIXEL_PACK_BUFFER, 0, data.limit(), data);
    }

    public void update(FloatBuffer data) {
        bind();
        glBufferSubData(GL_PIXEL_PACK_BUFFER, 0, data.limit() * 4, data);
    }
}
