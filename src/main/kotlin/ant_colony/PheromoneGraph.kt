package ant_colony

import graph.Graph
import graph.Node

/**
 * Абстрактный класс, описывающий граф с ребрами, содержащими феромоны
 * @see Graph
 */
abstract class PheromoneGraph<T : Comparable<T>>(
    nodes: List<Node>, override val edges: MutableList<PheromoneEdge<T>>
): Graph<T, PheromoneEdge<T>>(nodes, edges)