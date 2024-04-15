import { JsonRestApi } from '@App/api/JsonRestApi';
import type { ExceptionDto } from '@App/data/dto/ExceptionDto';
import { User } from '@App/data/entities/User';
import { UserValidation } from '@App/data/validation/UserValidation';
import { AnimationTool } from '@App/tools/AnimationTool';
import { ImageTool } from '@App/tools/ImageTool';
import { ScreenProgressTool } from '@App/tools/ScreenProgressTool';
import { ToasterTool } from '@App/tools/ToasterTool';

const imageTool = ImageTool.get();
const toaster = ToasterTool.get();
const anim = AnimationTool.get();
const progress = ScreenProgressTool.get();
const rest = JsonRestApi.get();
const validation = new UserValidation();

function editProfileManager(form: HTMLFormElement) {
  const userEmail = form.dataset.auth;
  const main = document.querySelector('main')!;
  const header = document.querySelector('header')!;
  const avatarInput = form.querySelector<HTMLInputElement>('#avatar-input')!;
  const avatarImg = form.querySelector<HTMLImageElement>('.avatar')!;
  const headerAvatarImg = document
    .querySelector('header')!
    .querySelector<HTMLImageElement>('.avatar')!;
  const avatarMessage = avatarInput.dataset.message!;
  const spanUserName = form.querySelector('#span-user-name');
  const modal = form.querySelector<HTMLDivElement>('.modal')!;
  const modalCancel = modal.querySelector<HTMLButtonElement>('button.danger');
  const modalConfirm = modal.querySelector<HTMLButtonElement>('button.primary');
  let lastAvatarURL = '';

  main.style.minHeight = `calc(95vh - ${header.clientHeight}px)`;

  avatarImg.addEventListener('click', () => avatarInput.click());
  avatarInput.addEventListener('input', async () => {
    progress.show();
    try {
      if (!avatarInput.files || !avatarInput.files[0]) {
        toaster.info(avatarMessage);
        anim.shake(avatarImg);
        return;
      }
      const avatar = await imageTool.imageToBlob(avatarInput.files[0]);
      if (!avatar) {
        toaster.info(avatarMessage);
        anim.shake(avatarImg);
        return;
      }

      const payload = new FormData();
      payload.set('avatar', avatar);
      const res = await fetch('/api/authentication/user/avatar', {
        method: 'PATCH',
        body: payload,
        credentials: 'include',
      });
      const json = (await res.json()) as ExceptionDto;
      if (res.ok) toaster.success(json.message);
      else toaster.danger(json.message);
      URL.revokeObjectURL(lastAvatarURL);
      lastAvatarURL = URL.createObjectURL(avatar);
      avatarImg.src = lastAvatarURL;
      headerAvatarImg.src = lastAvatarURL;
    } catch (error) {
      console.log(error);
    } finally {
      progress.hide();
    }
  });

  const passwordInput = modal.querySelector<HTMLInputElement>('#password')!;
  let password = passwordInput.value.trim();
  let isPasswordValid = validation.isPasswordValid(password);

  passwordInput.addEventListener('input', () => {
    password = passwordInput.value.trim();
    isPasswordValid = validation.isPasswordValid(password);
    passwordInput.dataset.error = String(!isPasswordValid);
  });

  const nameInput = form.querySelector<HTMLInputElement>('#name')!;
  const defaultName = nameInput.value.trim();
  let name = defaultName;
  let isNameValid = validation.isNameValid(name);

  nameInput.addEventListener('input', () => {
    name = nameInput.value.trim();
    isNameValid = validation.isNameValid(name);
    nameInput.dataset.error = String(!isNameValid);
  });

  const emailInput = form.querySelector<HTMLInputElement>('#email')!;
  const defaultEmail = emailInput.value.trim();
  let email = defaultEmail;
  let isEmailValid = validation.isEmailValid(email);

  emailInput.addEventListener('input', () => {
    email = emailInput.value.trim();
    isEmailValid = validation.isEmailValid(email);
    emailInput.dataset.error = String(!isEmailValid);
  });

  const usernameInput = form.querySelector<HTMLInputElement>('#username')!;
  const usernameAlreadyInUse = usernameInput.dataset.usernameAlreadyRegistered;
  let username = usernameInput.value.trim();
  let isUsernameValid = validation.isUsernameValid(username);

  usernameInput.addEventListener('input', () => {
    usernameInput.value = validation.normalizeUsername(usernameInput.value);
    username = usernameInput.value.trim();
    isUsernameValid = validation.isUsernameValid(username);
    usernameInput.dataset.error = String(!isUsernameValid);
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    progress.show();
    if (!isUsernameValid) {
      toaster.alert(nameInput.dataset.message);
      return;
    }
    if (!isEmailValid) {
      toaster.alert(emailInput.dataset.message);
      return;
    }
    if (!isNameValid) {
      toaster.alert(nameInput.dataset.message);
      return;
    }
    const { status, json } = await rest.GET<User>(
      'user/passwordu, JSON.stringify({password, newPassword})sername/' + username,
    );
    if (status === 200) {
      if (json?.email !== userEmail) toaster.info(usernameAlreadyInUse?.replace('#', username));
    }
    if (defaultEmail !== email && isEmailValid) {
      modal?.classList.remove('out-bottom');
      progress.hide();
      return;
    }
    updateProfile();
  });

  async function updateProfile(includeCredentials = false) {
    try {
      const payload = includeCredentials ? { email, password, name, username } : { name, username };
      const { json, status } = await rest.PUT<User>(
        '/authentication/user',
        JSON.stringify(payload),
      );
      if (status === 200) {
        toaster.success(form.dataset.success);
        spanUserName!.textContent = json!.name;
        return history.pushState(null, '', '/u/' + json!.username);
      } else {
        toaster.danger((json as unknown as ExceptionDto).message);
        console.log(json);
      }
    } catch (error) {
      toaster.danger('Oopsss...');
      console.log(error);
    } finally {
      progress.hide();
    }
  }

  modalCancel?.addEventListener('click', () => modal.classList.add('out-bottom'));
  modalConfirm?.addEventListener('click', () => {
    if (!isPasswordValid) {
      toaster.alert(passwordInput.dataset.message);
      anim.shake(passwordInput);
      return;
    }
    progress.show();
    updateProfile(true);
  });
}

function updatePasswordManager(modal: HTMLElement, button: HTMLButtonElement) {
  button.addEventListener('click', () => modal.classList.remove('out-left'));

  const cancel = modal.querySelector<HTMLButtonElement>('button.danger');
  const form = modal.querySelector('form')!;

  const passwordInput = modal.querySelector<HTMLInputElement>('#c-password')!;
  let password = passwordInput.value.trim();
  let isPasswordValid = validation.isPasswordValid(password);

  passwordInput.addEventListener('input', () => {
    password = passwordInput.value.trim();
    isPasswordValid = validation.isPasswordValid(password);
    passwordInput.dataset.error = String(!isPasswordValid);
  });

  const onConfirmInput = () => {
    confirmNewPassword = confirmNewPasswordInput.value.trim();
    confirmNewPasswordInput.dataset.error = String(
      !(isNewPasswordValid && newPassword === confirmNewPassword),
    );
  };

  const newPasswordInput = modal.querySelector<HTMLInputElement>('#n-password')!;
  let newPassword = newPasswordInput.value.trim();
  let isNewPasswordValid = validation.isPasswordValid(newPassword);

  newPasswordInput.addEventListener('input', () => {
    newPassword = newPasswordInput.value.trim();
    isNewPasswordValid = validation.isPasswordValid(newPassword);
    newPasswordInput.dataset.error = String(!isNewPasswordValid);
    onConfirmInput();
  });

  const confirmNewPasswordInput = modal.querySelector<HTMLInputElement>('#c-n-password')!;
  let confirmNewPassword = confirmNewPasswordInput.value.trim();

  confirmNewPasswordInput.addEventListener('input', onConfirmInput);

  cancel?.addEventListener('click', () => {
    modal.classList.add('out-left');
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    progress.show();
    try {
      if (!isPasswordValid) {
        toaster.danger(passwordInput.dataset.message);
        anim.shake(passwordInput);
        return;
      }
      if (!isNewPasswordValid) {
        toaster.danger(newPasswordInput.dataset.message);
        anim.shake(newPasswordInput);
        return;
      }
      if (confirmNewPassword !== newPassword) {
        toaster.danger(confirmNewPasswordInput.dataset.message);
        anim.shake(confirmNewPasswordInput);
        return;
      }
      if (confirmNewPassword === password) {
        toaster.danger(confirmNewPasswordInput.dataset.messageEqual);
        anim.shake(confirmNewPasswordInput);
        anim.shake(newPasswordInput);
        anim.shake(passwordInput);
        return;
      }
      const { json, status } = await rest.PATCH(
        '/authentication/user/password',
        JSON.stringify({ password, newPassword }),
      );
      if (status === 204) {
        passwordInput.value = '';
        password = '';
        newPassword = '';
        newPasswordInput.value = '';
        confirmNewPassword = '';
        confirmNewPasswordInput.value = '';
        cancel?.click();
        toaster.success(form.dataset.success);
        return;
      }
      toaster.danger((json as unknown as ExceptionDto).message);
    } catch (error) {
      console.log(error);
    } finally {
      progress.hide();
    }
  });
}

export function runProfileManager() {
  const editProfile = document.getElementById('edit-profile') as HTMLFormElement;

  if (editProfile) {
    editProfileManager(editProfile);
    updatePasswordManager(
      document.getElementById('modal-password')!,
      document.getElementById('button-update-password') as HTMLButtonElement,
    );
  }
}
