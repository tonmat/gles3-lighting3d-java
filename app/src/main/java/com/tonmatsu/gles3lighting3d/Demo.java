package com.tonmatsu.gles3lighting3d;

import android.content.*;

import com.tonmatsu.gles3lighting3d.gles.array.*;
import com.tonmatsu.gles3lighting3d.gles.buffer.*;
import com.tonmatsu.gles3lighting3d.gles.shader.*;
import com.tonmatsu.gles3lighting3d.util.*;

import org.joml.*;

import java.nio.*;

import static android.opengl.GLES31.*;
import static com.tonmatsu.gles3lighting3d.util.BufferUtil.*;
import static org.joml.Math.*;

public class Demo {
    private final Context context;
    private int width;
    private int height;
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f model = new Matrix4f();
    private final Matrix4f modelview = new Matrix4f();
    private final Matrix3f normal = new Matrix3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f light = new Vector3f();
    private final Vector3f lightV = new Vector3f();
    private ShaderProgram demoShader;
    private ShaderProgram gaussianBlurShader;
    private ShaderProgram blendShader;

    private VertexArray vao;
    private IndexBuffer ibo;
    private VertexBuffer vbo;
    private IntBuffer indices;
    private FloatBuffer vertices;

    private VertexArray vao2;
    private IndexBuffer ibo2;
    private VertexBuffer vbo2;
    private IntBuffer indices2;
    private FloatBuffer vertices2;

    private PixelPackBuffer pbo;

    private float exposure;

    private int[] gbFramebuffers = new int[2];
    private int[] gbTextures = new int[2];

    private int hdrFB;
    private int colorTexture;
    private int bloomTexture;

    public Demo(Context context) {
        this.context = context;
    }

    public void initialize() {
        AssetUtil.initialize(context);

        light.set(0, 0, 1);

        view.lookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);

        indices = allocateIntBuffer(6 * 6)
                .put(0).put(1).put(2)
                .put(2).put(3).put(0)

                .put(4).put(5).put(6)
                .put(6).put(7).put(4)

                .put(8).put(9).put(10)
                .put(10).put(11).put(8)

                .put(12).put(13).put(14)
                .put(14).put(15).put(12)

                .put(16).put(17).put(18)
                .put(18).put(19).put(16)

                .put(20).put(21).put(22)
                .put(22).put(23).put(20);
        indices.flip();

        vertices = allocateFloatBuffer(6 * 4 * (3 + 3))
                .put(1).put(0).put(0)/**/.put(0).put(0).put(-1)
                .put(0).put(0).put(0)/**/.put(0).put(0).put(-1)
                .put(0).put(1).put(0)/**/.put(0).put(0).put(-1)
                .put(1).put(1).put(0)/**/.put(0).put(0).put(-1)

                .put(0).put(0).put(1)/**/.put(0).put(0).put(1)
                .put(1).put(0).put(1)/**/.put(0).put(0).put(1)
                .put(1).put(1).put(1)/**/.put(0).put(0).put(1)
                .put(0).put(1).put(1)/**/.put(0).put(0).put(1)

                .put(0).put(0).put(0)/**/.put(0).put(-1).put(0)
                .put(1).put(0).put(0)/**/.put(0).put(-1).put(0)
                .put(1).put(0).put(1)/**/.put(0).put(-1).put(0)
                .put(0).put(0).put(1)/**/.put(0).put(-1).put(0)

                .put(0).put(1).put(1)/**/.put(0).put(1).put(0)
                .put(1).put(1).put(1)/**/.put(0).put(1).put(0)
                .put(1).put(1).put(0)/**/.put(0).put(1).put(0)
                .put(0).put(1).put(0)/**/.put(0).put(1).put(0)

                .put(0).put(0).put(0)/**/.put(-1).put(0).put(0)
                .put(0).put(0).put(1)/**/.put(-1).put(0).put(0)
                .put(0).put(1).put(1)/**/.put(-1).put(0).put(0)
                .put(0).put(1).put(0)/**/.put(-1).put(0).put(0)

                .put(1).put(0).put(1)/**/.put(1).put(0).put(0)
                .put(1).put(0).put(0)/**/.put(1).put(0).put(0)
                .put(1).put(1).put(0)/**/.put(1).put(0).put(0)
                .put(1).put(1).put(1)/**/.put(1).put(0).put(0);
        vertices.flip();

        indices2 = allocateIntBuffer(6)
                .put(0).put(1).put(2)
                .put(2).put(3).put(0);
        indices2.flip();

        vertices2 = allocateFloatBuffer(4 * (2 + 2))
                .put(-1).put(-1)/**/.put(0).put(1)
                .put(+1).put(-1)/**/.put(1).put(1)
                .put(+1).put(+1)/**/.put(1).put(0)
                .put(-1).put(+1)/**/.put(0).put(0);
        vertices2.flip();

        demoShader = new ShaderProgram();
        demoShader.attachShader(ShaderType.VERTEX, "shaders/demo-vs.glsl");
        demoShader.attachShader(ShaderType.FRAGMENT, "shaders/demo-fs.glsl");
        demoShader.link();

        gaussianBlurShader = new ShaderProgram();
        gaussianBlurShader.attachShader(ShaderType.VERTEX, "shaders/gaussian-blur-vs.glsl");
        gaussianBlurShader.attachShader(ShaderType.FRAGMENT, "shaders/gaussian-blur-fs.glsl");
        gaussianBlurShader.link();

        blendShader = new ShaderProgram();
        blendShader.attachShader(ShaderType.VERTEX, "shaders/blend-vs.glsl");
        blendShader.attachShader(ShaderType.FRAGMENT, "shaders/blend-fs.glsl");
        blendShader.link();

        vao = new VertexArray();
        ibo = new IndexBuffer(indices.capacity() * 4, BufferUsage.STATIC);
        ibo.update(indices);
        vbo = new VertexBuffer(vertices.capacity() * 4, BufferUsage.STATIC);
        vbo.update(vertices);
        vao.setLayout(new VertexAttributeLayout(vbo, VertexAttribute.VEC3, VertexAttribute.VEC3));
        vao.unbind();
        ibo.unbind();
        vbo.unbind();

        vao2 = new VertexArray();
        ibo2 = new IndexBuffer(indices2.capacity() * 4, BufferUsage.STATIC);
        ibo2.update(indices2);
        vbo2 = new VertexBuffer(vertices2.capacity() * 4, BufferUsage.STATIC);
        vbo2.update(vertices2);
        vao2.setLayout(new VertexAttributeLayout(vbo2, VertexAttribute.VEC2, VertexAttribute.VEC2));
        vao2.unbind();
        ibo2.unbind();
        vbo2.unbind();

        pbo = new PixelPackBuffer(16);
        pbo.unbind();

        for (int i = 0; i < 2; i++) {
            gbFramebuffers[i] = createFramebuffer();
            gbTextures[i] = createTexture();
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbTextures[i], 0);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);

        hdrFB = createFramebuffer();
        colorTexture = createTexture();
        bloomTexture = createTexture();
        glDrawBuffers(2, new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1}, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, bloomTexture, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);

        glEnable(GL_CULL_FACE);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);
        projection.setPerspective(1.2f, (float) width / height, 0.001f, 1000);
        for (int i = 0; i < 2; i++)
            resizeTexture(gbTextures[i], width, height);
        resizeTexture(colorTexture, width, height);
        resizeTexture(bloomTexture, width, height);
    }

    public void update(float delta) {
        // RENDER 1

        rotation.x += delta * 0.11f;
        rotation.y += delta * 0.77f;
        rotation.z += delta * 0.33f;

        glBindFramebuffer(GL_FRAMEBUFFER, hdrFB);
        glClear(GL_COLOR_BUFFER_BIT);

        demoShader.bind();
        vao.bind();
        demoShader.setUniformMatrix4f("u_projection", projection);
        demoShader.setUniform3f("u_light", light.mulProject(view, lightV));

        {
            model.identity().scale(5, 10, 1).translate(0, 0, -5f).translate(-0.5f, -0.5f, -0.5f);
            modelview.set(view).mul(model);
            demoShader.setUniformMatrix4f("u_modelview", modelview);
            demoShader.setUniformMatrix3f("u_normal", modelview.normal(normal));
            demoShader.setUniform3f("u_color", 0.01f, 0.01f, 0.01f);
            glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        }

        {
            model.identity().rotateXYZ(rotation).scale(0.2f).translate(-0.5f, -0.5f, -0.5f);
            modelview.set(view).mul(model);
            demoShader.setUniformMatrix4f("u_modelview", modelview);
            demoShader.setUniformMatrix3f("u_normal", modelview.normal(normal));
            demoShader.setUniform3f("u_color", 0, 1, 0);
            glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        }

        {
            model.identity().translate(0, 0.4f, 0).rotateYXZ(rotation).scale(0.1f).translate(-0.5f, -0.5f, -0.5f);
            modelview.set(view).mul(model);
            demoShader.setUniformMatrix4f("u_modelview", modelview);
            demoShader.setUniformMatrix3f("u_normal", modelview.normal(normal));
            demoShader.setUniform3f("u_color", 1, 0, 0);
            glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        }

        {
            model.identity().translate(0, -0.4f, 0).rotateZYX(rotation).scale(0.1f).translate(-0.5f, -0.5f, -0.5f);
            modelview.set(view).mul(model);
            demoShader.setUniformMatrix4f("u_modelview", modelview);
            demoShader.setUniformMatrix3f("u_normal", modelview.normal(normal));
            demoShader.setUniform3f("u_color", 0, 0, 1);
            glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        }

        vao.unbind();
        demoShader.unbind();

        //

        pbo.bind();
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glReadPixels(width / 2, height / 2, 1, 1, GL_RGBA, GL_FLOAT, 0);
        final FloatBuffer pixels = ((ByteBuffer) glMapBufferRange(GL_PIXEL_PACK_BUFFER, 0, 16, GL_MAP_READ_BIT))
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        final float r = pixels.get();
        final float g = pixels.get();
        final float b = pixels.get();
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);
        pbo.unbind();

        final float brightness = r * 0.2125f + g * 0.7154f + b * 0.0721f;
        final float newExposure = 1.0f / max(brightness, 0.3f);
        exposure = exposure + (newExposure - exposure) * delta * 10;
        pbo.unbind();

        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);

        // BLOOM

        gaussianBlurShader.bind();
        vao2.bind();
        ibo2.bind();
        for (int i = 0; i < 10; i++) {
            final int horizontal = i % 2;
            glBindFramebuffer(GL_FRAMEBUFFER, gbFramebuffers[horizontal]);
            gaussianBlurShader.setUniform1i("u_horizontal", horizontal);
            glBindTexture(GL_TEXTURE_2D, i == 0 ? bloomTexture : gbTextures[1 - horizontal]);
            glDrawElements(GL_TRIANGLES, indices2.limit(), GL_UNSIGNED_INT, 0);
        }
        ibo2.unbind();
        vbo2.unbind();
        gaussianBlurShader.unbind();
        glBindTexture(GL_TEXTURE_2D, GL_NONE);

        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);

        // RENDER 2

        glClear(GL_COLOR_BUFFER_BIT);

        glBindTexture(GL_TEXTURE_2D, colorTexture);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gbTextures[1]);
        blendShader.bind();
        blendShader.setUniform1i("u_color", 0);
        blendShader.setUniform1i("u_bloom", 1);
        blendShader.setUniform1f("u_exposure", exposure);
        vao2.bind();
        ibo2.bind();
        glDrawElements(GL_TRIANGLES, indices2.limit(), GL_UNSIGNED_INT, 0);
        ibo2.unbind();
        vbo2.unbind();
        blendShader.unbind();
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
    }

    public void dispose() {
        demoShader.dispose();
        blendShader.dispose();
        vao.dispose();
        ibo.dispose();
        vbo.dispose();
        vao2.dispose();
        ibo2.dispose();
        vbo2.dispose();
        glDeleteFramebuffers(gbFramebuffers.length, gbFramebuffers, 0);
        glDeleteTextures(gbTextures.length, gbTextures, 0);
        glDeleteFramebuffers(1, new int[]{hdrFB}, 0);
        glDeleteTextures(1, new int[]{colorTexture}, 0);
        glDeleteTextures(1, new int[]{bloomTexture}, 0);
        AssetUtil.dispose();
    }

    private int createFramebuffer() {
        final int[] framebuffer = new int[1];
        glGenFramebuffers(1, framebuffer, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer[0]);
        return framebuffer[0];
    }

    private int createTexture() {
        final int[] texture = new int[1];
        glGenTextures(1, texture, 0);
        glBindTexture(GL_TEXTURE_2D, texture[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        return texture[0];
    }

    private void resizeTexture(int texture, int width, int height) {
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, null);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
    }
}
