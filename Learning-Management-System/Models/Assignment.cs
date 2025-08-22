using Microsoft.VisualBasic.FileIO;
using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models
{
    public class Assignment
    {
        public Guid AssignmentId { get; set; }

        [Required]
        public string Title { get; set; }

        [Required]
        public string Instructions { get; set; }


        public DateTime? CreatedAt { get; set; }

        [Required]
        public DateTime Deadline { get; set; }


        public string? FilePath { get; set; }  

        [Required]
        public FileType FileType { get; set; }

        public Guid CourseId {  get; set; }
        public virtual Course? Course { get; set; }

        public virtual List<Submission>? Submissions { get; set; }
    }
}
