/* eslint-disable no-unused-vars */
export class User {
  constructor(
    public readonly email: string,
    public readonly username: string,
    public readonly name: string,
    public readonly avatarURL: string | null,
    public readonly emailChecked: boolean,
    public readonly authorities: Array<string>,
  ) {}

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public fromEntity(entity: any) {
    if (
      !entity ||
      typeof entity !== 'object' ||
      !entity.email ||
      !entity.username ||
      !entity.name ||
      !entity.avatarURL ||
      !entity.emailChecked ||
      !entity.authorities
    ) {
      return null;
    }

    return new User(
      entity.email,
      entity.username,
      entity.name,
      entity.avatarURL,
      entity.emailChecked,
      entity.authorities,
    );
  }
}
