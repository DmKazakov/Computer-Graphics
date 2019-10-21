uniform sampler1D tex;

uniform vec2 shift;
uniform float zoom;
uniform int iterations;
uniform float threshold;

void main() {
    vec2 c;
    c.x = (gl_TexCoord[0].x - 0.5) * zoom - shift.x;
    c.y = (gl_TexCoord[0].y - 0.5) * zoom - shift.y;

    vec2 z = c;
    int i = 0;
    for (; i < iterations; i++) {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = 2.0 * z.y * z.x + c.y;

        if ((x * x + y * y) > threshold) {
            break;
        }
        z.x = x;
        z.y = y;
    }

    float color = (i == iterations ? 0.0 : float(i)) / float(iterations);
    gl_FragColor = texture1D(tex, color);
}
