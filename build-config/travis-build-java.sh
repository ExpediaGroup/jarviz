#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

if [[ "$TRAVIS_COMMIT_MESSAGE" == '[maven-release-plugin] prepare release'* ]] ; then
  echo 'Skipping build for release preparation step.'
  exit 0
fi

echo 'Building jarviz-lib'
mvn clean install
echo 'Done'
