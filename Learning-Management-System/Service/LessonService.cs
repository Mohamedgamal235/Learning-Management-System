using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class LessonService : ILessonService
    {
        private readonly ILessonRepository _lessonRepository;

        public LessonService(ILessonRepository lessonRepository)
        {
            _lessonRepository = lessonRepository;
        }

        // Implementation will be added later
    }
}
