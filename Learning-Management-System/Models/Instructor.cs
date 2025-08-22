namespace Learning_Management_System.Models
{
    public class Instructor : AppUser
    {
        public string? JopTitle { get; set; }
        public double? Salary { get; set; }  

        public virtual List<Course>? Courses { get; set; }
    }
}
