package genetic_algorithms.operators

import genetic_algorithms.Chromosome
import genetic_algorithms.Population

/**
 * Набор алгоритмов для создания промежуточной популяции
 */
object SelectionMethods {
    /**
     *  Список доступных алгоритмов
     */
    enum class Types {
        TOURNAMENT,
        ROULETTE_WHEEL
    }

    // Стандартное количество особей, принимающих участие в турнире
    private const val DEFAULT_TOURNAMENT_SIZE = 2

    /**
     * Турнирный отбор
     * @param population Популяция, особи которой будут участвовать в турнире
     * @param onFitness Функция пригодности, на результатах работы которой будет принято решение, какая особь побеждает в турнире
     * @param tournamentSize Количество особей участвующих в одном турнире
     * @return Набор особей, победивших в турнире
     */
    fun <T> tournamentSelection(
        population: Population<T>,
        onFitness: (chromosome: Chromosome<T>) -> Double,
        tournamentSize: Int = DEFAULT_TOURNAMENT_SIZE
    ): Population<T> {
        // Количество особей, принимающих участие в турнире, не может быть больше их общего количества в популяции
        require(tournamentSize <= population.entities.size) {
            "The number of individuals participating in the tournament at the same time cannot be greater " +
                    "than the total number of individuals in the population"
        }

        val newEntities = arrayListOf<Chromosome<T>>()

        // пока размер промежуточной популяции не будет равен размеру исходной
        while (newEntities.size != population.entities.size) {
            // случайно выбираем несколько особей для участия в турнире
            val randomEntities = Array(tournamentSize) { population.entities.random() }
            // сортируем по убыванию значений функции пригодности
            randomEntities.sortByDescending(onFitness)
            // берём самую пригодную особь
            newEntities.add(randomEntities[0])
        }

        return Population(newEntities)
    }

    /**
     * Отбор методом рулетки
     * @param population Популяция, особи которой будут участвовать в турнире
     * @param onFitness Функция пригодности
     * @return Набор особей, которые были выбраны в результате рулеточного отбора
     */
    fun <T> rouletteWheelSelection(
        population: Population<T>, onFitness: (chromosome: Chromosome<T>) -> Double
    ): Population<T> {
        // Сумма значений пригодности всех особей в популяции
        val fitnessSum = population.entities.sumOf(onFitness)
        val newEntities = arrayListOf<Chromosome<T>>()

        while (newEntities.size != population.entities.size) {
            // выбираем случайное значение на рулетке
            val randomRouletteValue = getRandomBetween(0.0, fitnessSum)
            var sum = 0.0

            // смотрим, на какую особь указывает это значение и выбираем её в промежуточную популяцию
            for (entity in population.entities) {
                sum += onFitness(entity)
                if (sum >= randomRouletteValue) {
                    newEntities.add(entity)
                    break
                }
            }
        }

        return Population(newEntities)
    }

    private fun getRandomBetween(min: Double, max: Double) = min + Math.random() * (max - min)
}