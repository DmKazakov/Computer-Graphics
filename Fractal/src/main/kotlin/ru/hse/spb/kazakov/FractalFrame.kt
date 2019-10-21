package ru.hse.spb.kazakov

import com.jogamp.opengl.*

import com.jogamp.opengl.awt.GLCanvas
import javax.swing.JFrame
import com.jogamp.opengl.util.FPSAnimator

const val SCREEN_WIDTH = 700
const val SCREEN_HEIGHT = 700

class FractalFrame : JFrame("Fractal"), GLEventListener {
    private val camera = Camera()
    private lateinit var shader: Shader
    var iterations = 100
    var threshold = 4.0f

    init {
        val profile = GLProfile.get(GLProfile.GL2)
        val capabilities = GLCapabilities(profile)
        val canvas = GLCanvas(capabilities)
        setUpCanvas(canvas)
        setUpWindow()
    }

    private fun setUpCanvas(canvas: GLCanvas) {
        canvas.addGLEventListener(this)
        canvas.addMouseWheelListener(camera)
        canvas.addMouseListener(camera)
        canvas.addMouseMotionListener(camera)
        contentPane.add(canvas)
        canvas.requestFocusInWindow()
        val animator = FPSAnimator(canvas, 60)
        animator.start()
    }

    private fun setUpWindow() {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = false
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        resetShaderValues()

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0f, 0f);
        gl.glVertex2f(-1f, -1f);
        gl.glTexCoord2f(1f, 0f);
        gl.glVertex2f(1f, -1f);
        gl.glTexCoord2f(1f, 1f);
        gl.glVertex2f(1f, 1f);
        gl.glTexCoord2f(0f, 1f);
        gl.glVertex2f(-1f, 1f);
        gl.glEnd();

        gl.glFlush()
    }

    private fun resetShaderValues() {
        shader.setValue("shift", camera.shiftX, camera.shiftY)
        shader.setValue("zoom", camera.zoom)
        shader.setValue("iterations", iterations)
        shader.setValue("threshold", threshold)
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2

        shader = Shader(gl, TEXTURE_RESOURCE, MANDELBROT_RESOURCE)
        resetShaderValues()
    }

    override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int) {
    }

    override fun dispose(drawable: GLAutoDrawable?) {
    }
}
