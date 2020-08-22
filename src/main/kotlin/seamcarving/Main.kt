package seamcarving

import java.awt.Color

fun main(args: Array<String>) {
    val inBufferedImg = readImage(args[args.indexOf("-in") + 1])
    val energy = computeEnergy(inBufferedImg)
    val normalizedEnergy = normalizeEnergy(energy, deapMax(energy))
    val outImgWithVerticalSeam = setPixelsColor(inBufferedImg, findCoordsOfShortestVerticalPath(normalizedEnergy), Color.RED)
    writeImage(outImgWithVerticalSeam, args[args.indexOf("-out") + 1])
}
