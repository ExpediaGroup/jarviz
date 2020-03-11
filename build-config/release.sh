#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

JARVIZ_HOME="$(pwd)"
export NPM_TOKEN=''

echo "Node version: $(npm --version)"
echo 'Java version:'
java -version

printf '\nReleasing jarviz-graph...\n'
cd ./jarviz-graph
TEMP_NPM_VERSION=$(npm version patch)
git add package.json
git commit -m "Bumped version to ${TEMP_NPM_VERSION} in package.json"
cd "${JARVIZ_HOME}"

printf '\nBuilding jarviz-lib...\n'
mvn release:prepare -Dresume=false -DskipTests -Darguments='-DskipTests'
echo 'Done'
