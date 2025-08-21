using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class LessonVideoService : ILessonVideoService
    {
        private readonly ILessonVideoRepository _lessonVideoRepository;

        public LessonVideoService(ILessonVideoRepository lessonVideoRepository)
        {
            _lessonVideoRepository = lessonVideoRepository;
        }

        // Implementation will be added later
    }
}
