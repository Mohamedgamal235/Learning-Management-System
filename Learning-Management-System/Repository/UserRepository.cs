using Learning_Management_System.Context;
using Learning_Management_System.Models;
using Learning_Management_System.Repository.Interfaces;

namespace Learning_Management_System.Repository
{
    public class UserRepository : IUserRepository
    {
        private readonly LMSContext _context;

        public UserRepository(LMSContext context)
        {
            _context = context;
        }

        // Implementation will be added later
    }
}
