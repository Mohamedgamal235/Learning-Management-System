using System.ComponentModel.DataAnnotations;

namespace Learning_Management_System.Models.Attributes
{
    public class ValidationVideoAttribute : ValidationAttribute
    {
        private readonly string[] _allowedExtensions = { ".mp4", ".mov", ".avi", ".mkv" };
        protected override ValidationResult? IsValid(object? value, ValidationContext validationContext)
        {
            if(value == null || string.IsNullOrWhiteSpace(value.ToString()))
                return new ValidationResult("Video file is required.");
            
            var videoPath = value.ToString();
            var extention = Path.GetExtension(videoPath);

            if (!_allowedExtensions.Contains(extention))
                return new ValidationResult($"Invalid video format. Allowed formats: {string.Join(", ", _allowedExtensions)}");

            return ValidationResult.Success;

        }
    }
}
