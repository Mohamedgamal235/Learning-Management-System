using Learning_Management_System.Context;
using Learning_Management_System.Models;
using Learning_Management_System.Repository.Interfaces;

namespace Learning_Management_System.Repository
{
    public class InstructorRepository : IInstructorRepository
    {
        private readonly LMSContext _context;

        public InstructorRepository(LMSContext context)
        {
            _context = context;
        }

        // Implementation will be added later
    }
}
