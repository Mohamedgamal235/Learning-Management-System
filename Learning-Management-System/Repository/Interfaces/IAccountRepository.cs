namespace Learning_Management_System.Repository.Interfaces
{
    public interface IAccountRepository
    {
        Task<IdentityResult> CreateUserAsync(AppUser user, string password);
        Task<AppUser?> FindByEmailAsync(string email);
        Task<AppUser?> FindByNameAsync(string name);
        Task<SignInResult> PasswordSignInAsync(AppUser user , string password , bool RememberMe);
        Task SignOutAsync();
    }
}
