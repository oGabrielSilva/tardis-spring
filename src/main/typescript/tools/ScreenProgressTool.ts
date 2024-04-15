import { generateHTML } from '@App/utils/generateHTML';

export class ScreenProgressTool {
  private static tool: ScreenProgressTool;

  private readonly screenProgressID = 'progress-view-container';
  private readonly barClassName = 'bar';
  private readonly bar2ClassName = 'bar2';
  private readonly view = generateHTML<HTMLDivElement>({
    htmlType: 'div',
    attributes: [{ key: 'id', value: this.screenProgressID }],
    children: [
      { htmlType: 'div', attributes: [{ key: 'class', value: this.barClassName }] },
      { htmlType: 'div', attributes: [{ key: 'class', value: this.bar2ClassName }] },
    ],
  });

  private isVisible = false;

  private constructor() {}

  public show() {
    if (this.isVisible) return;
    this.isVisible = true;
    document.body.appendChild(this.view);
  }

  public hide() {
    this.isVisible = false;
    this.view.remove();
  }

  public onScreen() {
    return this.isVisible;
  }

  public static get() {
    if (!ScreenProgressTool.tool) {
      ScreenProgressTool.tool = new ScreenProgressTool();
    }
    return ScreenProgressTool.tool;
  }
}
