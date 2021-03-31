#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Releasing jarviz-lib...'

if [ ! -z "$SONATYPE_USERNAME" ]; then
  if [ ! -z "$TRAVIS_TAG" ]; then
    echo "Releasing for tag $TRAVIS_TAG"

    echo 'Building jarviz-lib'
    mvn clean install -DskipTests

    echo 'Deploying jarviz-lib release'
    #mvn deploy --settings build-config/mvn-settings.xml -B -U -P oss-release -DskipTests=true
  else
    if [[ "$TRAVIS_COMMIT_MESSAGE" == '[maven-release-plugin] prepare release'* ]] ; then
      echo 'Skipping deploy for release preparation step.'
      exit 0
    fi

    echo 'Releasing jarviz-lib snapshot'
    #mvn deploy --settings build-config/mvn-settings.xml -B -U -P oss-snapshot -DskipTests=true
  fi
else
  echo 'Sonatype authentication variables missing!'
fi

echo 'Done'
