import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.gt

fun main() {

    val population = database.getCollection<Population>().apply { drop() }

    println("\n --- Import population from file --- \n")
    // got from https://datahub.io/core/population
    val populationJson = Population::class.java.getResource("population_json.json")
        .readText()
    val populationCol = Json.decodeFromString(
        ListSerializer(Population.serializer()),
        populationJson
    )
    println(populationCol.size)
    population.insertMany(populationCol)

    prettyPrintCursor(
        population
            .find(Population::year eq 2000)
            .sort(Population::value eq -1)
            .limit(20)
    )

    val bsonRequest = and(Population::year eq 2018, Population::value gt 1_000_000_000)
    prettyPrintCursor(population.find(bsonRequest))
    prettyPrintExplain(population.find(bsonRequest))
}
