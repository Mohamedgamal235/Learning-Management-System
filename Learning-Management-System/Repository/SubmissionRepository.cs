using Learning_Management_System.Context;
using Learning_Management_System.Models;
using Learning_Management_System.Repository.Interfaces;

namespace Learning_Management_System.Repository
{
    public class SubmissionRepository : ISubmissionRepository
    {
        private readonly LMSContext _context;

        public SubmissionRepository(LMSContext context)
        {
            _context = context;
        }

        // Implementation will be added later
    }
}
