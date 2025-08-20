namespace Learning_Management_System.Models
{
    public class Student : User
    {
        public string? School { get; set; }
        public string? Headline { get; set; }
        public DateTime EnrollmentDate { get; set; }

        public virtual List<Submission>? Submissions { get; set; }
        public virtual List<Enrollment>? Enrollments { get; set; }
    }
}
