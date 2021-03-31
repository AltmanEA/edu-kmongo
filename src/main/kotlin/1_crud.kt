import com.mongodb.client.FindIterable
import kotlinx.serialization.Serializable
import org.bson.conversions.Bson
import org.litote.kmongo.*


@Serializable
data class Scholar(val name: String, val group: String)

val scholars = mongoDatabase.getCollection<Scholar>().apply { drop() }

fun main() {

    println(" --- CREATE ---")
    scholars.insertOne(Scholar("Penny", "Girls"))
    scholars.insertOne("{'name': 'Amy', 'group': 'Girls' }")
    scholars.insertMany(
        arrayOf("Sheldon", "Leonard", "Howard", "Raj").map { Scholar(it, "Boys") }
    )

    println("\n --- READ --- \n")
    println(scholars.find().toList())
    println(scholars.findOne("{name: 'Penny'}"))
    val foundStudents: FindIterable<Scholar> = scholars.find(Scholar::group eq "Boys").sort("{'name': 1}")
    prettyPrintCursor(foundStudents)

    println("\n --- UPDATE --- \n")
    var jsons = "--- Jsons: \n"
    scholars.updateOne(
        (Scholar::name eq "Amy").also { bson: Bson -> jsons += "query: ${bson.json}\n" },
        setValue(Scholar::name, "Amy Farrah Fowler").also { jsons +="update: ${it.json}\n" }
    )
    println(jsons)
    prettyPrintCursor(scholars.find())
    scholars.updateMany("{'group' : 'Boys'}", "{'\$set' : {'group' : 'Man'}}")
    prettyPrintCursor(scholars.find())

    println("\n --- DELETE --- \n")
    scholars.deleteOne(Scholar::name eq "Penny")
    prettyPrintCursor(scholars.find())
    scholars.deleteMany(Scholar::group eq "Man")
    prettyPrintCursor(scholars.find())
    scholars.drop()
    prettyPrintCursor(scholars.find())
}

