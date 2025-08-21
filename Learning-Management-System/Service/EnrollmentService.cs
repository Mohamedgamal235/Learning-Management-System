using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class EnrollmentService : IEnrollmentService
    {
        private readonly IEnrollmentRepository _enrollmentRepository;

        public EnrollmentService(IEnrollmentRepository enrollmentRepository)
        {
            _enrollmentRepository = enrollmentRepository;
        }

        // Implementation will be added later
    }
}
