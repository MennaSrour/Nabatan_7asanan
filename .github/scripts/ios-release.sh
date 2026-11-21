#!/bin/bash
set -euo pipefail

: "${RUNNER_TEMP:?GitHub runner temporary directory is required}"
: "${APPLE_CERT_BASE64:?Missing signing certificate}"
: "${APPLE_CERT_PASSWORD:?Missing certificate password}"
: "${APPLE_PROVISIONING_PROFILE_BASE64:?Missing provisioning profile}"

signing_dir=$(mktemp -d "$RUNNER_TEMP/ios-signing.XXXXXX")
keychain="$signing_dir/signing.keychain-db"
keychain_password=$(openssl rand -hex 32)
touch "$signing_dir/installed-profiles"
cleanup() {
  security delete-keychain "$keychain" >/dev/null 2>&1 || true
  while IFS= read -r path; do rm -f "$path"; done < "$signing_dir/installed-profiles"
  rm -rf "$signing_dir"
}
trap cleanup EXIT

# Decode outside the workspace; never cache certificates, keychains or profiles.
export SIGNING_DIR="$signing_dir"
python3 - <<'PY'
import base64
import os
from pathlib import Path
root = Path(os.environ['SIGNING_DIR'])
for secret, filename in [('APPLE_CERT_BASE64', 'certificate.p12'),
                         ('APPLE_PROVISIONING_PROFILE_BASE64', 'profile.mobileprovision')]:
    (root / filename).write_bytes(base64.b64decode(os.environ[secret], validate=True))
PY
security create-keychain -p "$keychain_password" "$keychain"
security set-keychain-settings -lut 21600 "$keychain"
security unlock-keychain -p "$keychain_password" "$keychain"
security import "$signing_dir/certificate.p12" -P "$APPLE_CERT_PASSWORD" \
  -A -t cert -f pkcs12 -k "$keychain" >/dev/null
security set-key-partition-list -S apple-tool:,apple:,codesign: \
  -k "$keychain_password" "$keychain" >/dev/null
# Retain the login keychain so Xcode can also find Apple's intermediate certificates.
security list-keychains -d user -s "$keychain" "$HOME/Library/Keychains/login.keychain-db"
identity=$(security find-identity -v -p codesigning "$keychain" | awk '/"/ {print $2; exit}')
if [[ -z "$identity" ]]; then echo 'No valid signing identity in the supplied P12.' >&2; exit 1; fi

security cms -D -i "$signing_dir/profile.mobileprovision" > "$signing_dir/profile.plist"
mkdir -p .ci/export
python3 - <<'PY'
import datetime
import fnmatch
import os
import plistlib
import re
from pathlib import Path
root = Path(os.environ['SIGNING_DIR'])
profile = plistlib.loads((root / 'profile.plist').read_bytes())
project = Path('iosApp/iosApp.xcodeproj/project.pbxproj').read_text()
bundle = re.search(r'PRODUCT_BUNDLE_IDENTIFIER = ([\w.]+);', project).group(1)
allowed_bundle = profile['Entitlements']['application-identifier'].split('.', 1)[1]
if not fnmatch.fnmatchcase(bundle, allowed_bundle):
    raise SystemExit('Provisioning profile does not match the Xcode bundle identifier.')
if profile['ExpirationDate'] <= datetime.datetime.now(datetime.timezone.utc).replace(tzinfo=None):
    raise SystemExit('Provisioning profile has expired.')
team = profile['TeamIdentifier'][0]
if profile.get('ProvisionsAllDevices'):
    method = 'enterprise'
elif profile.get('ProvisionedDevices'):
    method = 'debugging' if profile['Entitlements'].get('get-task-allow') else 'release-testing'
else:
    method = 'app-store-connect'
options = dict(method=method, teamID=team, signingStyle='manual',
               provisioningProfiles={bundle: profile['UUID']},
               manageAppVersionAndBuildNumber=False)
Path('.ci/ExportOptions.plist').write_bytes(plistlib.dumps(options))
(root / 'team').write_text(team)
(root / 'uuid').write_text(profile['UUID'])
PY
team=$(cat "$signing_dir/team")
profile_uuid=$(cat "$signing_dir/uuid")
for directory in "$HOME/Library/MobileDevice/Provisioning Profiles" \
  "$HOME/Library/Developer/Xcode/UserData/Provisioning Profiles"; do
  mkdir -p "$directory"
  path="$directory/ci-$profile_uuid.mobileprovision"
  printf '%s\n' "$path" >> "$signing_dir/installed-profiles"
  cp "$signing_dir/profile.mobileprovision" "$path"
done

xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Release -sdk iphoneos -destination 'generic/platform=iOS' \
  -derivedDataPath "$RUNNER_TEMP/ios-release-derived" \
  -clonedSourcePackagesDirPath "$PWD/.ci/SourcePackages" \
  -archivePath "$PWD/.ci/iosApp.xcarchive" \
  CODE_SIGN_STYLE=Manual DEVELOPMENT_TEAM="$team" CODE_SIGN_IDENTITY="$identity" \
  PROVISIONING_PROFILE_SPECIFIER="$profile_uuid" \
  OTHER_CODE_SIGN_FLAGS="--keychain $keychain" archive
xcodebuild -exportArchive -archivePath "$PWD/.ci/iosApp.xcarchive" \
  -exportOptionsPlist "$PWD/.ci/ExportOptions.plist" -exportPath "$PWD/.ci/export"
