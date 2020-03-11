#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

if [[ "$TRAVIS_COMMIT_MESSAGE" == '[maven-release-plugin] prepare release'* ]] ; then
  echo 'Skipping build for prepare release step.'
fi

JARVIZ_HOME="$(pwd)"
cd ./jarviz-graph
echo 'Installing jarviz-graph'
npm install
echo 'Building jarviz-graph'
npm run build
cd "${JARVIZ_HOME}"
echo 'Done'
