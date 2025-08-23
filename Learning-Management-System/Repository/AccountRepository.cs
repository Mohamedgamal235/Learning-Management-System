using Learning_Management_System.Context;
using Learning_Management_System.Repository.Interfaces;
using Microsoft.AspNetCore.Authentication;
using Microsoft.EntityFrameworkCore.Internal;

namespace Learning_Management_System.Repository
{
    public class AccountRepository : IAccountRepository
    {
        private readonly UserManager<AppUser> userManager;
        private readonly SignInManager<AppUser> signInManager;
        private readonly LMSContext context;

        public AccountRepository(UserManager<AppUser> userManager , SignInManager<AppUser> signInManager , LMSContext context)
        {
            this.userManager = userManager;
            this.signInManager = signInManager;
            this.context = context;
        }
        public async Task<IdentityResult> CreateUserAsync(AppUser user, string password)
        {
             return await userManager.CreateAsync(user, password);
        }

        public async Task<AppUser?> FindByEmailAsync(string email)
        {
            return await userManager.FindByEmailAsync(email);
        }

        public async Task<SignInResult> PasswordSignInAsync(AppUser user, string password, bool lockoutOnFailure = false)
        {
            return await signInManager.CheckPasswordSignInAsync(user, password , false);
        }

        public async Task SignOutAsync()
        {
            await signInManager.SignOutAsync();
        }

        public async Task AddInstructorAsync(Instructor instructor)
        {
            context.Instructors.Add(instructor);
            await context.SaveChangesAsync();
        }

        public async Task AddStudentAsync(Student student)
        {
            context.Students.Add(student);
            await context.SaveChangesAsync();
        }

        public async Task SignInAsync(AppUser user, bool RememberMe)
        {
            if (RememberMe)
                await signInManager.SignInAsync(user, new AuthenticationProperties
                {
                    IsPersistent = true,
                    ExpiresUtc = DateTimeOffset.UtcNow.AddDays(2)
                });
            else
                await signInManager.SignInAsync(user, RememberMe);
        }
    }
}
