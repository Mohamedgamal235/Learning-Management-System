using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class QuizAttemptConfiguration : IEntityTypeConfiguration<QuizAttempt>
    {
        public void Configure(EntityTypeBuilder<QuizAttempt> builder)
        {
            builder.HasKey(qa => qa.QuizAttemptId);

            builder.Property(qa => qa.StartAt);
            builder.Property(qa => qa.SubmittedAt);
            builder.Property(qa => qa.Total).IsRequired();

            builder.HasCheckConstraint("CK_QuizAttempt_TotalRange", "Total >= 0 AND Total <= 100");

            builder.HasCheckConstraint("CK_QuizAttempt_SubmittedAfterStart", 
                "(StartAt IS NULL OR SubmittedAt IS NULL) OR (SubmittedAt > StartAt)");

            builder.HasOne(qa => qa.Quiz)
                .WithMany(q => q.QuizAttempts)
                .HasForeignKey(qa => qa.QuizId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(qa => qa.StudentAnswers)
                .WithOne(sa => sa.QuizAttempt)
                .HasForeignKey(sa => sa.QuizAttemptId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
