package com.tonmatsu.gles3lighting3d.gles.array;

import static android.opengl.GLES20.*;

public enum VertexAttribute {
    FLOAT(1, GL_FLOAT, 4),
    VEC2(2, GL_FLOAT, 8),
    VEC3(3, GL_FLOAT, 12),
    VEC4(4, GL_FLOAT, 16);

    final int size;
    final int type;
    final int bytes;

    VertexAttribute(int size, int type, int bytes) {
        this.size = size;
        this.type = type;
        this.bytes = bytes;
    }
}
