namespace Learning_Management_System.Models
{
    public class Attendance
    {
        public Guid AttendanceId { get; set; }
        public bool IsPresent { get; set; }
        public DateTime MarkedAt { get; set; }

        public Guid LessonId { get; set; }
        public virtual Lesson? Lesson { get; set; }

        public Guid StudentId { get; set; }
        public virtual Student? Student { get; set; } 
    }
}
