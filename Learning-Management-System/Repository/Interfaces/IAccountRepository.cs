namespace Learning_Management_System.Repository.Interfaces
{
    public interface IAccountRepository
    {
        Task<IdentityResult> CreateUserAsync(AppUser user, string password);
        Task<AppUser?> FindByEmailAsync(string email);
        Task<SignInResult> PasswordSignInAsync(AppUser user , string password , bool lockoutOnFailure = false);
        Task AddStudentAsync(Student student);
        Task AddInstructorAsync(Instructor instructor);
        Task SignInAsync(AppUser user , bool RememberMe);
        Task SignOutAsync();
    }
}
