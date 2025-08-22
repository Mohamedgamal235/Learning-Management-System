

namespace Learning_Management_System.Repository.Interfaces
{
    public interface IAccountRepository
    {
        Task<IdentityResult> CreateUserAsync(AppUser user, string password);
        Task<AppUser?> FindByNameAsync(string username);
        Task<bool> CheckPasswordAsync(AppUser user , string password);
        Task AddToRoleAsync(AppUser user, string role);
    }
}
