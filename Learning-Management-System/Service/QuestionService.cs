using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class QuestionService : IQuestionService
    {
        private readonly IQuestionRepository _questionRepository;

        public QuestionService(IQuestionRepository questionRepository)
        {
            _questionRepository = questionRepository;
        }

        // Implementation will be added later
    }
}
