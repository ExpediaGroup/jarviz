#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

function removeNPMVersionPrefix() {
  TMP_V="$1"
  if [[ ("$TMP_V" == 'v'* ) || ("$TMP_V" == 'V'* ) ]] ; then
    printf '%s' "${TMP_V:1}"
  else
    printf '%s' "${TMP_V}"
  fi
}

JARVIZ_HOME="$(pwd)"

echo "Node version: $(removeNPMVersionPrefix "$(node --version)")"
echo "NPM version: $(npm --version)"
echo 'Java version:'
java -version

printf '\n📦 \033[0;32mYou are about to release both Java and Node packages. Proceed ?\033[0m [yes/no] '
read -r user_input
if [ "$user_input" != 'yes' ]; then
  echo 'Release canceled!'
  exit 0
fi

printf '\nPreparing release of jarviz-graph...\n'
cd ./jarviz-graph
TMP_NPM_VERSION=$(removeNPMVersionPrefix "$(npm version patch)")
git add package.json
git commit -m "[npm] prepare release @vrbo/jarviz-graph@${TMP_NPM_VERSION}"
cd "${JARVIZ_HOME}"

printf '\nPreparing release of jarviz-lib...\n'
mvn release:prepare -Dresume=false -DskipTests

echo 'Done'

printf '\n✏️  \033[0;32mPlease modify jarviz-cli/jarviz and set %s as JARVIZ_CLI_VERSION.\033[0m\n' "'${TMP_NPM_VERSION}'"
#sed -i -E "s|JARVIZ_CLI_VERSION\=[^\n]*|JARVIZ_CLI_VERSION\=${TEMP_NPM_VERSION}|" jarviz-cli/jarviz
