namespace Learning_Management_System.Models
{
    public class QuizAttempt
    {
        public Guid QuizAttemptId { get; set; }
        public DateTime? StartAt { get; set; }
        public DateTime? SubmittedAt { get; set; }
        public double Total {  get; set; }

        public Guid QuizId { get; set; }
        public virtual Quiz? Quiz { get; set; }
        public virtual List<StudentAnswer>? StudentAnswers { get; set; }
    }
}
