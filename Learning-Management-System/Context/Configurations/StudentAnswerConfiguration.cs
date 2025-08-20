using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class StudentAnswerConfiguration : IEntityTypeConfiguration<StudentAnswer>
    {
        public void Configure(EntityTypeBuilder<StudentAnswer> builder)
        {
            builder.HasKey(sa => sa.StudentAnswerId);

            builder.Property(sa => sa.SelectedChoiceId);
            builder.Property(sa => sa.SelectedBool);
            builder.Property(sa => sa.IsCorrect).IsRequired();

            builder.HasOne(sa => sa.Question)
                .WithMany()
                .HasForeignKey(sa => sa.QuestionId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasOne(sa => sa.QuizAttempt)
                .WithMany(qa => qa.StudentAnswers)
                .HasForeignKey(sa => sa.QuizAttemptId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
