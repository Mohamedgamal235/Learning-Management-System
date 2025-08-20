
namespace Learning_Management_System.Models
{
    public abstract class User
    {
        public Guid UserId { get; set; }
        public string UserName { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string ConfirmedPassword { get; set; }
        public string? LinkedIn {  get; set; }
        public string? PhotoPath { get; set; }
        public Role Role { get; set; }

        public virtual List<Notification>? Notifications { get; set; }
    }
}
