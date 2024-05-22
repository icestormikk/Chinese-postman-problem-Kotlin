package ant_colony

import graph.Graph
import graph.Node

abstract class PheromoneGraph<T>(
    nodes: List<Node>, override val edges: MutableList<PheromoneEdge<T>>
): Graph<T, PheromoneEdge<T>>(nodes, edges)