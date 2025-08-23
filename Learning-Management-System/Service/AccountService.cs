using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;
using Microsoft.AspNetCore.Identity;

namespace Learning_Management_System.Service
{
    public class AccountService : IAccountService
    {
        private readonly IAccountRepository accountRepository;

        public AccountService(IAccountRepository accountRepository)
        {
            this.accountRepository = accountRepository;
        }
        public async Task<SignInResult> LoginAsync(LoginModel login)
        {
            var user = await accountRepository.FindByEmailAsync(login.Email);
            if (user == null)
                return SignInResult.Failed;

            var passwordCheck = await accountRepository.PasswordSignInAsync(user, login.Password);
            if (!passwordCheck.Succeeded)
                return passwordCheck;

            await accountRepository.SignInAsync(user, login.RememberMe); // create on cookie 

            return SignInResult.Success;
        }

        public async Task LogoutAsync()
        {
            await accountRepository.SignOutAsync();
        }

        public async Task<IdentityResult> RegisterAsync(RegisterModel registerModel)
        {
            AppUser user; 
            if (registerModel.Role == Role.Student)
            {
                user = new Student
                {
                    FirstName = registerModel.FirstName,
                    MiddelName = registerModel.MiddelName,
                    LastName = registerModel.LastName,
                    Email = registerModel.Email,
                    UserName = registerModel.Email,
                    Role = Role.Student,
                    School = null,
                    Headline = null,
                    EnrollmentDate = DateTime.Now,
                }; 
            }
            else if (registerModel.Role == Role.Instructor)
            {
                user = new Instructor
                {
                    FirstName = registerModel.FirstName,
                    MiddelName = registerModel.MiddelName,
                    LastName = registerModel.LastName,
                    Email = registerModel.Email,
                    UserName = registerModel.Email,
                    Role = Role.Instructor,
                    JopTitle = null,
                    Salary = null,
                }; 
            }
            else
                throw new Exception("Invalid role");

            var result = await accountRepository.CreateUserAsync(user, registerModel.Password);

            if (result.Succeeded)
            {
                if (user is Student student)
                   await accountRepository.AddStudentAsync(student);
                else if (user is Instructor instructor)
                    await accountRepository.AddInstructorAsync(instructor);
            }
            return result; 
        } 
    }
}
