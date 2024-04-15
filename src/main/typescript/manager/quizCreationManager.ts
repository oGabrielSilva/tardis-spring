import { QuizValidation } from '@App/data/validation/QuizValidation';
import { AnimationTool } from '@App/tools/AnimationTool';
import { ImageTool } from '@App/tools/ImageTool';
import { ScreenProgressTool } from '@App/tools/ScreenProgressTool';
import { ToasterTool } from '@App/tools/ToasterTool';

const progress = ScreenProgressTool.get();
const imageTool = ImageTool.get();
const validation = new QuizValidation();
const anim = AnimationTool.get();
const toaster = ToasterTool.get(anim);

export function quizCreationManager(form: HTMLFormElement) {
  const addImage = form.querySelector('#btn-add-f-img') as HTMLButtonElement;
  const imageInput = form.querySelector('input[type="file"]') as HTMLInputElement;
  const image = form.querySelector('#image-preview') as HTMLImageElement;
  let imageSrc = '';
  let blob: Blob;

  addImage.addEventListener('click', () => imageInput.click());
  image.addEventListener('click', () => imageInput.click());
  imageInput.addEventListener('input', () => {
    progress.show();
    setTimeout(async () => {
      try {
        if (!imageInput.files || !imageInput.files[0]) return;
        const imgBlob = await imageTool.imageToBlobWhithoutResize(imageInput.files[0]);
        if (!imgBlob) return toaster.alert('Oopss...');
        if (imgBlob.size > 1024000) {
          console.log(imgBlob.size);
          toaster.danger(imageInput.dataset.message);
          return;
        }
        URL.revokeObjectURL(imageSrc);
        imageSrc = URL.createObjectURL(imgBlob);
        image.src = imageSrc;
        blob = imgBlob;
        if (image.classList.contains('dn')) {
          image.classList.remove('dn');
          addImage.classList.add('dn');
        }
      } catch (error) {
        console.log(error);
      } finally {
        progress.hide();
      }
    }, 500);
  });

  const titleInput = form.querySelector('#title') as HTMLInputElement;
  let title = titleInput.value;
  let isTitleValid = validation.isTitleValid(title);

  titleInput.addEventListener('input', () => {
    title = titleInput.value;
    isTitleValid = validation.isTitleValid(title);
  });

  const descriptionInput = form.querySelector('#description') as HTMLInputElement;
  const displayDescriptionLen = form.querySelector('#description-len') as HTMLElement;
  let description = descriptionInput.value;

  displayDescriptionLen.textContent = (350 - description.length).toString();

  descriptionInput.addEventListener('input', () => {
    description = descriptionInput.value;
    displayDescriptionLen.textContent = (350 - description.length).toString();
  });

  const keywordInput = form.querySelector('#keywords') as HTMLInputElement;
  const keywordsPreview = form.querySelector('#preview-keywords') as HTMLElement;
  let keywords = validation.catchKeywords(keywordInput.value);

  keywordInput.addEventListener('input', () => {
    keywords = validation.catchKeywords(keywordInput.value);
    keywordsPreview.innerHTML = '';
    keywords.forEach((key) => {
      const preview = document.createElement('span');
      preview.textContent = key;
      preview.title = key;
      keywordsPreview.appendChild(preview);
    });
  });

  const submit = async () => {
    progress.show();
    try {
      if (!blob) {
        toaster.alert(addImage.dataset.message);
        if (!addImage.classList.contains('dn')) {
          addImage.scrollIntoView();
          anim.shake(addImage);
        }
        return;
      }
      if (!isTitleValid) {
        toaster.alert(titleInput.dataset.message);
        titleInput.scrollIntoView();
        anim.shake(titleInput);
        return;
      }
      if (keywords.length < 1) {
        toaster.alert(keywordInput.dataset.message);
        keywordInput.scrollIntoView();
        anim.shake(keywordInput);
        return;
      }
      const payload = new FormData();
      payload.set('title', title);
      payload.set('description', description ? description : '');
      payload.set('keywords', keywords.join(','));
      payload.set('image', blob);

      const result = await fetch('/api/quiz', {
        body: payload,
        credentials: 'include',
        method: 'POST',
      });
      console.log(result);
      if (result.status === 403) {
        window.open('/session?clear=true', '_blank', 'focus');
        return;
      }
      const json = await result.json();
      if (!result.ok) {
        toaster.danger(json.message);
      } else window.location.href = json.url; //Termine
    } catch (error) {
      console.log(error);
    } finally {
      progress.hide();
    }
  };

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    submit();
  });
}
