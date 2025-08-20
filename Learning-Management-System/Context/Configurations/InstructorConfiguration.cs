using Learning_Management_System.Models;

namespace Learning_Management_System.Context.Configurations
{
    public class InstructorConfiguration : IEntityTypeConfiguration<Instructor>
    {
        public void Configure(EntityTypeBuilder<Instructor> builder)
        {
            builder.HasKey(i => i.UserId);

            builder.Property(i => i.UserName).IsRequired()
                .HasMaxLength(60);

            builder.Property(i => i.Email).IsRequired()
                .HasMaxLength(100);

            builder.Property(i => i.Password).IsRequired().HasMaxLength(18);
            builder.Property(i => i.ConfirmedPassword).IsRequired().HasMaxLength(18);

            builder.HasMany(i => i.Courses)
                .WithOne(c => c.Instructor)
                .HasForeignKey(c => c.InstructorId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
