import { JsonRestApi } from '@App/api/JsonRestApi';
import type { ExceptionDto } from '@App/data/dto/ExceptionDto';
import type { QuestDto } from '@App/data/dto/QuestDto';
import { QuizValidation } from '@App/data/validation/QuizValidation';
import { AnimationTool } from '@App/tools/AnimationTool';
import { ImageTool } from '@App/tools/ImageTool';
import { ScreenProgressTool } from '@App/tools/ScreenProgressTool';
import { ToasterTool } from '@App/tools/ToasterTool';
import { forbidden } from '@App/utils/forbidden';
import { generateHTML } from '@App/utils/generateHTML';

declare global {
  // eslint-disable-next-line no-var
  var listOfQuests: Array<QuestDto>;
}

const progress = ScreenProgressTool.get();
const imageTool = ImageTool.get();
const validation = new QuizValidation();
const anim = AnimationTool.get();
const toaster = ToasterTool.get(anim);
const rest = JsonRestApi.get();

let quests = typeof listOfQuests !== 'undefined' ? listOfQuests : [];

export function quizEditManager(form: HTMLFormElement) {
  const imageInput = form.querySelector('input[type="file"]') as HTMLInputElement;
  const image = form.querySelector('#image-preview') as HTMLImageElement;
  let imageSrc = '';
  let blob: Blob;

  image.addEventListener('click', () => imageInput.click());
  imageInput.addEventListener('input', () => {
    progress.show();
    setTimeout(async () => {
      try {
        if (!imageInput.files || !imageInput.files[0]) return;
        const imgBlob = await imageTool.imageToBlobWhithoutResize(imageInput.files[0]);
        if (!imgBlob) return toaster.alert('Oopss...');
        if (imgBlob.size > 1024000) {
          toaster.danger(imageInput.dataset.message);
          return;
        }
        URL.revokeObjectURL(imageSrc);
        imageSrc = URL.createObjectURL(imgBlob);
        image.src = imageSrc;
        blob = imgBlob;
        if (image.classList.contains('dn')) {
          image.classList.remove('dn');
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

  const showKeywords = () => {
    keywordsPreview.innerHTML = '';
    keywords.forEach((key) => {
      const preview = document.createElement('span');
      preview.textContent = key;
      preview.title = key;
      keywordsPreview.appendChild(preview);
    });
  };

  keywordInput.addEventListener('input', () => {
    keywords = validation.catchKeywords(keywordInput.value);
    showKeywords();
  });

  showKeywords();

  const submit = async () => {
    progress.show();
    try {
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
      if (result.status === 403) {
        return forbidden();
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

  formAddNewQuestManager();
}

function formAddNewQuestManager() {
  const questPreview = document.getElementById('preview-quests')!;
  const quizSlug = document.getElementById('quiz-slug')!.dataset.value;
  const addQuestButton = document.querySelector('#button-add-new-quest')! as HTMLButtonElement;
  const questModal = document.getElementById('add-quest-modal')!;
  const formAddQuest = questModal.querySelector('form')!;
  const hideAddQuestModal = [
    questModal.querySelector('#hide-form-add-quest') as HTMLButtonElement,
    questModal.querySelector('button.danger') as HTMLButtonElement,
  ];
  const questImagePreview = questModal.querySelector('#quest-image-preview') as HTMLImageElement;
  const questImageInput = questModal.querySelector('#quest-image-input') as HTMLInputElement;
  const questTitle = questModal.querySelector('#quest-quest') as HTMLInputElement;

  const alternativeA = formAddQuest.querySelector('#alternative-a') as HTMLInputElement;
  const alternativeB = formAddQuest.querySelector('#alternative-b') as HTMLInputElement;
  const alternativeC = formAddQuest.querySelector('#alternative-c') as HTMLInputElement;
  const alternativeD = formAddQuest.querySelector('#alternative-d') as HTMLInputElement;
  const alternativeE = formAddQuest.querySelector('#alternative-e') as HTMLInputElement;

  let questImageSrc = '';
  let questImageBlob: Blob | null = null;

  addQuestButton.addEventListener('click', () => {
    questModal.style.display = '';
    setTimeout(() => questModal.classList.remove('out-right'), 10);
  });
  questModal.addEventListener('click', ({ target }) => {
    if (
      (target as HTMLElement).id === questModal.id &&
      formAddQuest.dataset.updateQuest === 'true'
    ) {
      formAddQuest.dataset.updateQuest = 'false';
      resetAddNewQuestForm();
      hideAddQuestModal[0].click();
    }
  });
  hideAddQuestModal.forEach((button) =>
    button.addEventListener('click', () => {
      questModal.classList.add('out-right');
      setTimeout(() => (questModal.style.display = 'none'), 500);
    }),
  );

  questImagePreview.addEventListener('click', () => questImageInput.click());
  questImageInput.addEventListener('input', () => {
    progress.show();
    setTimeout(async () => {
      try {
        if (!questImageInput.files || !questImageInput.files[0]) return;
        const imgBlob = await imageTool.imageToBlobWhithoutResize(questImageInput.files[0]);
        if (!imgBlob) return toaster.alert('Oopss...');
        if (imgBlob.size > 1024000) {
          toaster.danger(questImageInput.dataset.message);
          return;
        }
        URL.revokeObjectURL(questImageSrc);
        questImageSrc = URL.createObjectURL(imgBlob);
        questImagePreview.src = questImageSrc;
        questImageBlob = imgBlob;
      } catch (error) {
        console.log(error);
      } finally {
        progress.hide();
      }
    }, 500);
  });

  function resetAddNewQuestForm() {
    [alternativeA, alternativeB, alternativeC, alternativeD, alternativeE].forEach(
      (a) => (a.value = ''),
    );
    questImageBlob = null;
    questImageInput.value = '';
    questTitle.value = '';
    questImagePreview.src = questImagePreview.dataset.defaultSrc!;
  }

  const normalizeQuestTitle = () => {
    questTitle.value = validation.normalizeQuestTitle(questTitle.value);
  };
  questTitle.addEventListener('input', normalizeQuestTitle);

  formAddQuest.addEventListener('submit', async (e) => {
    e.preventDefault();
    let error = false;
    progress.show();
    try {
      const title = questTitle.value;
      if (!validation.isQuestTitleValid(title)) {
        toaster.danger(questTitle.dataset.message);
        anim.shake(questTitle);
        return (error = true);
      }
      const opt = [alternativeC, alternativeD, alternativeE];
      if (alternativeA.value.length < 1) {
        toaster.danger(alternativeA.dataset.message);
        anim.shake(alternativeA);
        return (error = true);
      }

      if (alternativeB.value.length < 1) {
        const optWithValue = opt.filter((al) => al.value.length >= 1);
        if (optWithValue.length > 0) {
          alternativeB.value = optWithValue[0].value;
          alternativeC.value = alternativeD.value ? alternativeD.value : '';
          alternativeD.value = alternativeE.value ? alternativeE.value : '';
          alternativeE.value = '';
        } else {
          toaster.danger(alternativeB.dataset.message);
          anim.shake(alternativeB);
          return (error = true);
        }
      }
      const payload = new FormData();
      payload.set('quest', questTitle.value);
      if (questImageBlob) payload.set('image', questImageBlob);
      payload.set('a', alternativeA.value ? alternativeA.value : '');
      payload.set('b', alternativeB.value ? alternativeB.value : '');
      payload.set('c', alternativeC.value ? alternativeC.value : '');
      payload.set('d', alternativeD.value ? alternativeD.value : '');
      payload.set('e', alternativeE.value ? alternativeE.value : '');
      const response = await fetch(`/api/quiz/${quizSlug}/quest`, {
        body: payload,
        credentials: 'include',
        method: 'POST',
      });
      if (response.status === 403) {
        error = true;
        return forbidden();
      }
      if (!response.ok) {
        const json = (await response.json()) as ExceptionDto;
        toaster.danger(json.message);
        error = true;
      } else {
        toaster.info(formAddQuest.dataset.success);
        const json = (await response.json()) as QuestDto;
        appendQuestPreviewAt(json, questPreview.querySelector('.items')!);
        setTimeout(() => hideAddQuestModal[0].click(), 500);
      }
    } catch (e) {
      error = true;
      console.log(error);
    } finally {
      progress.hide();
      if (!error) resetAddNewQuestForm();
    }
  });

  const previews = document.querySelectorAll<HTMLElement>('.preview-item')!;
  const confirmDeleteMessage = questPreview.dataset.delete ?? '';
  const deletedSuccessMessage = questPreview.dataset.deleteSuccess ?? '';
  previews.forEach((item) => {
    const id = Number(item.dataset.id);
    const quest = quests.find((q) => q.id === id);
    if (!quest) return console.log('Quest error: ' + id);
    item.addEventListener('click', ({ target }) =>
      onPreviewItemClick(quest, target as HTMLElement),
    );
    const deleteButton = item.querySelector<HTMLButtonElement>('button.delete')!;
    deleteButton.onclick = () => onDelete(deleteButton, quest);
  });

  async function onDelete(element: HTMLElement, quest: QuestDto) {
    const parent = element.parentElement!.parentElement!;
    if (element.dataset.clicked !== 'true') {
      element.dataset.clicked = 'true';
      anim.shake(parent);
      const icon = element.querySelector('i')!;
      icon.style.color = 'var(--alert)';
      toaster.alert(normalizeDeleteMessage(confirmDeleteMessage, quest));
      setTimeout(() => {
        element.dataset.clicked = 'false';
        icon.style.color = 'var(--danger-light)';
      }, 5000);
      return;
    }
    toaster.hide();
    progress.show();
    const { response, json } = await rest.DELETE('/api/quiz/quest/' + quest.id);
    if (response.status === 403) {
      return forbidden();
    }
    if (response.ok) {
      toaster.success(deletedSuccessMessage);
      quests = quests.filter((q) => q.id != quest.id);
      anim.scaleOut(parent, () => {
        parent.remove();
        updateQuestPreview(questPreview.querySelector('.items')!);
      });
    } else {
      toaster.danger(json!.message);
      anim.shake(parent);
    }
    progress.hide();
  }

  function normalizeDeleteMessage(message: string, quest: QuestDto) {
    return message.replace('#', quest.quest);
  }

  function appendQuestPreviewAt(quest: QuestDto, at: HTMLElement) {
    quests.push(quest);
    updateQuestPreview(at);
  }

  function updateQuestPreview(at: HTMLElement) {
    at.innerHTML = '';
    quests.forEach((q, i) => {
      at.appendChild(
        generateHTML<HTMLDivElement>({
          htmlType: 'div',
          attributes: [{ key: 'class', value: 'preview-item' }],
          onClick: (t) => onPreviewItemClick(q, t),
          children: [
            {
              htmlType: 'span',
              value: (i + 1).toString(),
            },
            {
              htmlType: 'span',
              value: q.quest,
              attributes: [
                { key: 'class', value: 'quest-title' },
                { key: 'style', value: 'color: var(--title);' },
              ],
            },
            {
              htmlType: 'div',
              attributes: [{ key: 'class', value: 'buttons' }],
              children: [
                {
                  htmlType: 'button',
                  onClick: () => {},
                  children: [
                    { htmlType: 'i', attributes: [{ key: 'class', value: 'bi bi-pencil-square' }] },
                  ],
                  attributes: [
                    { key: 'type', value: 'button' },
                    { key: 'class', value: 'quest-edit' },
                    { key: 'data-id', value: q.id.toString() },
                  ],
                },
                {
                  htmlType: 'button',
                  onClick: (target) => onDelete(target, q),
                  children: [
                    {
                      htmlType: 'i',
                      attributes: [
                        { key: 'class', value: 'bi bi-trash3' },
                        {
                          key: 'style',
                          value: 'color: var(--danger-light)',
                        },
                      ],
                    },
                  ],
                  attributes: [
                    { key: 'type', value: 'button' },
                    { key: 'class', value: 'delete' },
                    { key: 'data-id', value: q.id.toString() },
                  ],
                },
              ],
            },
          ],
        }),
      );
    });

    const alert = document.getElementById('preview-small-quest-alert')!;
    const noQuizAlert = document.getElementById('no-quest-alert');
    if (quests.length < 1) {
      noQuizAlert?.classList.remove('dn');
    } else noQuizAlert?.classList.add('dn');
    if (quests.length >= 5) {
      alert.classList.add('dn');
      return;
    }
    alert.classList.remove('dn');
    alert.scrollIntoView();
    setTimeout(() => anim.shake(alert), 500);
  }

  function onPreviewItemClick(q: QuestDto, target: HTMLElement): void {
    if (
      target.nodeName.toLocaleLowerCase() === 'i' &&
      target.parentElement?.classList.contains('delete')
    ) {
      return;
    }
    resetAddNewQuestForm();
    alternativeA.value = q.a;
    alternativeB.value = q.b;
    alternativeC.value = q.c;
    alternativeD.value = q.d;
    alternativeE.value = q.e;

    questTitle.value = q.quest;
    questImagePreview.src = q.imageURL.startsWith('https://')
      ? q.imageURL
      : questImagePreview.dataset.defaultSrc!;
    formAddQuest.dataset.updateQuest = 'true';
    addQuestButton.click();
  }
}
