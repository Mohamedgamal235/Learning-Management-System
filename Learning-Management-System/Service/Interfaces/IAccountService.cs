namespace Learning_Management_System.Service.Interfaces
{
    public interface IAccountService
    {
        Task<IdentityResult> RegisterAsync(RegisterModel registerModel);
        Task<SignInResult> LoginAsync(LoginModel login);
        Task LogoutAsync();
    }
}
