#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

LAST_COMMIT_MSG=$(git log -1 --pretty=format:"%s")
if [[ ("$LAST_COMMIT_MSG" == '[maven-release-plugin] prepare release'* ) ]] ; then
  echo 'Skipping build for release preparation step.'
  exit 0
fi

JARVIZ_HOME="$(pwd)"
cd ./jarviz-graph
echo 'Installing jarviz-graph'
npm install
echo 'Building jarviz-graph'
npm run build
cd "${JARVIZ_HOME}"
echo 'Done'
