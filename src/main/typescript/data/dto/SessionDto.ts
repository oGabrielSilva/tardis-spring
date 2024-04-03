/* eslint-disable no-unused-vars */
import type { User } from '../entities/User';

export class SessionDto {
  constructor(
    public readonly user: User,
    public readonly token: string,
  ) {}
}
