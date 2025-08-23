using Learning_Management_System.Service.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
using System.Threading.Tasks;

namespace Learning_Management_System.Controllers
{
    public class AccountController : Controller
    {
        private readonly IAccountService accountService;

        public AccountController(IAccountService accountService)
        {
            this.accountService = accountService;
        }

        // ------------------------------------
        // --------------- Register -----------
        // ------------------------------------

        [HttpGet]
        public IActionResult Register(RegisterModel registerModel)
        {
            return View(registerModel);
        }

        [HttpPost]
        public async Task<IActionResult> SaveRegister(RegisterModel registerModel)
        {
            if (ModelState.IsValid)
            {
                IdentityResult result = await accountService.RegisterAsync(registerModel);
                if (result.Succeeded)
                {
                    // mapping 
                    LoginModel log = new LoginModel();
                    log.Email = registerModel.Email;
                    log.Password = registerModel.Password;
                    log.RememberMe = false; 

                    await accountService.LoginAsync(log); // create cookie 
                    return RedirectToAction("Login");
                }
                foreach(var error in result.Errors)
                    ModelState.AddModelError(string.Empty, error.Description);
            }
            return View("Register", registerModel);
        }

        // ------------------------------------
        // --------------- Log in -------------
        // ------------------------------------

        [HttpGet]
        public IActionResult Login(LoginModel loginModel)
        {
            return View(loginModel);
        }

        [HttpPost]
        public async Task<IActionResult> SaveLogin(LoginModel loginModel)
        {
            if (ModelState.IsValid)
            {
                var result = await accountService.LoginAsync(loginModel);
                if (result != Microsoft.AspNetCore.Identity.SignInResult.Failed)
                {
                    return RedirectToAction("Index");
                }
            }
            return View("Login" , loginModel);
        }

        // ------------------------------------
        // --------------- Log Out ------------
        // ------------------------------------

        public async Task<IActionResult> LogOut()
        {
            await accountService.LogoutAsync();
            return RedirectToAction("Login");
        }
    }
}
