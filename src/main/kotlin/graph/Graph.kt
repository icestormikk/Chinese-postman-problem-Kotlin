package graph

import common.Identifiable


/**
 * Абстрактный класс, содержащий обязательные атрибуты и методы для любого графа, который будет реализован в
 * программе
 * @param T Тип веса ребёр в графе (целое число, вещественное число и тд)
 * @property nodes Список всех вершин графа
 * @property edges Список всех рёбер графа
 */
abstract class Graph<T, E: Edge<T>>(
    val nodes: List<Node>,
    open val edges: MutableList<E>
) : Identifiable() {
    /**
     * Получение всех ребёр, по которым можно "выйти" из данной вершины
     * @param node Вершина, для которой надо найти подходящие рёбра
     * @return Список всех ребёр, подходящих под условие
     */
    open fun getEdgesFrom(node: Node): List<E> {
        return edges.filter {
            when (it.type) {
                EdgeType.DIRECTED -> {
                    return@filter it.source.id == node.id
                }
                EdgeType.NOT_ORIENTED -> {
                    return@filter it.source.id == node.id || it.destination.id == node.id
                }
            }
        }
    }

    open fun getEdgesFromTo(fromNode: Node, toNode: Node): List<E> {
        return edges.filter {
            when (it.type) {
                EdgeType.NOT_ORIENTED -> {
                    return@filter listOf(fromNode, toNode).all { node -> listOf(it.source, it.destination).contains(node) }
                }
                EdgeType.DIRECTED -> {
                    return@filter it.source.id == fromNode.id && it.destination.id == toNode.id
                }
            }
        }
    }

    /**
     * Абстрактная функция, которая вычисляет общую длину пути
     * @param path Путь, представляющий собой набор ребёр графа
     * @return Значение, которое отражает длину переданного пути
     */
    abstract fun calculateTotalLengthOf(path: List<E>): Double

    /**
     * Функция для нахождения случайного замкнутого пути в графе
     * @param startNode Начальная и конечная веришна пути
     * @return Один из существующих в графе путей, набор рёбер
     */
    fun getRandomPath(startNode: Node = nodes.random()): MutableList<E> {
        // Набор рёбер, которые были посещены минимум один раз
        val visited = mutableSetOf<E>()
        // Один из возможных путей в графе
        val path = mutableListOf<E>()

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
    fun getPathBetween(startNode: Node, endNode: Node): List<E>? {
        val visitedEdges = mutableSetOf<Node>()
        val queue = ArrayDeque<List<E>>()
        queue.add(listOf())

        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val currentNode = if (path.isNotEmpty()) path.last().destination else startNode

            if (currentNode == endNode && path.isNotEmpty()) {
                return path
            }

            if (currentNode !in visitedEdges) {
                visitedEdges.add(currentNode)
                val adjacentEdges = getEdgesFrom(currentNode)
                for (edge in adjacentEdges) {
                    queue.add(path + edge)
                }
            }
        }

        return null
    }

    /**
     * Функция для получения общей вершины между двух рёбер
     * @param startEdge Первое ребро
     * @param endEdge Второе ребро
     * @return Общая для двух вышеуказанных рёбер вершина (если имеется)
     */
    fun getCommonNode(startEdge: E, endEdge: E): Node? {
        return when (startEdge.type) {
            EdgeType.NOT_ORIENTED -> {
                when (endEdge.type) {
                    EdgeType.NOT_ORIENTED -> {
                        setOf(startEdge.source, startEdge.destination)
                            .intersect(setOf(endEdge.source, endEdge.destination))
                            .firstOrNull()
                    }
                    EdgeType.DIRECTED -> {
                        setOf(startEdge.source, startEdge.destination)
                            .intersect(setOf(endEdge.source))
                            .firstOrNull()
                    }
                }
            }
            EdgeType.DIRECTED -> {
                when (endEdge.type) {
                    EdgeType.NOT_ORIENTED -> {
                        setOf(startEdge.destination)
                            .intersect(setOf(endEdge.source, endEdge.destination))
                            .firstOrNull()
                    }
                    EdgeType.DIRECTED -> {
                        startEdge.destination
                    }
                }
            }
        }
    }

    fun pathToString(path: List<E>, separator: String = ", "): String =
        path.joinToString(separator) { "${it.source.label} -> ${it.destination.label}" }

    fun pathToNodeString(path: List<E>, startNode: Node): String {
        val nodes = mutableListOf(startNode)
        for (i in 0 until path.size - 1) {
            val commonNode = getCommonNode(path[i], path[i+1])
                ?: throw IllegalStateException("Can't find a common node for: ${pathToString(path.slice(i..i+1))}")
            nodes.add(commonNode)
        }
        nodes.add(startNode)

        return nodes.joinToString("->") { it.label }
    }
}
