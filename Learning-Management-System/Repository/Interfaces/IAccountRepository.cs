namespace Learning_Management_System.Repository.Interfaces
{
    public interface IAccountRepository
    {
        Task<IdentityResult> CreateUserAsync(AppUser user, string password);
        Task<AppUser?> FindByEmailAsync(string email);
        Task<AppUser?> FindByNameAsync(string name);
    }
}
