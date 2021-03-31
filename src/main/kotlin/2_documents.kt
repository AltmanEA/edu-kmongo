import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.litote.kmongo.*
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.util.*

@Serializable
data class Student(
    val name: String,
    val group: String,
    @Contextual val id: Id<Student> = newId()
)

@Serializable
data class Grade(
    val studentId: @Contextual Id<Student>,
    val studentName: String,
    val value: Int? = null,
    @Serializable(with = DateAsLongSerializer::class)
    val date: Date? = null,
)

@Serializable
data class Course(
    val name: String,
    val grades: List<Grade> = emptyList()
)

val json = Json { serializersModule = IdKotlinXSerializationModule }

val mStudents = mongoDatabase.getCollection<Student>().apply { drop() }
val mCourses = mongoDatabase.getCollection<Course>().apply { drop() }

fun fillStudentsAndCourse(fillCourse: Boolean = true): List<Student> {
    val students = listOf("Penny", "Amy").map { Student(it, "Girls") } +
            listOf("Sheldon", "Leonard", "Howard", "Raj").map { Student(it, "Boys") }
    mStudents.insertMany(students)
    if(fillCourse) {
        val courses = listOf("Math", "Phys", "History").map {
            Course(it, students.map { Grade(it.id, it.name) })
        }
        mCourses.insertMany(courses)
    }
    return students
}

fun setGrade(courseName: String, studentName: String, value: Int) =
    mCourses.updateOne(
        and(
            Course::name eq courseName,
            Course::grades / Grade::studentName eq studentName
        ),
        setValue(Course::grades.posOp / Grade::value, value)
    )

fun main() {

    println("\n --- Create study sheet --- \n")
    fillStudentsAndCourse()
    prettyPrintCursor(mStudents.find())
    prettyPrintCursor(mCourses.find())

    println("\n --- Set grade --- \n")
    setGrade("Math", "Penny", 5)
    setGrade("Math", "Sheldon", 6)
    prettyPrintCursor(mCourses.find(Course::name eq "Math"))

}


