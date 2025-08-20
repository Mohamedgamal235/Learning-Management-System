namespace Learning_Management_System.Models
{
    public class Quiz
    {
        public Guid QuizId { get; set; }
        public string Title { get; set; }
        public string? Description { get; set; }
        public DateTime StartDateTime { get; set; }
        public DateTime EndDateTime { get; set; }
        public int DurationMinutes { get; set; }

        public Guid CourseId { get; set; }
        public virtual Course? Course { get; set; }
        public virtual List<QuizAttempt>? QuizAttempts { get; set; }
        public virtual List<TrueFalseQuestion>? TrueFalseQuestions { get; set; }
        public virtual List<MCQQuestion>? MCQQuestions { get; set; }
        
    }
}
