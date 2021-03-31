import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import java.util.*

@Serializable
data class StudentB(
    val name: String,
    val group: String,
    val grades: List<GradeB> = emptyList()

)

@Serializable
data class GradeB(
    val courseId: @Contextual Id<CourseB>,
    val courseName: String,
    val value: Int? = null,
    @Serializable(with = DateAsLongSerializer::class)
    val date: Date? = null,
)

@Serializable
data class CourseB(
    val name: String,
    @Contextual val id: Id<CourseB> = newId()
)

val mStudentsB = mongoDatabase.getCollection<StudentB>("students_b").apply { drop() }
val mCoursesB = mongoDatabase.getCollection<CourseB>("courses_b").apply { drop() }

val values = listOf(
    listOf(5, 5, 5, 4, 4, 4),
    listOf(4, 4, 5, 4, 4, 4),
    listOf(5, 5, 5, 4, 3, 3)
)

fun main() {
    val coursesB = listOf("Math", "Phys", "History")
        .map {
            CourseB(it)
        }
    mCoursesB.insertMany(coursesB)
    val studentsB =
        (listOf("Penny", "Amy").map { it to "Girls" } +
                listOf("Sheldon", "Leonard", "Howard", "Raj").map { it to "Boys" })
            .mapIndexed { indexStudent, student ->
                StudentB(
                    student.first,
                    student.second,
                    coursesB.mapIndexed { indexCourse, course ->
                        GradeB(
                            course.id,
                            course.name,
                            values[indexCourse][indexStudent],
                            Date(System.currentTimeMillis())
                        )
                    }
                )
            }
    mStudentsB.insertMany(studentsB)

    prettyPrintCursor(mStudentsB.find())
    prettyPrintCursor(mCoursesB.find())

    prettyPrintCursor(
        mStudentsB.aggregate<StudentB>(
            match(
                not(StudentB::grades elemMatch (GradeB::value lte 3))
            )
        )
    )

    @Serializable
    class UnwindStudentB(
        val name: String,
        val group: String,
        val grades: GradeB
    )
    prettyPrintCursor(
        mStudentsB.aggregate<UnwindStudentB>(
            match(
                not(StudentB::grades elemMatch (GradeB::value lte 3))
            ),
            unwind("\$grades")
        )
    )

    @Serializable
    data class UnwindStudentCourseB(
        val name: String,
        val courseName: String,
        val gradesValue: Int
    )
    prettyPrintCursor(
        mStudentsB.aggregate<UnwindStudentCourseB>(
            match(
                not(StudentB::grades elemMatch (GradeB::value lte 3))
            ),
            unwind("\$grades"),
            project(
                UnwindStudentCourseB::name from UnwindStudentB::name,
                UnwindStudentCourseB::courseName from UnwindStudentB::grades / GradeB::courseName,
                UnwindStudentCourseB::gradesValue from UnwindStudentB::grades / GradeB::value,
            )
        )
    )

    prettyPrintCursor(
        mStudentsB.aggregate<UnwindStudentCourseB>(
            match(
                not(StudentB::grades elemMatch (GradeB::value lte 3))
            ),
            unwind("\$grades"),
            project(
                UnwindStudentCourseB::name from UnwindStudentB::name,
                UnwindStudentCourseB::courseName from UnwindStudentB::grades / GradeB::courseName,
                UnwindStudentCourseB::gradesValue from UnwindStudentB::grades / GradeB::value,
            ),
            group(
                UnwindStudentCourseB::name,
                UnwindStudentCourseB::courseName sum UnwindStudentCourseB::courseName,
                UnwindStudentCourseB::gradesValue sum UnwindStudentCourseB::gradesValue
            )
        )
    )

}