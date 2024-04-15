import { ScreenProgressTool } from '@App/tools/ScreenProgressTool';
import { ToasterTool } from '@App/tools/ToasterTool';

export function forbidden() {
  ToasterTool.get().hide();
  ScreenProgressTool.get().hide();
  ToasterTool.get().alert('Forbidden (403)');
  window.open('/session?clear=true', '_blank', 'focus');
}
