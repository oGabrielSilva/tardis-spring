import type { ExceptionDto } from '@App/data/dto/ExceptionDto';

export class JsonRestApi {
  private static rest: JsonRestApi;

  private constructor() {}

  public async GET<T = null>(path: string, headers = new Headers()) {
    const response = await fetch(this.normalizePath(path), this.optionsWithoutBody(headers, 'GET'));
    const json = await this.getJson<T>(response);
    return { json, response, status: response.status };
  }

  public async POST<T = null>(path: string, body: string, headers = new Headers()) {
    const response = await fetch(this.normalizePath(path), this.options(body, headers, 'POST'));
    const json = await this.getJson<T>(response);
    return { json, response, status: response.status };
  }

  public async PUT<T = null>(path: string, body: string, headers = new Headers()) {
    const response = await fetch(this.normalizePath(path), this.options(body, headers, 'PUT'));
    const json = await this.getJson<T>(response);
    return { json, response, status: response.status };
  }

  public async PATCH<T = null>(path: string, body: string, headers = new Headers()) {
    const response = await fetch(this.normalizePath(path), this.options(body, headers, 'PATCH'));
    const json = await this.getJson<T>(response);
    return { json, response, status: response.status };
  }

  public async DELETE(path: string, headers = new Headers()) {
    const response = await fetch(
      this.normalizePath(path),
      this.optionsWithoutBody(headers, 'DELETE'),
    );
    if (!response.ok) {
      const json = await this.getJson<ExceptionDto>(response);
      return { response, status: response.status, json };
    }
    return { response, status: response.status };
  }

  private normalizePath(path: string) {
    return path.startsWith('/api') ? path : '/api' + (path.startsWith('/') ? path : path + '/');
  }

  private configureHeaders(headers: Headers) {
    headers.set('Content-Type', 'application/json');
    return headers;
  }

  private options(
    body: string,
    headers: Headers,
    method: 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE' = 'GET',
  ): RequestInit {
    return { body, headers: this.configureHeaders(headers), credentials: 'include', method };
  }

  private optionsWithoutBody(headers: Headers, method: 'GET' | 'DELETE' = 'GET'): RequestInit {
    return { headers: this.configureHeaders(headers), credentials: 'include', method };
  }

  private getJson<T>(f: Response) {
    if (f.status !== 204 && f.status !== 205) return f.json() as Promise<T>;
    return null;
  }

  public static get() {
    if (!JsonRestApi.rest) {
      JsonRestApi.rest = new JsonRestApi();
    }
    return JsonRestApi.rest;
  }
}
