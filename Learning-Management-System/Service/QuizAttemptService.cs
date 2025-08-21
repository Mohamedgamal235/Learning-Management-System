using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class QuizAttemptService : IQuizAttemptService
    {
        private readonly IQuizAttemptRepository _quizAttemptRepository;

        public QuizAttemptService(IQuizAttemptRepository quizAttemptRepository)
        {
            _quizAttemptRepository = quizAttemptRepository;
        }

        // Implementation will be added later
    }
}
