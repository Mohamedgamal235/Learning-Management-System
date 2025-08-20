using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class MCQQuestionConfiguration : IEntityTypeConfiguration<MCQQuestion>
    {
        public void Configure(EntityTypeBuilder<MCQQuestion> builder)
        {
            builder.HasMany(mq => mq.Choices)
                .WithOne(mq => mq.MCQQuestion)
                .HasForeignKey(c => c.QuestionId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
