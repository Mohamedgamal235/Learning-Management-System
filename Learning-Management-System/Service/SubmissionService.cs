using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class SubmissionService : ISubmissionService
    {
        private readonly ISubmissionRepository _submissionRepository;

        public SubmissionService(ISubmissionRepository submissionRepository)
        {
            _submissionRepository = submissionRepository;
        }

        // Implementation will be added later
    }
}
