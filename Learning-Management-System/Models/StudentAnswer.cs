namespace Learning_Management_System.Models
{
    public class StudentAnswer
    {
        public Guid StudentAnswerId { get; set; }
        public Guid? SelectedChoiceId { get; set; }
        public bool? SelectedBool { get; set; }
        public bool IsCorrect { get; set; }

        public Guid QuestionId { get; set; }
        public virtual Question? Question { get; set; }
        public Guid QuizAttemptId { get; set; }
        public virtual QuizAttempt? QuizAttempt { get; set; }
    }
}
