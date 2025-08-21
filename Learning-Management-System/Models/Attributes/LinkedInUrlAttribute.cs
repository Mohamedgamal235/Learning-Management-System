using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models.Attributes
{
    public class LinkedInUrlAttribute : ValidationAttribute
    {
        protected override ValidationResult? IsValid(object? value, ValidationContext validationContext)
        {
            if (value == null || string.IsNullOrWhiteSpace(value.ToString()))
                return ValidationResult.Success;

            string link = value.ToString();
            if (link.Contains("linkedin.com" , StringComparison.OrdinalIgnoreCase))
                return ValidationResult.Success;

            return new ValidationResult("Shoul Contain Linkedin.com");
        }
    }
}
