namespace Learning_Management_System.Models
{
    public abstract class Question
    {
        public Guid QuestionId { get; set; }
        public string QuestionText { get; set; }
        public QuestionType QuestionType { get; set; }
        public double Points { get; set; }
        
        public Guid QuizId { get; set; }
        public virtual Quiz? Quiz { get; set; }
    }
}
