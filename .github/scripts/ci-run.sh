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
python3 - "$log" "$name" <<'PY'
import pathlib
import re
import sys
import xml.etree.ElementTree as ET
text = pathlib.Path(sys.argv[1]).read_text(errors='replace')
text = re.sub(r'\x1b\[[0-9;]*m', '', text)
marker = text.find('* What went wrong:')
summary = text[marker:marker + 10000] if marker >= 0 else text[-10000:]
errors = [line for line in text.splitlines()
          if re.search(r'(^e:|error:|^ld:|Undefined symbols|framework not found)', line)]
test_failures = []
for report in pathlib.Path('.').glob('**/build/test-results/**/TEST-*.xml'):
    try:
        for case in ET.parse(report).getroot().iter('testcase'):
            for failure in list(case.findall('failure')) + list(case.findall('error')):
                test_failures.append(f"{case.get('classname')}.{case.get('name')}: {failure.get('message', '')}\n{(failure.text or '')[:2500]}")
    except (ET.ParseError, OSError):
        continue
message = '\n'.join(errors[:20] + test_failures[:3]) + '\n' + summary
message = message[:14000].replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
print(f'::error title=Build failure ({sys.argv[2]})::{message}')
PY
exit "$status"
