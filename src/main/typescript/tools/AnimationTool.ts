export class AnimationTool {
  private static anim: AnimationTool;

  private readonly shakeClass = 'animated-by-shake';

  private constructor() {}

  public scaleIn(element: HTMLElement, onComplete?: () => void) {
    element.style.scale = '1.1';
    setTimeout(() => {
      element.style.scale = '';
      if (onComplete) setTimeout(() => onComplete(), 400);
    }, 400);
  }

  public scaleOut(element: HTMLElement, onComplete?: () => void) {
    element.style.scale = '1.1';
    setTimeout(() => {
      element.style.scale = '0';
      if (onComplete) setTimeout(() => onComplete(), 400);
    }, 100);
  }

  public shake(element: HTMLElement, getFocus = true, timer = 400) {
    if (element.classList.contains(this.shakeClass)) {
      element.classList.remove(this.shakeClass);
    }
    if (getFocus) element.focus();
    setTimeout(() => {
      element.classList.add(this.shakeClass);
      setTimeout(() => element.classList.remove(this.shakeClass), timer);
    }, 100);
  }

  public static get() {
    if (!AnimationTool.anim) {
      AnimationTool.anim = new AnimationTool();
    }
    return AnimationTool.anim;
  }
}
