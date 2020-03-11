#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Trying to deploy Jarviz...'

if [ "$TRAVIS_PULL_REQUEST" == 'false' -a ! -z "$SONATYPE_NEXUS_USERNAME" ]; then
  if [ ! -z "$TRAVIS_TAG" ]; then
    mkdir -p ${GPG_DIR}

    echo "Deploying jarviz-lib release $TRAVIS_TAG"
    openssl aes-256-cbc -K $encrypted_a065d0ea71e2_key -iv $encrypted_a065d0ea71e2_iv -in build-config/gpg.asc.enc -out ${GPG_DIR}/gpg.asc -d
    gpg --version
    gpg --import ${GPG_DIR}/gpg.asc

    echo 'Recieving public key...'
    gpg --keyserver hkp://pool.sks-keyservers.net:80 --recv-keys D5F915C564AD5F117400B9D9A974EB7CB594B2D0

    mvn deploy --settings build-config/travis-mvn-settings.xml -B -U -P oss-release -DskipTests=true

    rm -rf ${GPG_DIR}
  else
    if [[ "$TRAVIS_COMMIT_MESSAGE" == '[maven-release-plugin] prepare release'* ]] ; then
      echo 'Skipping mvn deploy, the release should happend when the tag is built.'
    else
      echo 'Deploying jarviz-lib snapshot'
      mvn deploy --settings build-config/travis-mvn-settings.xml -B -U -P oss-snapshot -DskipTests=true
    fi
  fi
fi

echo 'Done'
