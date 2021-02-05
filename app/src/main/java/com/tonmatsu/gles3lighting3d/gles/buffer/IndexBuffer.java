package com.tonmatsu.gles3lighting3d.gles.buffer;

import java.nio.*;

import static android.opengl.GLES31.*;

public class IndexBuffer {
    private static IndexBuffer binded;
    private final int[] buffer;

    public IndexBuffer(int size, BufferUsage usage) {
        glGenBuffers(1, buffer = new int[1], 0);
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, null, usage.usage);
    }

    public void dispose() {
        glDeleteBuffers(1, buffer, 0);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        binded = null;
    }

    public void update(ByteBuffer data) {
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data.limit(), data);
    }

    public void update(IntBuffer data) {
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data.limit() * 4, data);
    }
}
