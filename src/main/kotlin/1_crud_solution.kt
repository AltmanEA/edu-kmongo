import com.mongodb.client.model.UpdateOptions
import kotlinx.serialization.Serializable
import org.litote.kmongo.*

fun main() {

    @Serializable
    data class Count(val name: String, val value: Int = 0)

    val counts = database.getCollection<Count>().apply { drop() }

    counts.insertMany(
        listOf("Tables", "Figures", "Equations")
            .map { Count(it) }
    )

    fun incCount(countName: String) =
        counts.updateOne(
            Count::name eq countName,
            inc(Count::value, 1)
        )

    incCount("Tables")
    incCount("Tables")
    incCount("Equations")
    incCount("Listings")
    prettyPrintCursor(counts.find())

    fun incOrCreateCount(countName: String) =
        counts.updateOne(
            Count::name eq countName,
            inc(Count::value, 1),
            UpdateOptions().upsert(true)
        )

    incOrCreateCount("Tables")
    incOrCreateCount("Listings")
    incOrCreateCount("Listings")
    prettyPrintCursor(counts.find())

    prettyPrintCursor(counts.find(
        Count::value gt 0
    ))

    prettyPrintCursor(counts.find(
        and(
            Count::value gte 1,
            Count::value lte 2
        )
    ))

}