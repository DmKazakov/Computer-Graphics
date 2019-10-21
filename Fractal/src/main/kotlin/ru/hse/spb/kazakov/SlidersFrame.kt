package ru.hse.spb.kazakov

import java.util.*
import javax.swing.*
import javax.swing.event.ChangeListener


class SlidersFrame : JFrame("Settings") {
    private val panel: JPanel
    val iterationSlider = JSlider(1, 1000)
    val thresholdSlider = JSlider(1, 500)
    val reSlider = JSlider(1, 101)
    val imSlider = JSlider(1, 101)

    init {
        panel = JPanel()
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        val boxLayout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.layout = boxLayout

        setUpSlider(iterationSlider, "1", "1000", "Iterations number")
        setUpSlider(thresholdSlider, "0.1", "50", "Threshold")
        setUpSlider(reSlider, "0", "1", "Re")
        setUpSlider(imSlider, "0", "1", "Im")

        add(panel)
        setSize(300, 240)
    }

    private fun setUpSlider(slider: JSlider, minLabel: String, maxLabel: String, sliderLabel: String) {
        slider.paintLabels = true
        val sliderLabels = Hashtable<Int, JLabel>()
        sliderLabels[slider.minimum] = JLabel(minLabel)
        sliderLabels[slider.maximum] = JLabel(maxLabel)
        slider.labelTable = sliderLabels

        val sliderJLabel = JLabel("$sliderLabel:", JLabel.CENTER)
        sliderJLabel.alignmentX = JLabel.CENTER_ALIGNMENT
        panel.add(sliderJLabel)
        panel.add(slider)
    }

    fun setListener(listener: ChangeListener) {
        iterationSlider.addChangeListener(listener)
        thresholdSlider.addChangeListener(listener)
        reSlider.addChangeListener(listener)
        imSlider.addChangeListener(listener)
    }

    fun setDefaultValues() {
        iterationSlider.value = 100
        thresholdSlider.value = 30
        reSlider.value = 101
        imSlider.value = 50
    }
}
