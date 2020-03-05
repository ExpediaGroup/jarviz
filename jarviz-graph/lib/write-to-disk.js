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

// 3rd party
const path = require('path');
const fs = require('fs');

/**
 * writeToDisk()
 * Given a data object and dataName, write webpack assets to disk
 * @param {object} args
 * @param {string} args.data - processed data object with nodes/links.  should be passed to rendering context
 * @param {string} args.dataName - name of graph, used in defining target disk path
 * @param {string} args.outputPath - absolute path to output HTML graph
 */
async function writeToDisk({data, dataName, outputPath}) {
    const jarvizGraphHtml = fs.readFileSync(path.join(__dirname, '../build/client/jarviz-graph.html'), 'utf-8');
    console.log('writeToDisk', dataName);

    const jarvizGraphHtmlAndData = jarvizGraphHtml.replace('{{{JARVIZ_DATA}}}', JSON.stringify(data));

    // Return promise that resolves if webpack operation is sucessful, else throw exception
    return new Promise((resolve, reject) => {
        fs.writeFile(`${outputPath}/${dataName}.html`, jarvizGraphHtmlAndData, function(err) {
            if (err) {
                throw new Error(err);
            }
            resolve();
        });
    });
}
module.exports = {writeToDisk};
