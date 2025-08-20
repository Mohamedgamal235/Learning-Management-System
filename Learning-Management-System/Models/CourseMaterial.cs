namespace Learning_Management_System.Models
{
    public class CourseMaterial
    {
        public Guid CourseMaterialId { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string FilePath { get; set; }
        public string FileName { get; set; }
        public string ContentType { get; set; }
        public long FileSize { get; set; }
        public DateTime UpdloadedAt { get; set; }
        public Guid UploadedBy { get; set; }

        public Guid LessonId { get; set; }
        public virtual Lesson? Lesson { get; set; }

        public Guid CourseId { get; set; }
        public virtual Course? Course { get; set; }
    }
}
