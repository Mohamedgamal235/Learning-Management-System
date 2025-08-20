namespace Learning_Management_System.Models
{
    public class Choice
    {
        public Guid ChoiceId { get; set; }
        public string ChoiceText { get; set; }
        public bool IsCorrect { get; set; }
        
        public Guid QuestionId { get; set; }
        public virtual MCQQuestion? MCQQuestion { get; set; }
    }
}
