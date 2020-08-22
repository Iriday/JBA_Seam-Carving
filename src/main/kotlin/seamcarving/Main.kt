package seamcarving

import java.awt.Color

fun main(args: Array<String>) {
    val inBufferedImg = readImage(args[args.indexOf("-in") + 1])
    val energy = computeEnergy(inBufferedImg)
    val normalizedEnergy = normalizeEnergy(energy, deapMax(energy))
    setPixelsColor(inBufferedImg, findCoordsOfShortestVerticalPath(normalizedEnergy), Color.RED)
    setPixelsColor(inBufferedImg, findCoordsOfShortestHorizontalPath(normalizedEnergy), Color.RED)
    writeImage(inBufferedImg, args[args.indexOf("-out") + 1])
}
