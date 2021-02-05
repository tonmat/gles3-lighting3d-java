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

public class Demo {
    private final Context context;
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f model = new Matrix4f();
    private final Matrix4f modelview = new Matrix4f();
    private final Matrix3f normal = new Matrix3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f light = new Vector3f();
    private final Vector3f lightV = new Vector3f();
    private ShaderProgram shaderProgram;
    private VertexArray vao;
    private IndexBuffer ibo;
    private VertexBuffer vbo;
    private IntBuffer indices;
    private FloatBuffer vertices;

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

        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(ShaderType.VERTEX, "shaders/demo-vs.glsl");
        shaderProgram.attachShader(ShaderType.FRAGMENT, "shaders/demo-fs.glsl");
        shaderProgram.link();

        vao = new VertexArray();
        ibo = new IndexBuffer(indices.capacity() * 4, BufferUsage.STATIC);
        ibo.update(indices);
        vbo = new VertexBuffer(vertices.capacity() * 4, BufferUsage.STATIC);
        vbo.update(vertices);
        vao.setLayout(new VertexAttributeLayout(vbo, VertexAttribute.VEC3, VertexAttribute.VEC3));
        vao.unbind();
        ibo.unbind();
        vbo.unbind();

        glClearColor(0.1f, 0.12f, 0.14f, 1);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
        projection.setPerspective(1.2f, (float) width / height, 0.001f, 1000);
    }

    public void update(float delta) {
        rotation.x += delta * 0.11f;
        rotation.y += delta * 0.77f;
        rotation.z += delta * 0.33f;
        model.identity().rotateXYZ(rotation).scale(0.25f).translate(-0.5f, -0.5f, -0.5f);

        modelview.set(view).mul(model);

        shaderProgram.setUniformMatrix4f("u_projection", projection);
        shaderProgram.setUniformMatrix4f("u_modelview", modelview);
        shaderProgram.setUniformMatrix3f("u_normal", modelview.normal(normal));
        shaderProgram.setUniform3f("u_light", light.mulProject(view, lightV));

        // RENDER

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shaderProgram.bind();
        vao.bind();
        ibo.bind();
        glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        ibo.unbind();
        vao.unbind();
        shaderProgram.unbind();
    }

    public void dispose() {
        shaderProgram.dispose();
        vao.dispose();
        ibo.dispose();
        vbo.dispose();
        AssetUtil.dispose();
    }
}
