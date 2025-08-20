using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class LessonConfiguration : IEntityTypeConfiguration<Lesson>
    {
        public void Configure(EntityTypeBuilder<Lesson> builder)
        {
            builder.HasKey(l => l.LessonId);

            builder.Property(l => l.Title).IsRequired().HasMaxLength(100);
            builder.Property(l => l.OrderIndex).IsRequired();
            builder.Property(l => l.LessonDate).IsRequired();

            builder.HasCheckConstraint("CK_Lesson_OrderIndexPositive", "OrderIndex > 0");

            builder.HasOne(l => l.Course)
                .WithMany(c => c.Lessons)
                .HasForeignKey(l => l.CourseId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(l => l.Attendances)
                .WithOne(a => a.Lesson)
                .HasForeignKey(a => a.LessonId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(l => l.LessonVideos)
                .WithOne(lv => lv.Lesson)
                .HasForeignKey(lv => lv.LessonId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(l => l.CourseMaterials)
                .WithOne(cm => cm.Lesson)
                .HasForeignKey(cm => cm.LessonId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
