using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class SubmissionConfiguration : IEntityTypeConfiguration<Submission>
    {
        public void Configure(EntityTypeBuilder<Submission> builder)
        {
            builder.HasKey(s => new { s.AssignmentId, s.StudentId });

            builder.Property(s => s.FilePath).IsRequired().HasMaxLength(500);
            builder.Property(s => s.FileType).IsRequired();
            builder.Property(s => s.AnswerText).HasMaxLength(5000);
            builder.Property(s => s.SubmittedAt);
            builder.Property(s => s.Grade).HasDefaultValue(0);
            builder.Property(s => s.Feedback).HasMaxLength(1000);

            builder.HasCheckConstraint("CK_Submission_GradeRange", "Grade >= 0 AND Grade <= 100");

            builder.HasOne(s => s.Assignment)
                .WithMany(a => a.Submissions)
                .HasForeignKey(s => s.AssignmentId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasOne(s => s.Student)
                .WithMany(st => st.Submissions)
                .HasForeignKey(s => s.StudentId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
