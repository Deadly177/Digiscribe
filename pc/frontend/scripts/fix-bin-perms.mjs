import { chmodSync, readdirSync, statSync } from 'node:fs';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';

const binDir = fileURLToPath(new URL('../node_modules/.bin', import.meta.url));

try {
  const entries = readdirSync(binDir);
  entries.forEach((entry) => {
    if (entry.endsWith('.cmd') || entry.endsWith('.ps1')) {
      return;
    }
    const filePath = join(binDir, entry);
    try {
      const stats = statSync(filePath);
      if (stats.isFile()) {
        chmodSync(filePath, 0o755);
      }
    } catch (err) {
      console.warn(`Unable to adjust permissions for ${entry}: ${err.message}`);
    }
  });
} catch (err) {
  if (err.code !== 'ENOENT') {
    console.warn(`Unable to scan node_modules/.bin: ${err.message}`);
  }
}
