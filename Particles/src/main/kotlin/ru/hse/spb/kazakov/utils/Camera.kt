package ru.hse.spb.kazakov.utils

import com.jogamp.newt.event.MouseEvent
import com.jogamp.newt.event.MouseListener
import jglm.*
import ru.hse.spb.kazakov.*

private enum class Status {
    IDLE, ROTATING
}

class Camera(private var initPos: Vec3, private var target: Vec3, private var initUp: Vec3) : MouseListener {
    private var status = Status.IDLE
    private var startingPoint = Vec2i(0, 0)
    private var rotation = Mat4(1f)
    private var currDiff = Vec2i(0, 0)
    private var oldDiff = Vec2i(0, 0)
    var pos: Vec3 = initPos
    var up: Vec3 = initUp

    fun calculateMatrix(): Mat4 {
        update()
        val cameraTransform = Mat4.CalcLookAtMatrix(pos, target, up)
        val porjTransform = Jglm.perspective(FOV, WINDOW_WIDTH / WINDOW_HEIGHT.toFloat(), Z_NEAR, Z_FAR)
        return porjTransform.mult(cameraTransform)
    }

    private fun update() {
        val totalDiff = oldDiff.plus(currDiff)
        rotation = Mat4.rotationY(Math.toRadians(totalDiff.x.toDouble()).toFloat())
        rotation = rotation.mult(Mat4.rotationX(Math.toRadians(totalDiff.y.toDouble()).toFloat()))
        pos = Vec3(rotation.mult(Vec4(initPos, 1f)))
        up = Vec3(rotation.mult(Vec4(initUp, 1f)))
    }

    override fun mouseClicked(me: MouseEvent) {}
    override fun mouseEntered(me: MouseEvent) {}
    override fun mouseExited(me: MouseEvent) {}
    override fun mousePressed(me: MouseEvent) {
        if (status == Status.IDLE && me.button == MouseEvent.BUTTON1) {
            status = Status.ROTATING
            startingPoint = Vec2i(me.x, me.y)
        } else {
            status = Status.IDLE
        }
    }

    override fun mouseReleased(me: MouseEvent) {
        if (me.button == MouseEvent.BUTTON1) {
            status = Status.IDLE
            oldDiff = oldDiff.plus(currDiff)
            currDiff = Vec2i(0, 0)
        }
    }

    override fun mouseDragged(me: MouseEvent) {
        if (status == Status.ROTATING) {
            val currentPoint = Vec2i(me.x, me.y)
            currDiff = currentPoint.minus(startingPoint)
        }
    }

    override fun mouseWheelMoved(me: MouseEvent) {
        val scroll = me.rotation[1] / 10f
        initPos = initPos.plus(Vec3(0f, 0f, scroll))
        initPos.z = Jglm.clamp(initPos.z, 0.01f, 100f)
    }

    override fun mouseMoved(e: MouseEvent) {
    }
}
