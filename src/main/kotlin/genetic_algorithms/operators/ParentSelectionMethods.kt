package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import genetic_algorithms.Population

/**
 * Набор алгоритмов для отбора родителей
 */
object ParentSelectionMethods {
    /**
     * Список доступных алгоритмов
     */
    enum class Types {
        PANMIXIA,
        INBREEDING,
        OUTBREEDING
    }

    /**
     * Панмиксия - случайным образом из популяции выбираются две особи
     * @param population Набор особей, среди которых выбираются два родителя
     * @return Пара особей-родителей
     */
    fun <T> panmixia(population: Population<T>): Pair<Chromosome<T>, Chromosome<T>> {
        val firstParent = population.entities.random()
        val secondParent = population.entities.random()
        return Pair(firstParent, secondParent)
    }

    /**
     * Инбридинг - выбор наиболее "близких" особей
     * @param population Набор особей, среди которых выбираются два родителя
     * @param onDistance Функция для определения степени близости особей
     * @return Пара особей-родителей
     */
    fun <T> inbreeding(
        population: Population<T>, onDistance: (chr1: Chromosome<T>, chr2: Chromosome<T>) -> Double
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val first = population.entities.random()
        val sortedByDistance = population.entities
            .filter { it.id != first.id }
            .sortedBy { onDistance(first, it) } // сортировка идёт по возрастанию

        return Pair(first, sortedByDistance.getOrElse(0) { first })
    }

    /**
     * Аутбридинг - выбор наиболее "далёких" особей
     * @param population Набор особей, среди которых выбираются два родителя
     * @param onDistance Функция для определения степени близости особей
     * @return Пара особей-родителей
     */
    fun <T> outbreeding(
        population: Population<T>, onDistance: (chr1: Chromosome<T>, chr2: Chromosome<T>) -> Double
    ): Pair<Chromosome<T>, Chromosome<T>> {
        val first = population.entities.random()
        val sortedByDistance = population.entities
            .filter { it.id != first.id }
            .sortedByDescending { onDistance(first, it) } // сортировка идёт по убыванию

        return Pair(first, sortedByDistance.getOrElse(0) { first })
    }
}