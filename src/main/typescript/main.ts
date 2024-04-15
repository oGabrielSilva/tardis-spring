import { runAuthenticationManager } from './manager/authenticationManager';
import { runProfileManager } from './manager/profileManager';
import { quizCreationManager } from './manager/quizCreationManager';
import { quizEditManager } from './manager/quizEditManager';

(() => {
  console.log('This app is written in TypeScript');
  const managerID = document.getElementById('www-manager-id') as HTMLInputElement;
  if (!managerID) {
    return console.error('Manager ID is not defined');
  }
  switch (managerID.value) {
    case 'session':
      runAuthenticationManager(document.getElementById('session') as HTMLFormElement);
      break;
    case 'profile':
      runProfileManager();
      break;
    case 'quiz-create':
      quizCreationManager(document.getElementById('form-n-quiz') as HTMLFormElement);
      break;
    case 'quiz-edit':
      quizEditManager(document.getElementById('form-e-quiz') as HTMLFormElement);
      break;
    default:
      break;
  }
})();
