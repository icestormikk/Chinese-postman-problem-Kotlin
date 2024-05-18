package ant_colony

import graph.Graph
import graph.Node

abstract class PheromoneGraph<T>(nodes: List<Node>, override val edges: List<PheromoneEdge<T>>): Graph<T>(nodes, edges)