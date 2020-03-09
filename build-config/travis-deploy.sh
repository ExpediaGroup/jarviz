#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

echo 'Deploying Jarviz ...'

echo '**********************'
ls -l jarviz-lib
echo '**********************'
ls -l jarviz-graph
echo '**********************'

echo 'Done'
