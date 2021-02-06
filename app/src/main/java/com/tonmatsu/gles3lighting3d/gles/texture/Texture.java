package com.tonmatsu.gles3lighting3d.gles.texture;

import static android.opengl.GLES31.*;

public class Texture {
    private static final Texture[] binded = new Texture[32];
    private static int activeTexture;
    private final int[] texture;
    private final TextureTemplate template;
    private int width;
    private int height;

    public Texture(TextureTemplate template, int width, int height) {
        this.template = template;
        this.width = width;
        this.height = height;
        glGenTextures(1, texture = new int[1], 0);
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, template.internalFormat, width, height, 0, template.format, template.type, null);
    }

    public int name() {
        return texture[0];
    }

    public void dispose() {
        glDeleteTextures(1, texture, 0);
    }

    private void setActiveTexture(int index) {
        if (activeTexture == index)
            return;
        glActiveTexture(index);
        activeTexture = index;
    }

    private void bind() {
        if (binded[activeTexture] == this)
            return;
        glBindTexture(GL_TEXTURE_2D, texture[0]);
        binded[activeTexture] = this;
    }

    public void bind(int index) {
        setActiveTexture(index);
        bind();
    }

    private void unbind() {
        if (binded[activeTexture] == null)
            return;
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        binded[activeTexture] = null;
    }

    public void unbind(int index) {
        setActiveTexture(index);
        unbind();
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height)
            return;
        this.width = width;
        this.height = height;
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, template.internalFormat, width, height, 0, template.format, template.type, null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
