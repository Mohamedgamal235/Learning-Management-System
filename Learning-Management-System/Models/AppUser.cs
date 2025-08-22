
using Learning_Management_System.Models.Attributes;
using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models
{
    public abstract class AppUser : IdentityUser<Guid>
    {
        [Required]
        public string FirstName { get; set; }

        [Required]
        public string MiddelName { get; set; }

        [Required]
        public string LastName { get; set; }

        [EmailAddress]
        [RegularExpression(@"^[a-zA-Z0-9._%+-]+@gmail\.com$" , ErrorMessage = "Only Gmail Address is allowed.")]
        public string Email { get; set; }

        [DataType(DataType.Password)]
        [PasswordComplexity]
        public string Password { get; set; }

        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "Password not match")]
        public string ConfirmedPassword { get; set; }


        [DataType(DataType.Url)]
        [LinkedInUrl]
        public string? LinkedIn {  get; set; }

        [DataType(DataType.Upload)]
        public string? PhotoPath { get; set; }

        [Required]
        public Role Role { get; set; }

        public virtual List<Notification>? Notifications { get; set; }
    }
}
