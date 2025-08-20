namespace Learning_Management_System.Models
{
    public class LessonVideo
    {
        public Guid LessonVideoId { get; set; }
        public string Title { get; set; }
        public string VideoUrl { get; set; }
        public TimeSpan Duration { get; set; }
        public int OrderIndex { get; set; }
        public DateTime UploadedAt { get; set; }

        public Guid LessonId { get; set; }
        public virtual Lesson? Lesson { get; set; }
    }
}
