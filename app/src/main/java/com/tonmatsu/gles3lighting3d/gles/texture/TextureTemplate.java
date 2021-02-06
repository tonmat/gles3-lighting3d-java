package com.tonmatsu.gles3lighting3d.gles.texture;

import static android.opengl.GLES31.*;

public enum TextureTemplate {
    RGBA(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
    RGBA16F(GL_RGBA16F, GL_RGBA, GL_FLOAT);

    final int internalFormat;
    final int format;
    final int type;

    TextureTemplate(int internalFormat, int format, int type) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }
}
