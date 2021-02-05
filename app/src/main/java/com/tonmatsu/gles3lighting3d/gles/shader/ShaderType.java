package com.tonmatsu.gles3lighting3d.gles.shader;

import static android.opengl.GLES31.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER);

    public final int type;

    ShaderType(int type) {
        this.type = type;
    }
}
