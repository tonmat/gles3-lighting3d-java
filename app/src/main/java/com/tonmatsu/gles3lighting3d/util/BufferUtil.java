package com.tonmatsu.gles3lighting3d.util;

import java.nio.*;

public final class BufferUtil {
    private BufferUtil() {
    }

    public static ByteBuffer allocateByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer allocateIntBuffer(int capacity) {
        return allocateByteBuffer(capacity * 4).asIntBuffer();
    }

    public static FloatBuffer allocateFloatBuffer(int capacity) {
        return allocateByteBuffer(capacity * 4).asFloatBuffer();
    }
}
