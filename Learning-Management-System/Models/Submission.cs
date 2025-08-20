namespace Learning_Management_System.Models
{
    public class Submission
    {
        public Guid AssignmentId { get; set; }
        public virtual Assignment? Assignment { get; set; }

        public Guid StudentId { get; set; }
        public virtual Student? Student { get; set; }
        
        public string FilePath { get; set; }
        public FileType FileType { get; set; }
        public string? AnswerText { get; set; }
        public DateTime? SubmittedAt { get; set; }
        public double Grade { get; set; }
        public string? Feedback { get; set; }
    }
}
