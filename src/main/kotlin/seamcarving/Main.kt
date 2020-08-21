package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val inBufferedImg = readImage(args[args.indexOf("-in") + 1])
    val energy = computeEnergy(inBufferedImg)
    val normalizedEnergy = normalizeEnergy(energy, deapMax(energy))
    val outImgWithVerticalSeam = setPixelsColor(inBufferedImg, findCoordsOfShortestVerticalPath(normalizedEnergy), Color.RED)
    writeImage(outImgWithVerticalSeam, args[args.indexOf("-out") + 1])
}

fun computeEnergy(img: BufferedImage): List<MutableList<Double>> {
    fun adjustCoord(coord: Int, len: Int): Int =
        when (coord) { 0 -> 1; len - 1 -> len - 2; else -> coord }

    fun calcGrad(a: Color, b: Color): Double =
        (a.red - b.red).toDouble().pow(2) + (a.green - b.green).toDouble().pow(2) + (a.blue - b.blue).toDouble().pow(2)

    val energy = List(img.height) { MutableList(img.width) { 0.0 } }

    for (x in 0 until img.width) {
        for (y in 0 until img.height) {
            val x2 = adjustCoord(x, img.width)
            val y2 = adjustCoord(y, img.height)
            // from current pixel
            val left = Color(img.getRGB(x2 - 1, y))
            val right = Color(img.getRGB(x2 + 1, y))
            val top = Color(img.getRGB(x, y2 - 1))
            val bottom = Color(img.getRGB(x, y2 + 1))

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

// Dijkstra's algorithm
fun findCoordsOfShortestVerticalPath(matrix: List<List<Double>>): List<IntArray> {
    // pair.one = sum of energies; pair.two = index of prev (path)
    val temp = MutableList(matrix.size) { MutableList(matrix[0].size) { MutablePair(Double.MAX_VALUE, 0) } }
    temp[0] = matrix[0].map { v -> MutablePair(v, -1) }.toMutableList()

    for (i in 1..matrix.lastIndex) {
        for (j in matrix[i].indices) {
            val prev = temp[i - 1][j].one
            // left down
            if (j != 0 && (prev + matrix[i][j - 1]) < temp[i][j - 1].one) {
                temp[i][j - 1].one = prev + matrix[i][j - 1]
                temp[i][j - 1].two = j
            }
            // down
            if ((prev + matrix[i][j]) < temp[i][j].one) {
                temp[i][j].one = prev + matrix[i][j]
                temp[i][j].two = j
            }
            // down right
            if (j != matrix[i].lastIndex && (prev + matrix[i][j + 1]) < temp[i][j + 1].one) {
                temp[i][j + 1].one = prev + matrix[i][j + 1]
                temp[i][j + 1].two = j
            }
        }
    }
    fun extractCoords(temp: List<List<MutablePair<Double, Int>>>): MutableList<IntArray> {
        val coords = mutableListOf<IntArray>()
        val lastRow = temp.last()
        val lastCoord = lastRow.minBy { it.one }!!
        var index = lastRow.indexOf(lastCoord)

        coords.add(intArrayOf(matrix.lastIndex, index))

        for (i in temp.lastIndex downTo 1) {
            index = temp[i][index].two
            coords.add(intArrayOf(i - 1, index))
        }
        return coords
    }
    return extractCoords(temp)
}

fun setPixelsColor(img: BufferedImage, pixelsCoords: List<IntArray>, color: Color): BufferedImage {
    pixelsCoords.forEach { (y, x) -> img.setRGB(x, y, color.rgb) }
    return img
}

fun deapMax(arr: List<List<Double>>): Double =
    arr.flatMap { it.toList() }.max() ?: throw IllegalArgumentException("Array is empty")

fun readImage(path: String): BufferedImage = ImageIO.read(File(path))

fun writeImage(img: BufferedImage, path: String, format: String = "png") = ImageIO.write(img, format, File(path))