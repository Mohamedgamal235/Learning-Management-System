using Learning_Management_System.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Learning_Management_System.Context.Configurations
{
    public class UserConfiguration : IEntityTypeConfiguration<AppUser>
    {
        public void Configure(EntityTypeBuilder<AppUser> builder)
        {
            builder.HasKey(u => u.Id);

            builder.Property(u => u.UserName).IsRequired().HasMaxLength(50);
            builder.Property(u => u.Email).IsRequired().HasMaxLength(100);
            builder.Property(u => u.Password).IsRequired().HasMaxLength(255);
            builder.Property(u => u.ConfirmedPassword).IsRequired().HasMaxLength(255);
            builder.Property(u => u.LinkedIn).HasMaxLength(200);
            builder.Property(u => u.PhotoPath).HasMaxLength(500);
            builder.Property(u => u.Role).IsRequired();

            builder.HasIndex(u => u.Email).IsUnique();

            builder.HasIndex(u => u.UserName).IsUnique();

            builder.HasDiscriminator(u => u.Role)
                .HasValue<Admin>(Role.Admin)
                .HasValue<Instructor>(Role.Instructor)
                .HasValue<Student>(Role.Student);

            builder.HasMany(u => u.Notifications)
                .WithOne()
                .HasForeignKey("UserId")
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}