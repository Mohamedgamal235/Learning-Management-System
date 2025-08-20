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

            builder.Property(s => s.)

            builder.Property(i => i.Password).IsRequired().HasMaxLength(18);
            builder.Property(i => i.ConfirmedPassword).IsRequired().HasMaxLength(18);


            
        }
    }
}
