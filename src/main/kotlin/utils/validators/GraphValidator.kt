package utils.validators

import graph.GraphDao
import utils.helpers.LoggingHelper

object GraphValidator {
    private val logger = LoggingHelper.getLogger("GRAPH_VALIDATOR_LOGGER")

    fun validateGraphDao(graphDao: GraphDao) {
        try {
            require(graphDao.nodes.isNotEmpty()) { "The number of vertices in the graph must be strictly greater than zero" }
            require(graphDao.edges.isNotEmpty()) { "The number of edges in the graph must be strictly greater than zero" }

            graphDao.nodes.forEach { node ->
                require(graphDao.edges.any { it.source.id == node.id || it.destination.id == node.id }) {
                    "The vertex with id ${node.id} (${node.label}) is not connected to any edge"
                }
            }
        } catch (e: Exception) {
            logger.error { e.message }
            throw IllegalArgumentException("Error in graph construction (${e.message})")
        }
    }
}