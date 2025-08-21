using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class CourseService : ICourseService
    {
        private readonly ICourseRepository _courseRepository;

        public CourseService(ICourseRepository courseRepository)
        {
            _courseRepository = courseRepository;
        }

        // Implementation will be added later
    }
}
