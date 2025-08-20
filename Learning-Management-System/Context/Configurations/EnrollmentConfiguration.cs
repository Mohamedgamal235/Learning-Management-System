using Learning_Management_System.Models;

namespace Learning_Management_System.Context.Configurations
{
    public class EnrollmentConfiguration : IEntityTypeConfiguration<Enrollment>
    {
        public void Configure(EntityTypeBuilder<Enrollment> builder)
        {
            builder.HasKey(e => new { e.CourseId, e.StudentId });

            builder.Property(e => e.EnrolledAt).IsRequired();
            builder.Property(e => e.ProgressPercent).IsRequired();
            builder.Property(e => e.AttendanceCount).IsRequired();

            builder.HasOne(e => e.Course)
                .WithMany(e => e.Enrollments)
                .HasForeignKey(e => e.CourseId)
                .OnDelete(DeleteBehavior.Restrict);

            builder.HasOne(e => e.Student)
                .WithMany(e => e.Enrollments)
                .HasForeignKey(e => e.StudentId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}
