import type { JsonRestApi } from '@App/api/JsonRestApi';
import type { ExceptionDto } from '@App/data/dto/ExceptionDto';
import type { SessionDto } from '@App/data/dto/SessionDto';
import { UserValidation } from '@App/data/validation/UserValidation';
import type { AnimationTool as Anim } from '@App/tools/AnimationTool';
import type { ScreenProgressTool } from '@App/tools/ScreenProgressTool';
import type { ToasterTool } from '@App/tools/ToasterTool';

export function runAuthenticationManager(
  form: HTMLFormElement,
  anim: Anim,
  toaster: ToasterTool,
  progress: ScreenProgressTool,
  rest: JsonRestApi,
) {
  let path = '';
  const genericError = form.dataset.generic ? form.dataset.generic : 'Oopss...';
  const successMessage = form.dataset.success ? form.dataset.success : '#';
  const validation = new UserValidation();
  const emailInput = form.querySelector('input#email') as HTMLInputElement;
  const emailMessage = emailInput.dataset.message!;
  const passwordInput = form.querySelector('input#password') as HTMLInputElement;
  const passwordMessage = passwordInput.dataset.message!;
  const submitButton = form.querySelector('button[type="submit"]') as HTMLButtonElement;
  const signUpButton = form.querySelector('#session-sign-up') as HTMLButtonElement;
  const submit = document.createElement('button');

  emailInput.addEventListener('input', () => {
    emailInput.dataset.error = String(!validation.isEmailValid(emailInput.value.trim()));
  });

  passwordInput.addEventListener('input', () => {
    passwordInput.dataset.error = String(!validation.isPasswordValid(passwordInput.value.trim()));
  });

  submitButton.addEventListener('click', (e) => {
    e.preventDefault();
    path = '/authentication/session';
    submit.click();
  });

  signUpButton.addEventListener('click', () => {
    path = '/authentication/sign-up';
    submit.click();
  });

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    if (!validation.isEmailValid(emailInput.value.trim())) {
      anim.shake(emailInput);
      toaster.danger(emailMessage);
      return;
    }
    if (!validation.isPasswordValid(passwordInput.value.trim())) {
      anim.shake(passwordInput);
      toaster.danger(passwordMessage);
      return;
    }
    sendAuthenticationRequest();
  });

  async function sendAuthenticationRequest() {
    progress.show();
    try {
      const payload = JSON.stringify({
        email: emailInput.value.trim(),
        password: passwordInput.value.trim(),
      });
      const { json } = await rest.POST<SessionDto>(path, payload);
      if (!json) {
        toaster.alert(genericError);
        return;
      }
      if (!json.user) {
        toaster.alert((json as unknown as ExceptionDto).message);
        return;
      }
      toaster
        .addAfterFunction(() => (location.href = '/'))
        .success(successMessage.replace('#', json.user.username));
    } catch (error) {
      console.log(error);
      toaster.alert(genericError);
    } finally {
      progress.hide();
    }
  }

  submit.type = 'submit';
  submit.textContent = 'Submit';
  submit.classList.add('dn');
  form.appendChild(submit);
}
