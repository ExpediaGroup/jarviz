#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Deploying Jarviz ...'
JAR_CUR_DIR="$(pwd)"

echo 'Building jarviz-lib'
mvn clean install

echo 'Building jarviz-graph'
cd ./jarviz-graph
build:example

cd "$JAR_CUR_DIR"

echo 'Done'
