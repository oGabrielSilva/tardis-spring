/* eslint-disable no-unused-vars */
export class ExceptionDto {
  public constructor(
    public readonly timestamp: string,
    public readonly message: string,
    public readonly url: string,
    public status: number,
  ) {}
}
