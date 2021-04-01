#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

LAST_COMMIT_MSG=$(git log -1 --pretty=format:"%s")
if [[ ("$LAST_COMMIT_MSG" == '[maven-release-plugin] prepare release'* ) ]] ; then
  echo 'Skipping build for release preparation step.'
  exit 0
fi

echo 'Compile, test (no IT tests) and build jarviz-lib module.'
mvn clean install -DskipITs
echo 'Done'
