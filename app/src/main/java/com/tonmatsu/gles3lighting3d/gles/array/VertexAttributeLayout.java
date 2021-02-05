package com.tonmatsu.gles3lighting3d.gles.array;

import com.tonmatsu.gles3lighting3d.gles.buffer.*;

public class VertexAttributeLayout {
    final VertexBuffer buffer;
    final VertexAttribute[] attributes;

    public VertexAttributeLayout(VertexBuffer buffer, VertexAttribute... attributes) {
        this.buffer = buffer;
        this.attributes = attributes;
    }
}
