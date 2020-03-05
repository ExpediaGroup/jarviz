//--------------------------------------------------------------------------
// Copyright 2020 Expedia, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//--------------------------------------------------------------------------

const chalk = require('chalk');
const seperator = '\n';

/**
 * log()
 * Logs a message
 * @param  {...any} args
 */
function log(...args) {
    console.log(chalk.white(...args));
}

/**
 * logError()
 * Logs an error
 * @param  {...any} args
 */
function logError(...args) {
    console.log(chalk.red(...args));
}

/**
 * logCritical()
 * Logs a critical message
 * @param  {...any} args
 */
function logCritical(...args) {
    console.log(chalk.bold.underline.red(seperator));
    console.log(chalk.bold.underline.red(...args));
    console.log(chalk.bold.underline.red(seperator));
}

/**
 * logSuccess()
 * Logs a successful message
 * @param  {...any} args
 */
function logSuccess(...args) {
    console.log(chalk.green(...args));
}

module.exports = {
    log,
    logError,
    logCritical,
    logSuccess
};
