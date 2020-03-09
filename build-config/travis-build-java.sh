#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Building jarviz-lib'
mvn clean install

echo 'Done'
