using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class CourseMaterialService : ICourseMaterialService
    {
        private readonly ICourseMaterialRepository _courseMaterialRepository;

        public CourseMaterialService(ICourseMaterialRepository courseMaterialRepository)
        {
            _courseMaterialRepository = courseMaterialRepository;
        }

        // Implementation will be added later
    }
}
