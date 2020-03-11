#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

JARVIZ_HOME="$(pwd)"
cd ./jarviz-graph

echo 'Installing jarviz-graph'
npm install

echo 'Building jarviz-graph'
npm run build

cd "${JARVIZ_HOME}"
echo 'Done'
