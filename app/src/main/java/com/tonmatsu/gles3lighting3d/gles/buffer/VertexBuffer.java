package com.tonmatsu.gles3lighting3d.gles.buffer;

import java.nio.*;

import static android.opengl.GLES31.*;

public class VertexBuffer {
    private static VertexBuffer binded;
    private final int[] buffer;

    public VertexBuffer(int size, BufferUsage usage) {
        glGenBuffers(1, buffer = new int[1], 0);
        bind();
        glBufferData(GL_ARRAY_BUFFER, size, null, usage.usage);
    }

    public void dispose() {
        glDeleteBuffers(1, buffer, 0);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, buffer[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(ByteBuffer data) {
        bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, data.limit(), data);
    }

    public void update(FloatBuffer data) {
        bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, data.limit() * 4, data);
    }
}
