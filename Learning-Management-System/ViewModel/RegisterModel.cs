using Learning_Management_System.Models.Attributes;
using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.ViewModel
{
    public class RegisterModel
    {
        [Required] public string FirstName { get; set; }
        [Required] public string MiddelName { get; set; }
        [Required] public string LastName { get; set; }

        [EmailAddress]
        [RegularExpression(@"^[a-zA-Z0-9._%+-]+@gmail\.com$", ErrorMessage = "Only Gmail Address is allowed.")]
        [Required] public string Email { get; set; }

        [DataType(DataType.Password)]
        [PasswordComplexity]
        public string Password { get; set; }

        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "Password not match")]
        public string ConfirmedPassword { get; set; }

        [Required]
        public Role Role { get; set; }
    }
}
