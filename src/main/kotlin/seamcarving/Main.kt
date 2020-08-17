package seamcarving

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.lang.NumberFormatException
import javax.imageio.ImageIO

fun main() {
    run()
}

fun run() {
    val size = getRectSizeFromConsole()
    val bufferedImage = BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB)
    drawX(bufferedImage.createGraphics(), size[0], size[1], Color.RED)

    ImageIO.write(bufferedImage, "png", File(getFilepathFromConsole()))
}

fun drawX(graphics: Graphics, rectWidth: Int, rectHeight: Int, color: Color) {
    val c = graphics.color
    graphics.color = color
    graphics.drawLine(0, 0, rectWidth - 1, rectHeight - 1)
    graphics.drawLine(0, rectHeight - 1, rectWidth - 1, 0)
    graphics.color = c
}

fun getRectSizeFromConsole(): IntArray {
    while (true) {
        try {
            println("Enter rectangle width:")
            val width = readLine()!!.toInt()
            println("Enter rectangle height:")
            val height = readLine()!!.toInt()

            if (width > 0 && height > 0) {
                return intArrayOf(width, height)
            }
            println("Error, values should not be negative, please try again")

        } catch (e: NumberFormatException) {
            println("Error, incorrect input, please try again")
        }
    }
}

fun getFilepathFromConsole(): String {
    println("Enter output image name:")
    return readLine()!!
}