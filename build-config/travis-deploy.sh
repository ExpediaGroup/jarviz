#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Deploying Jarviz'

if [ "$TRAVIS_PULL_REQUEST" == 'false' -a ! -z "$SONATYPE_JIRA_USERNAME" ]; then
  if [ ! -z "$TRAVIS_TAG" ]; then
    echo "Deploying release"
    mkdir -p ${GPG_DIR}
    openssl aes-256-cbc -K $encrypted_a065d0ea71e2_key -iv $encrypted_a065d0ea71e2_iv -in build-config/gpg.asc.enc -out ${GPG_DIR}/gpg.asc -d
    gpg --version
    gpg --import ${GPG_DIR}/gpg.asc
    mvn deploy --settings mvn-settings.xml -B -U -P oss-release -DskipTests=true -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
    rm -rf ${GPG_DIR}
  else
    echo "Deploying snapshot"
    mvn deploy --settings mvn-settings.xml -B -U -P oss-snapshot -DskipTests=true -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
  fi
fi

echo 'Done'
