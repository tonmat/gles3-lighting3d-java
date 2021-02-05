package com.tonmatsu.gles3lighting3d.gles.array;

import static android.opengl.GLES31.*;

public class VertexArray {
    private static VertexArray binded;
    private final int[] array;

    public VertexArray() {
        glGenVertexArrays(1, array = new int[1], 0);
        bind();
    }

    public void dispose() {
        glDeleteVertexArrays(1, array, 0);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindVertexArray(array[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindVertexArray(0);
        binded = null;
    }

    public void setLayout(VertexAttributeLayout... attributesLayout) {
        bind();

        int stride = 0;
        for (final VertexAttributeLayout attributeLayout : attributesLayout)
            for (final VertexAttribute attribute : attributeLayout.attributes)
                stride += attribute.bytes;

        int offset = 0;
        for (final VertexAttributeLayout attributeLayout : attributesLayout) {
            attributeLayout.buffer.bind();
            for (int i = 0; i < attributeLayout.attributes.length; i++) {
                final VertexAttribute attrib = attributeLayout.attributes[i];
                glVertexAttribPointer(i, attrib.size, attrib.type, false, stride, offset);
                glEnableVertexAttribArray(i);
                offset += attrib.bytes;
            }
            attributeLayout.buffer.unbind();
        }
    }
}
