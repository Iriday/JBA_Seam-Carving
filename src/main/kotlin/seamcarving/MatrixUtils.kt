package seamcarving

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

fun deapMax(matrix: List<List<Double>>): Double =
    matrix.flatMap { it.toList() }.max() ?: throw IllegalArgumentException("Matrix is empty")
