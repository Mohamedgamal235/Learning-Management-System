using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class StudentAnswerService : IStudentAnswerService
    {
        private readonly IStudentAnswerRepository _studentAnswerRepository;

        public StudentAnswerService(IStudentAnswerRepository studentAnswerRepository)
        {
            _studentAnswerRepository = studentAnswerRepository;
        }

        // Implementation will be added later
    }
}
