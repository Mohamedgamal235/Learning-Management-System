namespace Learning_Management_System.Models
{
    public class Lesson
    {
        public Guid LessonId { get; set; }
        public string Title { get; set; }
        public int OrderIndex {  get; set; }
        public DateTime LessonDate { get; set; }

        public Guid CourseId { get; set; }
        public virtual Course? Course { get; set; }

        public virtual List<Attendance>? Attendances { get; set; }
        public virtual List<LessonVideo>? LessonVideos { get; set; }
        public virtual List<CourseMaterial>? CourseMaterials { get; set; }
    }
}
