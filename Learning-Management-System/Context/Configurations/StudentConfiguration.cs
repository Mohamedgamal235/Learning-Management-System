using Learning_Management_System.Models;

namespace Learning_Management_System.Context.Configurations
{
    public class StudentConfiguration : IEntityTypeConfiguration<Student>
    {
        public void Configure(EntityTypeBuilder<Student> builder)
        {
            builder.HasKey(i => i.UserId);

            builder.Property(i => i.UserName).IsRequired()
                .HasMaxLength(60);

            builder.Property(i => i.Email).IsRequired()
                .HasMaxLength(100);

            builder.Property(s => s.EnrollmentDate);

            builder.Property(i => i.Password).IsRequired().HasMaxLength(18);
            builder.Property(i => i.ConfirmedPassword).IsRequired().HasMaxLength(18);
            
            builder.HasMany(s => s.Submissions)
                .WithOne(s => s.Student)
                .HasForeignKey(s => s.StudentId)
                .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(s => s.Enrollments)
                .WithOne(s => s.Student)
                .HasForeignKey(s => s.StudentId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
