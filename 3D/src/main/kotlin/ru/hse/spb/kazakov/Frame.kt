package ru.hse.spb.kazakov

import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_TRIANGLES

import com.jogamp.opengl.awt.GLCanvas
import javax.swing.JFrame
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.GL2
import com.jogamp.opengl.glu.GLU
import com.mokiat.data.front.parser.OBJDataReference
import com.mokiat.data.front.parser.OBJFace
import com.mokiat.data.front.parser.OBJParser
import uno.buffer.intBufferOf
import uno.buffer.toByteBuffer
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import javax.imageio.ImageIO

const val SCREEN_WIDTH = 700
const val SCREEN_HEIGHT = 700
const val DISSOLVE_SPEED = 0.01f

class Frame(modelFilePath: String) : JFrame("3D"), GLEventListener {
    private lateinit var shader: Shader
    private val camera: Camera = Camera()
    private val model = this::class.java.classLoader.getResourceAsStream(modelFilePath).use { OBJParser().parse(it) }
    private var dissolveThreshold = 0.0f
    private var dissolveFact = 1

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
        val animator = FPSAnimator(canvas, 60, true)
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
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT or GL2.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()
        camera.apply(gl)
        shader.setValue("dissolve_threshold", dissolveThreshold)

        gl.glBegin(GL_TRIANGLES)
        model.objects.map { it.meshes }.flatten().map { it.faces }.flatten().forEach { drawFace(it, gl) }
        gl.glEnd()

        gl.glFlush()
        dissolveThreshold += DISSOLVE_SPEED * dissolveFact
        if (dissolveThreshold < 0) dissolveFact = 1
        if (dissolveThreshold > 3) dissolveFact = -1
    }

    private fun drawFace(face: OBJFace, gl: GL2) {
        val vertices = face.references
        for (i in 2..vertices.lastIndex) {
            drawVertices(listOf(vertices[0], vertices[i - 1], vertices[i]), gl)
        }
    }

    private fun drawVertices(vertices: List<OBJDataReference>, gl: GL2) {
        vertices.forEach {
            if (it.hasNormalIndex()) {
                val normal = model.normals[it.normalIndex]
                gl.glNormal3f(normal.x, normal.y, normal.z)
            }
            if (it.hasTexCoordIndex()) {
                val texCoord = model.texCoords[it.texCoordIndex]
                gl.glTexCoord2f(texCoord.u, texCoord.v)
            }
            val vertex = model.vertices[it.vertexIndex]
            gl.glVertex3f(vertex.x, vertex.y, vertex.z)
        }
    }

    override fun init(drawable: GLAutoDrawable) {
        val gl = drawable.gl.gL2
        shader = Shader(gl, PHONG_FRAGMENT_SHADER, PHONG_VERTEX_SHADER)
        setUpTexture(TEXTURE, gl)
        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glMatrixMode(GL2.GL_PROJECTION)
        GLU().gluPerspective(45.0, 1.0, 1.0, 50.0)
        gl.glMatrixMode(GL2.GL_MODELVIEW)
        gl.glEnable(GL2.GL_DEPTH_TEST)
    }

    private fun setUpTexture(sourceFile: String, gl: GL2) {
        gl.glGenTextures(1, intBufferOf(0))
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR)
        shader.setValue("dissolve_texture", 0)

        val texture = this::class.java.classLoader.getResourceAsStream(sourceFile)
        val image = ImageIO.read(texture)
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, image.width, image.height, 0, GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE, image.toBuffer())
        gl.glGenerateMipmap(GL.GL_TEXTURE_2D)
        gl.glEnable(GL.GL_TEXTURE_2D)
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    }

    override fun dispose(drawable: GLAutoDrawable?) {
        shader.delete()
    }
}

private fun BufferedImage.toBuffer() = (data.dataBuffer as DataBufferByte).data.toByteBuffer()
