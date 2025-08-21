using System.ComponentModel.DataAnnotations;
using System.Text.RegularExpressions;

namespace Learning_Management_System.Models.Attributes
{
    public class PasswordComplexityAttribute : ValidationAttribute
    {
        protected override ValidationResult? IsValid(object? value, ValidationContext validationContext)
        {
            if (value == null || string.IsNullOrEmpty(value.ToString()))
                return new ValidationResult("Password is required.");

            string password = value.ToString();

            var hasLetter = Regex.IsMatch(password, @"[a-zA-Z]");
            var hasDigit = Regex.IsMatch(password, @"\d");
            var hasSymbol = Regex.IsMatch(password, @"\W");
            var notHasSpace = Regex.IsMatch(password, @"\S");
            

            if (hasLetter && hasDigit && hasSymbol && notHasSpace)
                return ValidationResult.Success;
            else if (!hasLetter)
                return new ValidationResult("Password must contain at least one letter");
            else if (!hasDigit)
                return new ValidationResult("Password must contain at least one digit");
            else if (!hasSymbol)
                return new ValidationResult("Password must contain at least one Symbol");
            else if (!notHasSpace)
                return new ValidationResult("Password must not contain space");

            return new ValidationResult("Password must contain at least one letter, one digit, and one symbol.");
        }
    }
}
