package seamcarving

fun main(args: Array<String>) {
    val inBufferedImg = readImage(args[args.indexOf("-in") + 1])
    val outReducedImg = reduceImage(inBufferedImg, args[args.indexOf("-width") + 1].toInt(), args[args.indexOf("-height") + 1].toInt())
    writeImage(outReducedImg, args[args.indexOf("-out") + 1])
}
