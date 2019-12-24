package ru.hse.spb.kazakov

import com.jogamp.newt.awt.NewtCanvasAWT
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.util.Animator
import jglm.Vec3
import ru.hse.spb.kazakov.particle.ParticleSystem
import ru.hse.spb.kazakov.utils.Camera

class GLDrawer : GLEventListener {
    val glWindow = GLWindow.create(GLCapabilities(GLProfile.getGL2GL3()))
    val newtCanvasAWT = NewtCanvasAWT(glWindow)
    val animator = Animator(glWindow)
    private val camera = Camera(
        Vec3(0.7f, 0.7f, 0.7f),
        Vec3(0f, 0f, 0f),
        Vec3(0f, 1f, 0f)
    )
    private val particleSystem = ParticleSystem()
    private var time = System.currentTimeMillis()

    init {
        glWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
        glWindow.addGLEventListener(this)
        glWindow.addMouseListener(camera)
        animator.setRunAsFastAsPossible(true)
        animator.start()
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL3
        particleSystem.init(gl)
        gl.glClearColor(0f, 0f, 0f, 0f)
    }

    override fun display(drawable: GLAutoDrawable) {
        val now = System.currentTimeMillis()
        val deltaTime = now - time
        time = now
        val gl = drawable.gl.gL3
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT or GL3.GL_DEPTH_BUFFER_BIT)
        particleSystem.render(gl, deltaTime.toInt(), camera.calculateMatrix(), camera.pos)
    }

    override fun dispose(drawable: GLAutoDrawable?) {}
    override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int) {}
}
