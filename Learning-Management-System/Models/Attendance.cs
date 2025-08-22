using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models
{
    public class Attendance
    {
        public Guid AttendanceId { get; set; }

        [Required]
        public bool IsPresent { get; set; }
        [Required]
        public DateTime MarkedAt { get; set; }

        public Guid LessonId { get; set; }
        public virtual Lesson? Lesson { get; set; }

        public Guid StudentId { get; set; }
        public virtual Student? Student { get; set; } 
    }
}
