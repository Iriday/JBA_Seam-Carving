package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun readImage(path: String): BufferedImage = ImageIO.read(File(path))

fun writeImage(img: BufferedImage, path: String, format: String = "png") = ImageIO.write(img, format, File(path))

fun computeEnergy(rgbs: List<List<Int>>): List<MutableList<Double>> {
    val width = rgbs[0].size
    val height = rgbs.size

    fun adjustCoord(coord: Int, len: Int): Int =
        when (coord) { 0 -> 1; len - 1 -> len - 2; else -> coord }

    fun calcGrad(a: Color, b: Color): Double =
        (a.red - b.red).toDouble().pow(2) + (a.green - b.green).toDouble().pow(2) + (a.blue - b.blue).toDouble().pow(2)

    val energy = List(height) { MutableList(width) { 0.0 } }

    for (x in 0 until width) {
        for (y in 0 until height) {
            val x2 = adjustCoord(x, width)
            val y2 = adjustCoord(y, height)
            // from current pixel
            val left = Color(rgbs[y][x2 - 1])
            val right = Color(rgbs[y][x2 + 1])
            val top = Color(rgbs[y2 - 1][x])
            val bottom = Color(rgbs[y2 + 1][x])

            energy[y][x] = sqrt(calcGrad(left, right) + calcGrad(top, bottom)) // x gradient + y gradient
        }
    }
    return energy
}

fun normalizeEnergy(energy: List<List<Double>>, maxEnergyVal: Double): List<List<Double>> =
    energy.map { it.map { v -> 255.0 * v / maxEnergyVal } }

fun createGrayScaleImage(energy: List<List<Double>>, type: Int): BufferedImage {
    val image = BufferedImage(energy[0].size, energy.size, type)
    val arr = energy.map { it.map { v -> v.toInt() }.map { v -> Color(v, v, v).rgb } }.flatten().toIntArray()

    image.setRGB(0, 0, energy[0].size, energy.size, arr, 0, energy[0].size)
    return image
}

fun setPixelsColor(img: BufferedImage, pixelsCoords: List<IntArray>, color: Color): BufferedImage {
    pixelsCoords.forEach { (y, x) -> img.setRGB(x, y, color.rgb) }
    return img
}

fun swapCoords(coords: List<IntArray>): List<IntArray> {
    var temp: Int
    return coords.map { c -> temp = c[0]; c[0] = c[1]; c[1] = temp; c }
}

fun getRGBsFromImg(img: BufferedImage): MutableList<MutableList<Int>> {
    var y = -1
    var x: Int
    return MutableList(img.height) { y++; x = 0; MutableList(img.width) { img.getRGB(x++, y) } }
}

fun reduceImage(img: BufferedImage, width: Int): BufferedImage {
    val rgbs = getRGBsFromImg(img)

    for (i in 0 until width) {
        val energy = computeEnergy(rgbs)
        val normalizedEnergy = normalizeEnergy(energy, deapMax(energy))
        val coords = findCoordsOfShortestVerticalPath(normalizedEnergy)
        coords.forEach { (i, j) -> rgbs[i].removeAt(j) }
    }

    val newImg = BufferedImage(img.width - width, img.height, img.type)
    newImg.setRGB(0, 0, newImg.width, newImg.height, rgbs.flatten().toIntArray(), 0, newImg.width)
    return newImg
}
