package sanskrit.ui

import java.awt.BorderLayout
import java.awt.Frame
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.InvocationTargetException
import javax.swing.WindowConstants


class ApplicationWindow @JvmOverloads constructor(title: String = "Application Window") : JFrame(title) {
    var exitOnClose = true

    init {
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        createUI()
    }

    fun maximize() {
        extendedState = extendedState or Frame.MAXIMIZED_BOTH
    }

    fun fullScreen() {
        extendedState = Frame.MAXIMIZED_BOTH
        center()
    }

    fun center() {
        GuiUtils.center(this)
    }

    fun setPercentageOfScreen(widthPercentage: Int, heightPercentage: Int) {
        GuiUtils.setPercentageOfScreen(this, widthPercentage, heightPercentage)
    }

    fun setWidthPercentageOfScreen(percentage: Int) {
        GuiUtils.setWidthPercentageOfScreen(this, percentage)
    }

    fun setHeightPercentageOfScreen(percentage: Int) {
        GuiUtils.setHeightPercentageOfScreen(this, percentage)
    }

    fun setPercentageOfScreen(widthPercentage: Double, heightPercentage: Double) {
        GuiUtils.setPercentageOfScreen(this, widthPercentage, heightPercentage)
    }

    fun setWidthPercentageOfScreen(percentage: Double) {
        GuiUtils.setWidthPercentageOfScreen(this, percentage)
    }

    fun setHeightPercentageOfScreen(percentage: Double) {
        GuiUtils.setHeightPercentageOfScreen(this, percentage)
    }

    fun placeOnSecondaryScreen(): Boolean {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val defaultScreen = ge.defaultScreenDevice
        var secondaryScreen: GraphicsDevice? = null

        val graphicsDevices = ge.screenDevices
        for (i in graphicsDevices.indices) {
            if (graphicsDevices[i] !== defaultScreen && graphicsDevices[i].type == GraphicsDevice.TYPE_RASTER_SCREEN) {
                secondaryScreen = graphicsDevices[i]
                break
            }
        }

        if (secondaryScreen != null) {
            val configuration = secondaryScreen.defaultConfiguration
            val bounds = configuration.bounds
            setLocation(bounds.x, bounds.y)
            return true
        }

        return false
    }

    fun placeOnPrimaryScreen(): Boolean {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val defaultScreen = ge.defaultScreenDevice

        if (defaultScreen != null) {
            val configuration = defaultScreen.defaultConfiguration
            val bounds = configuration.bounds
            setLocation(bounds.x, bounds.y)
            return true
        }

        return false
    }

    fun reset() {
        setPercentageOfScreen(75, 75)
        placeOnPrimaryScreen()
        center()
    }

    fun closeApplicationWindow() {
        isVisible = false
        if (exitOnClose) {
            try {
                dispose()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                System.exit(0)
            }
        }
    }

    @Throws(IllegalStateException::class)
    fun handleQuit() {
        println("handleQuit")
        closeApplicationWindow()
        throw IllegalStateException()
    }

    protected fun createUI() {
        contentPane.layout = BorderLayout()
        setPercentageOfScreen(95, 75)
        center()

        addWindowListener(object : WindowAdapter() {

            override fun windowClosing(e: WindowEvent?) {
                closeApplicationWindow()
            }
        })
    }

    companion object {
        init {
            MacUtils.useMacScreenMenuBar()
        }
    }
}

object MacUtils {
    fun useMacScreenMenuBar() {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
    }
}