
using Learning_Management_System.Models.Attributes;
using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models
{
    public abstract class User
    {
        public Guid UserId { get; set; }
        public string UserName { get; set; }

        [EmailAddress]
        public string Email { get; set; }

        [DataType(DataType.Password)]
        [PasswordComplexity]
        public string Password { get; set; }
        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "Password not match")]
        public string ConfirmedPassword { get; set; }

        public string? LinkedIn {  get; set; }
        public string? PhotoPath { get; set; }
        public Role Role { get; set; }

        public virtual List<Notification>? Notifications { get; set; }
    }
}
