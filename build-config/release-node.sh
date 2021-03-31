#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Trying to deploy Jarviz...'
JARVIZ_HOME="$(pwd)"

if [ "$TRAVIS_PULL_REQUEST" == 'false' -a ! -z "$SONATYPE_NEXUS_USERNAME" ]; then
  if [ ! -z "$TRAVIS_TAG" ]; then
    echo "Deploying for tag $TRAVIS_TAG"


    echo 'Deploying jarviz-graph release'
    cd ./jarviz-graph
    npm config set '//registry.npmjs.org/:_authToken' "${NPM_TOKEN}" && npm publish --access public
    cd "${JARVIZ_HOME}"

  else
      echo 'Skipping deploy for jarviz-graph.'
  fi
fi

echo 'Done'
