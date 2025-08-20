using Microsoft.EntityFrameworkCore;
using Learning_Management_System.Models;

namespace Learning_Management_System.Context
{
    public class LMSContext : DbContext
    {
        public DbSet<Admin> Admins { get; set; }
        public DbSet<Instructor> Instructors { get; set; }
        public DbSet<Student> Students { get; set; }
        public DbSet<Course> Courses { get; set; }
        public DbSet<Enrollment> Enrollments { get; set; }
        public DbSet<Notification> Notifications { get; set; }
        public DbSet<Quiz> Quizzes { get; set; }
        public DbSet<Lesson> Lessons { get; set; }
        public DbSet<Assignment> Assignments { get; set; }
        public DbSet<Attendance> Attendances { get; set; }
        public DbSet<LessonVideo> LessonVideos { get; set; }
        public DbSet<CourseMaterial> CourseMaterials { get; set; }
        public DbSet<QuizAttempt> QuizAttempts { get; set; }
        public DbSet<TrueFalseQuestion> TrueFalseQuestions { get; set; }
        public DbSet<MCQQuestion> MCQQuestions { get; set; }
        public DbSet<Submission> Submissions { get; set; }
        public DbSet<StudentAnswer> StudentAnswers { get; set; }
        public DbSet<Choice> Choices { get; set; }
       
        public LMSContext() : base() { }
        public LMSContext(DbContextOptions<LMSContext> options) : base(options) { }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            base.OnConfiguring(optionsBuilder);
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.ApplyConfigurationsFromAssembly(typeof(LMSContext).Assembly);
        }

    }
}
