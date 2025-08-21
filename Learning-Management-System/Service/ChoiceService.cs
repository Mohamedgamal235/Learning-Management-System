using Learning_Management_System.Repository.Interfaces;
using Learning_Management_System.Service.Interfaces;

namespace Learning_Management_System.Service
{
    public class ChoiceService : IChoiceService
    {
        private readonly IChoiceRepository _choiceRepository;

        public ChoiceService(IChoiceRepository choiceRepository)
        {
            _choiceRepository = choiceRepository;
        }

        // Implementation will be added later
    }
}
