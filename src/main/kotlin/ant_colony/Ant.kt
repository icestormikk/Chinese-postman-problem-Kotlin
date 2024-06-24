package ant_colony

import graph.Node
import kotlin.math.pow
import kotlin.random.Random

/**
 * Класс для описания муравья
 * @property id Уникальное строковое значение для каждого муравья
 */
data class Ant(val id: String) {
    /**
     * Функция для создания пути движения муравья
     * @param graph Граф, по рёбрам которого перемещается муравей
     * @param startNode Начальная и конечная вершина движеняи муравья
     * @param proximityCoefficient Коэффициент близости вершин
     * @param alpha Коэффициент, показывающий насколько сильно муравьи при переходе будут обращать внимание на кол-во феромона
     * @param beta Коэффициент, показывающий насколько сильно муравьи при переходе будут обращать внимание на длину ребра
     * @return Список рёбер, по которым прошёл муравей
     */
    fun getPath(
        graph: PheromoneGraph<Double>,
        startNode: Node,
        proximityCoefficient: Double,
        alpha: Double,
        beta: Double,
        currentBestPathLength: Double
    ): MutableList<PheromoneEdge<Double>>? {
        // Набор посещённых рёбер
        val visitedEdgeIds = mutableSetOf<String>()

        /**
         * Получение ребра, по которому муравей пройдёт дальше
         * @param currentNode Вершина, в которой сейчас находится муравей
         * @return Ребро, по которому муравью следует продолжить движение
         */
        fun getNextEdge(currentNode: Node): PheromoneEdge<Double> {
            // получение списка всех возможных рёбер
            var suitableEdges = graph.getEdgesFrom(currentNode)
            val notVisitedSuitableEdges = suitableEdges.filter { !visitedEdgeIds.contains(it.id) }

            if (notVisitedSuitableEdges.isNotEmpty()) {
                suitableEdges = notVisitedSuitableEdges
            }

            // общее желание муравья
            val totalDesire = suitableEdges.sumOf { it.pheromoneCount.pow(alpha) * (proximityCoefficient / it.weight).pow(beta) }

            // случайное значение из промежутка от 0 до 1
            val randomValue = Random.nextDouble(0.0, 1.0)
            var sum = 0.0

            for (edge in suitableEdges) {
                // вычисляем желание муравья пройти по данной ветке
                sum += (edge.pheromoneCount.pow(alpha) * (proximityCoefficient / edge.weight).pow(beta)) / totalDesire
                // проверяем не было ли выбрано это ребро: если да, то возвращаем его
                if (randomValue < sum) {
                    visitedEdgeIds.add(edge.id)
                    return edge
                }
            }

            return suitableEdges.random()
            // если из вершины не исходит ни одно ребро, то бросаем исключение
//            throw IllegalStateException("Suitable edges were not found (Node: ${currentNode.id}, ${currentNode.label}, $sum, ${suitableEdges.map { it.pheromoneCount }}, $totalDesire, $randomValue)")
        }

        // путь муравья
        val path = mutableListOf<PheromoneEdge<Double>>()
        var currentLength = 0.0
        // муравей начинает своё движение из стартовой вершины
        var currentNode = startNode
        // пока каждое из рёбер графа не будет посещено хотя бы один раз и муравей не вернётся в стартовую вершину
        while (!(visitedEdgeIds.size >= graph.edges.size && currentNode.id == startNode.id)) {
            if (currentLength > currentBestPathLength) {
                return null
            }
            // получаем следующее ребро
            val nextEdge = getNextEdge(currentNode)
            // добавляем его в набор посещённых рёбер
            visitedEdgeIds.add(nextEdge.id)
            // добавляем это ребро в путь
            path.add(nextEdge)
            currentLength += nextEdge.weight
            // обновляем текущую вершину муравья: записываем в неё вершину, до которой муравей может добраться, пройдя по выбранному ребру
            currentNode = if (nextEdge.destination.id == currentNode.id) nextEdge.source else nextEdge.destination
        }

        // возвращаем готовый путь
        return path
    }
}
