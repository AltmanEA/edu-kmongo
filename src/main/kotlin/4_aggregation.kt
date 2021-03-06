import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import java.util.*

fun fillStudentAndCourseWithGrades() {
    val students = fillStudentsAndCourse(false)
    val values = listOf(
        listOf(5, 5, 5, 4, 4, 4),
        listOf(4, 4, 5, 4, 4, 4),
        listOf(5, 5, 5, 4, 3, 3)
    )
    val courses = listOf("Math", "Phys", "History")
        .mapIndexed { indexCourse, course ->
            Course(course,
                students.mapIndexed { indexStudent, student ->
                    Grade(
                        student.id, student.name,
                        values[indexCourse][indexStudent], Date(System.currentTimeMillis())
                    )
                }
            )
        }
    mCourses.insertMany(courses)
}

fun main() {
    println("\n --- Create filled study sheet --- \n")
    fillStudentAndCourseWithGrades()
    prettyPrintCursor(mStudents.find())
    prettyPrintCursor(mCourses.find())

    println("\n\n --- Analyse study sheet --- \n\n")

    println("\n --- Math --- \n")
    prettyPrintCursor(
        mCourses.aggregate<Course>(
            match(
                Course::name eq "Math"
            ),
        )
    )
    println("\n --- Unwind --- \n")
    @Serializable
    class UnwindCourse(
        val name: String,
        val grades: Grade
    )
    prettyPrintCursor(
        mCourses.aggregate<UnwindCourse>(
            unwind("\$grades")
        )
    )
    println("\n --- Unwind + Match--- \n")
    prettyPrintCursor(
        mCourses.aggregate<UnwindCourse>(
            unwind("\$grades"),
            match(UnwindCourse::grades / Grade::studentName eq "Penny")
        )
    )
    println("\n --- Unwind + Match + Project --- \n")
    @Serializable
    data class StudentGrade(
        val value: Int? = null,
        @Serializable(with = DateAsLongSerializer::class)
        val date: Date? = null,
    )

    @Serializable
    data class UnwindStudentCourse(
        val name: String,
        val grades: StudentGrade
    )
    prettyPrintCursor(
        mCourses.aggregate<UnwindStudentCourse>(
            unwind("\$grades"),
            match(UnwindCourse::grades / Grade::studentName eq "Penny"),
            project(
                UnwindCourse::name,
                UnwindCourse::grades / Grade::value,
                UnwindCourse::grades / Grade::date
            )
        )
    )

    println("\n --- + Sort + Limit --- \n")
    prettyPrintCursor(
        mCourses.aggregate<UnwindStudentCourse>(
            unwind("\$grades"),
            match(UnwindCourse::grades / Grade::studentName eq "Penny"),
            project(
                UnwindCourse::name,
                UnwindCourse::grades / Grade::value,
                UnwindCourse::grades / Grade::date
            ),
            sort(UnwindStudentCourse::grades / StudentGrade::value eq 1),
            limit(2)
        )
    )

    println("\n --- Accumulator --- \n")
    @Serializable
    data class Result(
        val _id: String,
        val grades: Int
    )
    prettyPrintCursor(
        mCourses.aggregate<Result>(
            unwind("\$grades"),
            group(
                UnwindCourse::grades / Grade::studentName,
                Result::grades sum UnwindCourse::grades / Grade::value
            )
        )
    )
}

