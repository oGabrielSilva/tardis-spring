import { JsonRestApi } from './api/JsonRestApi';
import { runAuthenticationManager } from './manager/authenticationManager';
import { runProfileManager } from './manager/profileManager';
import { AnimationTool } from './tools/AnimationTool';
import { ImageTool } from './tools/ImageTool';
import { ScreenProgressTool } from './tools/ScreenProgressTool';
import { ToasterTool } from './tools/ToasterTool';

(() => {
  const managerID = document.getElementById('www-manager-id') as HTMLInputElement;
  if (!managerID) {
    return console.error('Manager ID is not defined');
  }

  const anim = new AnimationTool();
  const toaster = ToasterTool.create(anim);
  const progress = new ScreenProgressTool();
  const rest = new JsonRestApi();
  const imageTool = new ImageTool();

  switch (managerID.value) {
    case 'session':
      runAuthenticationManager(
        document.getElementById('session') as HTMLFormElement,
        anim,
        toaster,
        progress,
        rest,
      );
      break;
    case 'profile':
      runProfileManager(imageTool, toaster);
      break;
    default:
      break;
  }
})();
