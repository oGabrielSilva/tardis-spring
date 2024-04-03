interface Attr {
  key: string;
  value: string;
}

interface HTML {
  htmlType: string;
  attributes?: Array<Attr>;
  children?: Array<HTML>;
  onClick?: () => void;
}

function generate({ htmlType, attributes, onClick, children }: HTML) {
  const html = document.createElement(htmlType);
  if (attributes) attributes.forEach((attr) => html.setAttribute(attr.key, attr.value));
  if (onClick) html.onclick = onClick;
  if (children) {
    children.forEach((child) => {
      const c = generate(child);
      html.appendChild(c);
    });
  }
  return html;
}

export function generateHTML<T>({ attributes, htmlType, onClick, children }: HTML) {
  const html = generate({ htmlType, attributes, onClick });
  if (children) {
    children.forEach((child) => {
      const c = generate(child);
      html.appendChild(c);
    });
  }
  return html as T;
}
