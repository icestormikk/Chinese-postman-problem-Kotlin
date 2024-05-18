package ant_colony

import kotlinx.serialization.Serializable

@Serializable
data class AntColonyAlgorithmConfiguration(
    // Количество итераций
    val iterationCount: Int,
    // количество феромонов
    val antCount: Int,
    // начальное количество феромонов на гранях
    val startPheromoneValue: Double,
    // Коэффициент близости
    val proximityCoefficient: Double,
    // Коэффициент, показывающий насколько сильно муравьи при переходе будут обращать внимание на кол-во феромона
    val alpha: Double,
    // то же самое, но для расстояния
    val beta: Double,
    // какой процент феромонов останется на грани после очередной итерации
    val remainingPheromoneRate: Double,
    // коэффициент Q (участвует в расчёте нового количества феромонов на путях)
    val q: Double,
    // Начальная вершина
    val startNodeId: String? = null
)