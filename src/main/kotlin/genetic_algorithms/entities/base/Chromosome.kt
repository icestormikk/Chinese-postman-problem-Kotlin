package genetic_algorithms.entities.base

import common.Identifiable
import kotlin.properties.Delegates

/**
 * Хромосома, представляющая собой закодированное решение некоторой задачи
 * @param T Тип значения, взятого в качестве гена
 * @property genes Список генов
 */
data class Chromosome<T>(
    private val _genes: List<T>
) : Identifiable() {
    var genes: List<T> by Delegates.observable(_genes, onChange = { _, _, new ->
        fitness = null
    })

    // Значение пригодности особи (для избежания повторных вычислений)
    var fitness: Double? = null
}
