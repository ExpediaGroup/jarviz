#!/usr/bin/env node

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

'use strict';

const program = require('commander');
const path = require('path');
let inputPath = null;
let outputPath = null;
const {processAll} = require('../lib');

program.option('-i, --input <path>', 'input path to directory containing jarviz JSONL');
program.option('-o, --output <path>', 'output path to directory for HTML graph');

program.parse(process.argv);

if (program.input) {
    inputPath = path.join(process.cwd(), program.input);
} else {
    inputPath = path.join(process.cwd());
}

if (program.output) {
    outputPath = path.join(process.cwd(), program.output);
} else {
    outputPath = path.join(process.cwd());
}

console.log('p', inputPath, outputPath);

processAll(inputPath, outputPath);
