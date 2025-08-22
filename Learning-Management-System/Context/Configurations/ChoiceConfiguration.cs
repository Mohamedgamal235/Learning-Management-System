using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class ChoiceConfiguration : IEntityTypeConfiguration<Choice>
    {
        public void Configure(EntityTypeBuilder<Choice> builder)
        {
            builder.HasKey(c => c.ChoiceId);

            builder.Property(c => c.ChoiceText).IsRequired().HasMaxLength(500);
            builder.Property(c => c.IsCorrect).IsRequired();

            builder.HasOne(c => c.MCQQuestion)
                .WithMany(mq => mq.Choices)
                .HasForeignKey(c => c.QuestionId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
