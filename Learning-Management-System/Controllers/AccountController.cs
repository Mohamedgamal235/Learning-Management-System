using Learning_Management_System.Service.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Learning_Management_System.Controllers
{
    public class AccountController : Controller
    {
        private readonly IAccountService accountService;

        public AccountController(IAccountService accountService)
        {
            this.accountService = accountService;
        }

        [HttpGet]
        public IActionResult Register(RegisterModel registerModel)
        {
            return View();
        }

    }
}
