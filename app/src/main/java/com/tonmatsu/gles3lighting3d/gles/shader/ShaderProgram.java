package com.tonmatsu.gles3lighting3d.gles.shader;

import com.tonmatsu.gles3lighting3d.util.*;

import org.joml.*;

import java.util.*;
import java.util.logging.*;

import static android.opengl.GLES31.*;

public class ShaderProgram {
    private static final Logger LOGGER = Logger.getLogger("ShaderProgram");
    private static ShaderProgram binded;
    private final int program;
    private final ArrayList<Integer> shaders;
    private final HashMap<String, Integer> uniformsLocations;
    private final float[] temp = new float[16];

    public ShaderProgram() {
        program = glCreateProgram();
        shaders = new ArrayList<>();
        uniformsLocations = new HashMap<>();
    }

    public void dispose() {
        glDeleteProgram(program);
    }

    public void bind() {
        if (binded == this)
            return;
        glUseProgram(program);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glUseProgram(GL_NONE);
        binded = null;
    }

    public void attachShader(ShaderType type, String asset) {
        final String source = AssetUtil.getString(asset);
        final int shader = glCreateShader(type.type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        final int[] status = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            LOGGER.log(Level.SEVERE, "" +
                    "could not compile shader\n" +
                    source + "\n" +
                    glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return;
        }
        glAttachShader(program, shader);
        shaders.add(shader);
    }

    public void link() {
        glLinkProgram(program);
        for (final int shader : shaders)
            glDeleteShader(shader);
        final int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            LOGGER.log(Level.SEVERE, "" +
                    "could not link program\n" +
                    glGetProgramInfoLog(program));
            glDeleteProgram(program);
            return;
        }
        shaders.clear();
    }

    private int getUniformLocation(String name) {
        Integer location = uniformsLocations.get(name);
        if (location == null) {
            location = glGetUniformLocation(program, name);
            if (location < 0)
                LOGGER.log(Level.WARNING, "could not get uniform location\n" + name);
            uniformsLocations.put(name, location);
        }
        return location;
    }

    public void setUniform1b(String name, boolean x) {
        bind();
        glUniform1i(getUniformLocation(name), x ? GL_TRUE : GL_FALSE);
    }

    public void setUniform1i(String name, int x) {
        bind();
        glUniform1i(getUniformLocation(name), x);
    }

    public void setUniform1f(String name, float x) {
        bind();
        glUniform1f(getUniformLocation(name), x);
    }

    public void setUniform2f(String name, float x, float y) {
        bind();
        glUniform2f(getUniformLocation(name), x, y);
    }

    public void setUniform3f(String name, float x, float y, float z) {
        bind();
        glUniform3f(getUniformLocation(name), x, y, z);
    }

    public void setUniform4f(String name, float x, float y, float z, float w) {
        bind();
        glUniform4f(getUniformLocation(name), x, y, z, w);
    }

    public void setUniform2f(String name, Vector2f v) {
        bind();
        glUniform2f(getUniformLocation(name), v.x, v.y);
    }

    public void setUniform3f(String name, Vector3f v) {
        bind();
        glUniform3f(getUniformLocation(name), v.x, v.y, v.z);
    }

    public void setUniform4f(String name, Vector4f v) {
        bind();
        glUniform4f(getUniformLocation(name), v.x, v.y, v.z, v.w);
    }

    public void setUniformMatrix3f(String name, Matrix3f m) {
        bind();
        glUniformMatrix3fv(getUniformLocation(name), 1, false, m.get(temp), 0);
    }

    public void setUniformMatrix4f(String name, Matrix4f m) {
        bind();
        glUniformMatrix4fv(getUniformLocation(name), 1, false, m.get(temp), 0);
    }
}
