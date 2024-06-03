package genetic_algorithms.entities.base

import common.Identifiable

/**
 * Хромосома, представляющая собой закодированное решение некоторой задачи
 * @param T Тип значения, взятого в качестве гена
 * @property genes Список генов
 */
data class Chromosome<T>(
    val genes: MutableList<T>,
) : Identifiable() {
    // Значение пригодности особи (для избежания повторных вычислений)
    var fitness: Double? = null
}
