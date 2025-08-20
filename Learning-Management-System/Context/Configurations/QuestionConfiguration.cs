using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class QuestionConfiguration : IEntityTypeConfiguration<Question>
    {
        public void Configure(EntityTypeBuilder<Question> builder)
        {
            builder.HasKey(q => q.QuestionId);

            builder.Property(q => q.QuestionText).IsRequired().HasMaxLength(1000);
            builder.Property(q => q.QuestionType).IsRequired();
            builder.Property(q => q.Points).IsRequired();

            // Configure inheritance
            builder.HasDiscriminator(q => q.QuestionType)
                .HasValue<MCQQuestion>(QuestionType.MCQ)
                .HasValue<TrueFalseQuestion>(QuestionType.TrueFalse);

            builder.HasOne(q => q.Quiz)
                .WithMany()
                .HasForeignKey(q => q.QuizId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
