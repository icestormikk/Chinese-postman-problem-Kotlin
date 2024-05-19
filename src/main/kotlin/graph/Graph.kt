package graph

import common.Identifiable


/**
 * Абстрактный класс, содержащий обязательные атрибуты и методы для любого графа, который будет реализован в
 * программе
 * @param T Тип веса ребёр в графе (целое число, вещественное число и тд)
 * @property nodes Список всех вершин графа
 * @property edges Список всех рёбер графа
 */
abstract class Graph<T>(
    val nodes: List<Node>,
    open val edges: List<Edge<T>>
) : Identifiable() {
    /**
     * Получение всех ребёр, по которым можно "выйти" из данной вершины
     * @param node Вершина, для которой надо найти подходящие рёбра
     * @return Список всех ребёр, подходящих под условие
     */
    open fun getEdgesFrom(node: Node): List<Edge<T>> {
        return edges.filter {
            (it.type == EdgeType.DIRECTED && it.source.id == node.id) ||
                    (it.type == EdgeType.NOT_ORIENTED && (it.source.id == node.id || it.destination.id == node.id))
        }
    }

    /**
     * Абстрактная функция, которая вычисляет общую длину пути
     * @param path Путь, представляющий собой набор ребёр графа
     * @return Значение, которое отражает длину переданного пути
     */
    abstract fun calculateTotalLengthOf(path: Array<Edge<T>>): T

    /**
     * Функция для нахождения случайного замкнутого пути в графе
     * @param startNode Начальная и конечная веришна пути
     * @return Один из существующих в графе путей, набор рёбер
     */
    fun getRandomPath(startNode: Node = nodes.random()): MutableList<Edge<T>> {
        // Набор рёбер, которые были посещены минимум один раз
        val visited = mutableSetOf<Edge<T>>()
        // Один из возможных путей в графе
        val path = mutableListOf<Edge<T>>()

        /**
         * Рекурсивная функция, которая случайным образом блуждает по графу и строит путь
         * @param node Вершина, в которой сейчас находится builder
         */
        fun pathBuilder(node: Node) {
            // список всех рёбер, исходящих из текущей вершины
            val suitableEdges = getEdgesFrom(node)
            // список всех ни разу не посещенных ребёр, исходящих из текущей вершины
            val notVisitedSuitableEdges = suitableEdges.filter { !visited.contains(it) }

            // если попали в вершину, из которой не исходит ни одно ребро, бросаем исключение
            if (suitableEdges.isEmpty()) {
                throw IllegalArgumentException("There are no output edges from the node with id ${node.id}")
            }

            // если мы посетили каждое ребро минимум один раз и находимся в стартовой вершине, то
            // выходим из рекурсии
            if (visited.size >= edges.size && node.id == startNode.id) return

            // выбираем, куда функция будет двигаться дальше: если из этой веришны исходят ребра, в которых
            // мы ни разу не были, выбираем любое из них; иначе - выбираем любое из имеющихся рёбер
            val nextEdge = (notVisitedSuitableEdges.ifEmpty { suitableEdges }).random()

            // обновляем список посещённых вершин и путь
            visited.add(nextEdge)
            path.add(nextEdge)
            // следующая вершина - пункт назначения выбранного ребра
            pathBuilder(if (nextEdge.destination.id == node.id) nextEdge.source else nextEdge.destination)
        }

        // строим путь и возвращаем его в качестве результата
        pathBuilder(startNode)
        return path
    }

    /**
     * Функция, которая находит один возможных путей между двумя вершинами в графе
     * @param startNode Начальная вершина
     * @param endNode Конечная вершина
     * @return Один из возможных путей между двумя вершинами в виде списка рёбер
     */
    private fun getPathBetween(startNode: Node, endNode: Node): List<Edge<T>>? {
        val visitedEdges = mutableSetOf<Node>()
        val queue = ArrayDeque<List<Edge<T>>>()
        queue.add(listOf())

        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val currentNode = if (path.isNotEmpty()) path.last().destination else startNode

            if (currentNode == endNode) {
                return path
            }

            if (currentNode !in visitedEdges) {
                visitedEdges.add(currentNode)
                val adjacentEdges = edges.filter { it.source == currentNode }
                for (edge in adjacentEdges) {
                    queue.add(path + edge)
                }
            }
        }

        return null
    }
}