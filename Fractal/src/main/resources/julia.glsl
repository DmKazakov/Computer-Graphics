uniform sampler1D tex;

uniform vec2 shift;
uniform vec2 c;
uniform float zoom;
uniform int iterations;
uniform float threshold;

vec2 mult(vec2 z, vec2 c) {
    return vec2(z.x * c.x - z.y * c.y, z.x * c.y + z.y * c.x);
}

void main() {
    vec2 z;
    z.x = (gl_TexCoord[0].x - 0.5) * zoom - shift.x;
    z.y = (gl_TexCoord[0].y - 0.5) * zoom - shift.y;

    int i = 0;
    for (; i < iterations; i++) {
        float tx = sin(z.x) * (exp(z.y) + exp(-z.y)) / 2.0;
        float ty = cos(z.x) * (exp(z.y) - exp(-z.y)) / 2.0;
        vec2 t = mult(c, vec2(tx, ty));

        if ((t.x * t.x + t.y * t.y) > threshold) {
            break;
        }
        z.x = t.x;
        z.y = t.y;
    }

    float color = (i == iterations ? 0.0 : float(i)) / float(iterations);
    gl_FragColor = texture1D(tex, color);
}
