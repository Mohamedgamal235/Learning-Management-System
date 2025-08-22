namespace Learning_Management_System.Models
{
    public enum Role
    {
        Admin, 
        Instructor,
        Student
    }

    public enum QuestionType
    {
        MCQ , 
        TrueFalse ,
    }

    public enum NotificationType
    {
        Email,
        InApp
    }

    public enum CourseCategory
    {
        Programming,
        Mathematics,
        Design,
        Business,
        Science
    }

    public enum CourseLevel
    {
        Beginner,
        Intermediate,
        Advanced
    }

    public enum FileType
    {
        Pdf,
        Docx,
        Pptx,
        Zip,
        Rar,
        Txt,
        Other
    }

    public enum VideoType
    {
        Mp4,
        Avi,
        Mkv,
        Mov,
        YouTube,
        Vimeo
    }


    public class Enums
    {
    }
}
