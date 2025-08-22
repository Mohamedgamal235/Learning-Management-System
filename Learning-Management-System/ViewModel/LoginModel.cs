using Learning_Management_System.Models.Attributes;
using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.ViewModel
{
    public class LoginModel
    {
        [EmailAddress]
        [RegularExpression(@"^[a-zA-Z0-9._%+-]+@gmail\.com$", ErrorMessage = "Only Gmail Address is allowed.")]
        [Required] public string Email { get; set; }

        [DataType(DataType.Password)]
        public string Password { get; set; }
    }
}
