using System.Reflection;

namespace Learning_Management_System.Models
{
    public class Notification
    {
        public Guid NotificationId { get; set; }
        public string Title { get; set; }
        public string Message { get; set; }
        public bool IsReaded { get; set; }
        public DateTime CreatedAt { get; set; }
        public NotificationType Type { get; set; }

        public Guid UserId { get; set; }
        public virtual AppUser? User { get; set; }
    }
}
