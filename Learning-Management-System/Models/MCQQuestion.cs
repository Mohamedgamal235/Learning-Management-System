namespace Learning_Management_System.Models
{
    public class MCQQuestion : Question
    {
        public virtual List<Choice>? Choices { get; set; }
    }
}
