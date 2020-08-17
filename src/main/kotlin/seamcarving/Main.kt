package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    if (args.size != 4 || args[0] != "-in" || args[2] != "-out") {
        print("Error, incorrect arguments")
        return
    }
    val bufferedImage = readImage(args[1])
    negateImage(bufferedImage)
    writeImage(args[3], bufferedImage)
}

fun negateImage(image: BufferedImage) {
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val color = Color(image.getRGB(x, y))
            image.setRGB(x, y, Color(255 - color.red, 255 - color.green, 255 - color.blue).rgb)
        }
    }
}

fun readImage(path: String): BufferedImage {
    return ImageIO.read(File(path))
}

fun writeImage(path: String, image: BufferedImage) {
    ImageIO.write(image, "png", File(path))
}
