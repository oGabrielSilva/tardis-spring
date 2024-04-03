import { generateHTML } from '@App/utils/generateHTML';
import { AnimationTool } from './AnimationTool';

interface Theme {
  icon: string;
  backdrop: string;
  color: string;
}

export class ToasterTool {
  private readonly anim;
  private readonly events: Array<number> = [];
  private readonly toasterID = 'toaster';
  private toaster: HTMLDivElement | null = null;
  private toasterText: HTMLSpanElement | null = null;
  private toasterIcon: HTMLElement | null = null;
  private toasterProgress: HTMLElement | null = null;
  private readonly styleWithAnim = document.createElement('style');
  private toasterInScreen = false;

  private readonly animationClass = 'ex-anim-progress-bar';
  private readonly animationKeyframePropertyName = 'kf-anim-progress-bar';
  private readonly themes = {
    info: {
      icon: 'bi bi-info-circle',
      backdrop: 'var(--primary)',
      color: 'var(--text-on-primary)',
    } as Theme,
    danger: {
      icon: 'bi bi-exclamation-diamond',
      backdrop: 'var(--danger)',
      color: 'var(--text-on-danger)',
    } as Theme,
    alert: {
      icon: 'bi bi-question-diamond',
      backdrop: 'var(--alert)',
      color: 'var(--text-on-alert)',
    } as Theme,
    success: {
      icon: 'bi bi-check2-circle',
      backdrop: 'var(--success)',
      color: 'var(--text-on-success)',
    } as Theme,
  };

  private afterFunction?: () => void = void 0;

  private constructor(anim: AnimationTool) {
    this.anim = anim;
  }

  private generate() {
    this.toaster = generateHTML({
      htmlType: 'div',
      attributes: [{ key: 'id', value: this.toasterID }],
      children: [
        {
          htmlType: 'div',
          attributes: [{ key: 'class', value: 'content' }],
          children: [
            {
              htmlType: 'i',
              attributes: [{ key: 'class', value: 'bi bi-info-circle' }],
            },
            { htmlType: 'span' },
          ],
        },
        {
          htmlType: 'div',
          attributes: [{ key: 'class', value: 'progress ' + this.animationClass }],
        },
      ],
    });
    this.toasterText = this.toaster!.querySelector('span');
    this.toasterIcon = this.toaster!.querySelector('i');
    this.toasterProgress = this.toaster!.querySelector('.progress');
    document.head.appendChild(this.styleWithAnim);
    this.toaster!.onclick = () => {
      this.hide();
    };
  }

  private updateStyle(timer: number) {
    this.styleWithAnim.innerHTML = `
    .${this.animationClass} {
      animation: ${this.animationKeyframePropertyName} ${timer}ms ease;
    }

    @keyframes ${this.animationKeyframePropertyName} {
      to {
        transform: translateX(-100%);
      }
    }
    `;
  }

  private updateTheme({ backdrop, color, icon }: Theme) {
    this.toasterIcon!.className = icon;
    this.toaster?.style.setProperty('background', backdrop);
    this.toasterIcon?.style.setProperty('color', color);
    this.toasterText?.style.setProperty('color', color);
  }

  private show(timer: number) {
    if (this.toasterInScreen) {
      this.hide().then(() => this.show(timer));
      return;
    }
    this.toaster!.style.scale = '0';
    this.toasterInScreen = true;
    this.toasterProgress!.style.transform = '';
    document.body.appendChild(this.toaster!);
    this.events.push(setTimeout(() => this.anim.scaleIn(this.toaster!), 100));
    this.events.push(
      setTimeout(() => (this.toasterProgress!.style.transform = 'translateX(-100%)'), timer - 10),
    );
  }

  private async hide() {
    return new Promise((r) => {
      try {
        this.events.forEach((e) => clearTimeout(e));
        this.anim.scaleOut(this.toaster!, () => {
          this.toaster?.remove();
          this.toasterInScreen = false;
          r(true);
        });
      } catch (error) {
        console.error(error);
        r(false);
      } finally {
        if (typeof this.afterFunction === 'function') this.afterFunction();
        this.afterFunction = void 0;
      }
    });
  }

  public addAfterFunction(func: () => void) {
    this.afterFunction = func;
    return this;
  }

  public info(message: string, timerInMilliseconds = 6000) {
    this.updateStyle(timerInMilliseconds);
    this.updateTheme(this.themes.info);
    this.toasterText!.textContent = message;
    this.show(timerInMilliseconds);
    this.events.push(setTimeout(() => this.hide(), timerInMilliseconds));
  }

  public alert(message: string, timerInMilliseconds = 6000) {
    this.updateStyle(timerInMilliseconds);
    this.updateTheme(this.themes.alert);
    this.toasterText!.textContent = message;
    this.show(timerInMilliseconds);
    this.events.push(setTimeout(() => this.hide(), timerInMilliseconds));
  }

  public success(message: string, timerInMilliseconds = 6000) {
    this.updateStyle(timerInMilliseconds);
    this.updateTheme(this.themes.success);
    this.toasterText!.textContent = message;
    this.show(timerInMilliseconds);
    this.events.push(setTimeout(() => this.hide(), timerInMilliseconds));
  }

  public danger(message: string, timerInMilliseconds = 6000) {
    this.updateStyle(timerInMilliseconds);
    this.updateTheme(this.themes.danger);
    this.toasterText!.textContent = message;
    this.show(timerInMilliseconds);
    this.events.push(setTimeout(() => this.hide(), timerInMilliseconds));
  }

  public getAnimator() {
    return this.anim;
  }

  public static create(animation: AnimationTool) {
    const t = new ToasterTool(animation);
    t.generate();
    return t;
  }
}
