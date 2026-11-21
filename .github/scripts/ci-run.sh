#!/bin/bash
# Keep the full log and surface failures in check annotations as well as job logs.
set -euo pipefail
name=$1
shift
mkdir -p .ci
log=".ci/$name.log"
if "$@" 2>&1 | tee "$log"; then
  exit 0
else
  status=$?
fi
python3 - "$log" <<'PY'
import pathlib
import re
import sys
text = pathlib.Path(sys.argv[1]).read_text(errors='replace')
text = re.sub(r'\x1b\[[0-9;]*m', '', text)
marker = text.find('* What went wrong:')
summary = text[marker:marker + 10000] if marker >= 0 else text[-10000:]
errors = [line for line in text.splitlines()
          if re.search(r'(^e:|error:|^ld:|Undefined symbols|framework not found)', line)]
message = '\n'.join(errors[:20]) + '\n' + summary
message = message[:14000].replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
print(f'::error title=Native build failure::{message}')
PY
exit "$status"
