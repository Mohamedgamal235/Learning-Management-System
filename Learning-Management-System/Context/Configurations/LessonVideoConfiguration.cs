using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class LessonVideoConfiguration : IEntityTypeConfiguration<LessonVideo>
    {
        public void Configure(EntityTypeBuilder<LessonVideo> builder)
        {
            builder.HasKey(lv => lv.LessonVideoId);

            builder.Property(lv => lv.Title).IsRequired().HasMaxLength(100);
            builder.Property(lv => lv.VideoUrl).HasMaxLength(500).IsRequired();
            builder.Property(lv => lv.Duration).IsRequired();
            builder.Property(lv => lv.OrderIndex).IsRequired();
            builder.Property(lv => lv.UploadedAt).IsRequired();

            // Ensure OrderIndex is positive
            builder.HasCheckConstraint("CK_LessonVideo_OrderIndexPositive", "OrderIndex > 0");

            // Relationship with Lesson
            builder.HasOne(lv => lv.Lesson)
                .WithMany(l => l.LessonVideos)
                .HasForeignKey(lv => lv.LessonId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
