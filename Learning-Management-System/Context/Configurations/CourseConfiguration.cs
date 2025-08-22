using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class CourseConfiguration : IEntityTypeConfiguration<Course>
    {
        public void Configure(EntityTypeBuilder<Course> builder)
        {
            builder.HasKey(c => c.CourseId);

            builder.Property(c => c.Title).IsRequired().HasMaxLength(100);
            builder.Property(c => c.Description).HasMaxLength(600);

            builder.Property(c => c.Credits).IsRequired();
            builder.Property(c => c.CreateAt).IsRequired();

            builder.Property(c => c.CourseCategory).IsRequired();
            builder.Property(c => c.CourseLevel).IsRequired();

            builder.HasOne(c => c.Instructor)
                .WithMany(i => i.Courses)
                .HasForeignKey(c => c.InstructorId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasMany(c => c.Lessons)
                .WithOne()
                .HasForeignKey(l => l.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(c => c.CourseMaterials)
                .WithOne()
                .HasForeignKey(c => c.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(c => c.Quizzes)
                .WithOne()
                .HasForeignKey(c => c.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(c => c.Assignments)
                .WithOne()
                .HasForeignKey(c => c.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(c => c.Enrollments)
                .WithOne()
                .HasForeignKey(c => c.CourseId)
                .OnDelete(DeleteBehavior.Cascade);
                
        }
    }
}
