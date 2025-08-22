using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class AssignmentConfiguration : IEntityTypeConfiguration<Assignment>
    {
        public void Configure(EntityTypeBuilder<Assignment> builder)
        {
            builder.HasKey(a => a.AssignmentId);

            builder.Property(a => a.Title).IsRequired().HasMaxLength(100);
            builder.Property(a => a.Instructions).IsRequired().HasMaxLength(2000);
            builder.Property(a => a.CreatedAt);
            builder.Property(a => a.Deadline).IsRequired();
            builder.Property(a => a.FilePath).HasMaxLength(500);
            builder.Property(a => a.FileType);

            builder.HasCheckConstraint("CK_Assignment_DeadlineFuture", "Deadline > CreatedAt OR CreatedAt IS NULL");

            builder.HasOne(a => a.Course)
                .WithMany(c => c.Assignments)
                .HasForeignKey(a => a.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(a => a.Submissions)
                .WithOne(s => s.Assignment)
                .HasForeignKey(s => s.AssignmentId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
