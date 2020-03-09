#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

JAR_CUR_DIR="$(pwd)"

echo 'Building jarviz-graph'
cd ./jarviz-graph

echo "> $(pwd)"
npm install
npm run build:example

cd "$JAR_CUR_DIR"
echo "> $(pwd)"
echo 'Done'
