import { UserValidation } from '@App/data/validation/UserValidation';
import type { AnimationTool } from '@App/tools/AnimationTool';
import type { ImageTool } from '@App/tools/ImageTool';
import type { ToasterTool } from '@App/tools/ToasterTool';

let imageTool: ImageTool;
let toaster: ToasterTool;
let anim: AnimationTool;
const validation = new UserValidation();

function editProfileManager(form: HTMLFormElement) {
  const main = document.querySelector('main')!;
  const header = document.querySelector('header')!;
  const avatarInput = form.querySelector<HTMLInputElement>('#avatar-input')!;
  const avatarImg = form.querySelector<HTMLImageElement>('.avatar')!;
  const avatarMessage = avatarInput.dataset.message!;
  let lastAvatarURL = '';

  main.style.minHeight = `calc(95vh - ${header.clientHeight}px)`;

  avatarImg.addEventListener('click', () => avatarInput.click());
  avatarInput.addEventListener('input', async () => {
    if (!avatarInput.files || !avatarInput.files[0]) {
      toaster.info(avatarMessage);
      anim.shake(avatarImg);
      return;
    }
    const img = await imageTool.imageToBlob(avatarInput.files[0]);
    if (!img) {
      toaster.info(avatarMessage);
      anim.shake(avatarImg);
      return;
    }
    URL.revokeObjectURL(lastAvatarURL);
    lastAvatarURL = URL.createObjectURL(img);
    avatarImg.src = lastAvatarURL;
  });

  const nameInput = form.querySelector<HTMLInputElement>('#name')!;
  const defaultName = nameInput.value;
  let name = defaultName;
  let isNameValid = validation.isNameValid(name);

  nameInput.addEventListener('input', () => {
    name = nameInput.value;
    isNameValid = validation.isNameValid(name);
    nameInput.dataset.error = String(!isNameValid);
  });

  const emailInput = form.querySelector<HTMLInputElement>('#email')!;
  const defaultEmail = emailInput.value;
  let email = defaultEmail;
  let isEmailValid = validation.isEmailValid(email);

  emailInput.addEventListener('input', () => {
    email = emailInput.value;
    isEmailValid = validation.isEmailValid(email);
    emailInput.dataset.error = String(!isEmailValid);
  });

  const usernameInput = form.querySelector<HTMLInputElement>('#username')!;
  const defaultUsername = usernameInput.value;
  let username = defaultUsername;
  let isUsernameValid = validation.isUsernameValid(username);

  usernameInput.addEventListener('input', () => {
    username = usernameInput.value;
    isUsernameValid = validation.isUsernameValid(username);
    usernameInput.dataset.error = String(!isUsernameValid);
  });

  //Terminar a função de salvar os dados...
}

export function runProfileManager(imgTool: ImageTool, toasterTool: ToasterTool) {
  imageTool = imgTool;
  toaster = toasterTool;
  anim = toaster.getAnimator();
  const editProfile = document.getElementById('edit-profile') as HTMLFormElement;

  if (editProfile) {
    editProfileManager(editProfile);
  }
}
