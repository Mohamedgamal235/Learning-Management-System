namespace Learning_Management_System.Models
{
    public class Enrollment
    {
        public Guid StudentId { get; set; }
        public virtual Student? Student { get; set; }

        public Guid CourseId { get; set; }
        public virtual Course? Course { get; set; }

        public DateTime EnrolledAt { get; set; }
        public double ProgressPercent { get; set; }
        public double? Grade { get; set; }
        public DateTime? LastAccessed {  get; set; } 
        public int AttendanceCount { get; set; }
    }
}
