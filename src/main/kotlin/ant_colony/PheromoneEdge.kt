package ant_colony

import graph.Edge
import graph.EdgeType
import graph.Node

class PheromoneEdge<T>(
    id: String,
    source: Node,
    destination: Node,
    weight: T,
    type: EdgeType,
    var pheromoneCount: Double = 0.0,
) : Edge<T>(source, destination, weight, type, id)