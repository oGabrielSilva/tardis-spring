import * as esbuild from 'esbuild';

const ctx = await esbuild.context({
  entryPoints: ['main.ts'],
  bundle: true,
  minify: true,
  sourcemap: true,
  target: ['es6'],
  loader: { '.ts': 'ts' },
  outfile: '../resources/static/javascript/dist.min.js',
});

console.log('Ready! Listening for changes ⚡');

ctx.watch();
