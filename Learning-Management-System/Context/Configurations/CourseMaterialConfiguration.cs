using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class CourseMaterialConfiguration : IEntityTypeConfiguration<CourseMaterial>
    {
        public void Configure(EntityTypeBuilder<CourseMaterial> builder)
        {
            builder.HasKey(cm => cm.CourseMaterialId);

            builder.Property(cm => cm.Title).IsRequired().HasMaxLength(100);
            builder.Property(cm => cm.Description).HasMaxLength(500);
            builder.Property(cm => cm.FilePath).IsRequired().HasMaxLength(500);
            builder.Property(cm => cm.FileName).IsRequired().HasMaxLength(255);
            builder.Property(cm => cm.ContentType).IsRequired().HasMaxLength(100);
            builder.Property(cm => cm.FileSize).IsRequired();
            builder.Property(cm => cm.UpdloadedAt).IsRequired();
            builder.Property(cm => cm.UploadedBy).IsRequired();

            builder.HasOne(cm => cm.Lesson)
                .WithMany(l => l.CourseMaterials)
                .HasForeignKey(cm => cm.LessonId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasOne(cm => cm.Course)
                .WithMany(c => c.CourseMaterials)
                .HasForeignKey(cm => cm.CourseId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
