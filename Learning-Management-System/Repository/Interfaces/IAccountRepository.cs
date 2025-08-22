namespace Learning_Management_System.Repository.Interfaces
{
    public interface IAccountRepository
    {
        Task<IdentityResult> CreateUserAsync(AppUser user, string password);
        Task<AppUser?> FindByEmailAsync(string email);
        Task<SignInResult> PasswordSignInAsync(AppUser user , string password , bool RememberMe);
        Task AddStudentAsync(Student student);
        Task AddInstructorAsync(Instructor instructor);
        Task SignOutAsync();
    }
}
