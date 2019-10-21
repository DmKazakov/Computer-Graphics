package ru.hse.spb.kazakov

import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Fractal : ChangeListener {
    private val fractalFrame = FractalFrame()
    private val slidersFrame = SlidersFrame()

    init {
        slidersFrame.setListener(this)
        slidersFrame.setDefaultValues()
        slidersFrame.isVisible = true
    }

    override fun stateChanged(p0: ChangeEvent) {
        fractalFrame.iterations = slidersFrame.iterationSlider.value
        fractalFrame.threshold = slidersFrame.thresholdSlider.value / 10.0f
        fractalFrame.re = (slidersFrame.reSlider.value - 0.01f) / 100f
        fractalFrame.im = (slidersFrame.imSlider.value - 0.01f) / 100f
    }
}
