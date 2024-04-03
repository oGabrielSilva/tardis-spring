export class JsonRestApi {
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

  private normalizePath(path: string) {
    console.log(
      path.startsWith('/api') ? path : '/api' + (path.startsWith('/') ? path : path + '/'),
    );
    return path.startsWith('/api') ? path : '/api' + (path.startsWith('/') ? path : path + '/');
  }

  private configureHeaders(headers: Headers) {
    headers.set('Content-Type', 'application/json');
    return headers;
  }

  private options(
    body: string,
    headers: Headers,
    method: 'GET' | 'POST' | 'PATCH' | 'DELETE' = 'GET',
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
}
