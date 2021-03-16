import org.litote.kmongo.*

fun incGrade(course: String, studentName: String) =
    mCourses.updateOne(
        and(
            Course::name eq course,
            Course::grades / Grade::studentName eq studentName
        ),
        inc(Course::grades.posOp / Grade::value, 1)
    )

fun addStudent(name: String, group: String, courses: List<String>) {
    val student = Student(name, group)
    mStudents.insertOne(student)
    mCourses.updateMany(
        Course::name `in` courses,
        push(Course::grades, Grade(student.id, student.name))
    )
}

fun setAllValue(course: String, value: Int) =
    mCourses.updateOne(
        Course::name eq course,
        setValue(Course::grades.allPosOp / Grade::value, value)
    )

fun main() {
    fillStudentsAndCourse()
    setGrade("Math", "Penny", 5)
    setGrade("Math", "Sheldon", 6)
    prettyPrintCursor(mCourses.find(Course::name eq "Math"))
    incGrade("Math", "Sheldon")
    prettyPrintCursor(mCourses.find(Course::name eq "Math"))
//    incGrade("Math","Raj")
//    prettyPrintCursor(mCourses.find(Course::name eq "Math"))
    addStudent("Stuart", "Boys", listOf("Math", "Phys"))
    addStudent("Emily", "Girls", listOf("Math", "Chem"))
    prettyPrintCursor(mCourses.find())
    setAllValue("Math", 3)
    prettyPrintCursor(mCourses.find(Course::name eq "Math"))
}