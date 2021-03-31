#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

if [ ! -z "$SONATYPE_USERNAME" ]; then
  echo "Releasing for tag $GITHUB_REF"

  echo 'Building jarviz-lib'
  mvn clean install -DskipTests

  echo 'Releasing jarviz-lib'
  mvn deploy --settings build-config/mvn-settings.xml -B -U -P oss-release -DskipTests=true
else
  echo 'Sonatype authentication variables missing!'
fi

echo 'Done'
