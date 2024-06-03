package genetic_algorithms.entities.base

import common.Identifiable

/**
 * Класс, описывающий популяции в генетическом алгоритме. Обычно представляет собой набор решений некоторой задачи
 * @param T Тип генов в хромосомах
 * @property entities Набор особей или же решений некоторой задачи
 */
data class Population<T>(
    val entities: MutableList<Chromosome<T>>,
) : Identifiable() {
    init {
        require(entities.all { it.genes.isNotEmpty() }) {
            "A population cannot contain chromosomes without genes"
        }
    }
}