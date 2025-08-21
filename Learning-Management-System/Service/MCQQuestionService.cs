using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class MCQQuestionService : IMCQQuestionService
    {
        private readonly IMCQQuestionRepository _mcqQuestionRepository;

        public MCQQuestionService(IMCQQuestionRepository mcqQuestionRepository)
        {
            _mcqQuestionRepository = mcqQuestionRepository;
        }

        // Implementation will be added later
    }
}
