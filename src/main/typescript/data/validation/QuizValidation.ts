export class QuizValidation {
  private readonly keywordsRegex = /[^a-zA-Z0-9,_\-@#!%&?*\s]/g;

  public isTitleValid(title: string) {
    return typeof title === 'string' && title.length >= 5;
  }

  public catchKeywords(keywords: string) {
    if (!keywords) return [];
    return this.normalizeKeywords(keywords)
      .replace(/,/g, ' ')
      .split(' ')
      .filter((k) => k.length >= 1);
  }

  public normalizeKeywords(keywords: string) {
    return typeof keywords === 'string'
      ? keywords.replace(this.keywordsRegex, '').toLocaleLowerCase()
      : '';
  }

  public isQuestTitleValid(title: string) {
    return typeof title === 'string' && title.length <= 150 && title.length >= 5;
  }

  public normalizeQuestTitle(title: string) {
    return typeof title === 'string' ? title.replace(/\r\n|\r|\n/g, '') : '';
  }
}
