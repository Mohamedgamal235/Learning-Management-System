using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class QuizConfiguration : IEntityTypeConfiguration<Quiz>
    {
        public void Configure(EntityTypeBuilder<Quiz> builder)
        {
            builder.HasKey(q => q.QuizId);

            builder.Property(q => q.Title).IsRequired().HasMaxLength(100);
            builder.Property(q => q.Description).HasMaxLength(500);
            builder.Property(q => q.StartDateTime).IsRequired();
            builder.Property(q => q.EndDateTime).IsRequired();
            builder.Property(q => q.DurationMinutes).IsRequired();

            builder.HasOne(q => q.Course)
                .WithMany(c => c.Quizzes)
                .HasForeignKey(q => q.CourseId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasMany(q => q.MCQQuestions)
                .WithOne(mq => mq.Quiz)
                .HasForeignKey(mq => mq.QuizId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasMany(q => q.TrueFalseQuestions)
                .WithOne(tf => tf.Quiz)
                .HasForeignKey(tf => tf.QuizId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasMany(q => q.QuizAttempts)
                .WithOne(qa => qa.Quiz)
                .HasForeignKey(qa => qa.QuizId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
