using Learning_Management_System.Repository.Interfaces;

namespace Learning_Management_System.Repository
{
    public class AccountRepository : IAccountRepository
    {
        private readonly UserManager<AppUser> userManager;

        public AccountRepository(UserManager<AppUser> userManager)
        {
            this.userManager = userManager;
        }
        public async Task<IdentityResult> CreateUserAsync(AppUser user, string password)
        {
             return await userManager.CreateAsync(user, password);
        }

        public async Task<AppUser?> FindByEmailAsync(string email)
        {
            return await userManager.FindByEmailAsync(email);
        }

        public async Task<AppUser?> FindByNameAsync(string name)
        {
            return await userManager.FindByNameAsync(name);
        }
    }
}
