using Microsoft.VisualBasic.FileIO;

namespace Learning_Management_System.Models
{
    public class Assignment
    {
        public Guid AssignmentId { get; set; }
        public string Title { get; set; }
        public string Instructions { get; set; }
        public DateTime? CreatedAt { get; set; }
        public DateTime Deadline { get; set; }
        public string? FilePath { get; set; }  
        public FileType? FileType { get; set; }

        public Guid CourseId {  get; set; }
        public virtual Course? Course { get; set; }

        public virtual List<Submission>? Submissions { get; set; }
    }
}
