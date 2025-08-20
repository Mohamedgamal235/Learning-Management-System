namespace Learning_Management_System.Models
{
    public class Course
    {
        public Guid CourseId { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public int Credits { get; set; }
        public DateTime CreateAt { get; set; }

        public Guid InstructorId { get; set; }
        public virtual Instructor? Instructor { get; set; }

        public virtual List<Lesson>? Lessons { get; set; }
        public virtual List<CourseMaterial>? CourseMaterials { get; set; }
        public virtual List<Quiz>? Quizzes { get; set; }
        public virtual List<Assignment>? Assignments { get; set; }
        public virtual List<Enrollment>? Enrollments { get; set; }
    }
}
