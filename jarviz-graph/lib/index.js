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

// 3rd party
const {scaleOrdinal} = require('d3-scale');
const d3_scale_chromatic = require('d3-scale-chromatic');
const fs = require('fs');
const glob = require('glob');
const path = require('path');
const copydir = require('copy-dir');

// user
const {convertToUsageCount, extractSimpleClassName, escapeHtml} = require('./utils');
const {log, logError, logSuccess, logCritical} = require('./log');
const {writeToDisk} = require('./write-to-disk');

// variables
const autoColorScale = scaleOrdinal([...d3_scale_chromatic.schemePaired, ...d3_scale_chromatic.schemeDark2, ...d3_scale_chromatic.schemeAccent]);

/**
 * processData()
 * Reads a JSON file stream and computes graph data
 * @param {*} dataPath - Path to JSON file
 * @param {*} done - callback function invoked with single object containing `data` and `dataName` properties upon successful run
 */
function processData(dataPath, done) {
    // Get just the filename from the path
    const dataName = path.basename(dataPath, '.jsonl');

    // Define variables
    let linesProcessed = 0;

    // Define data structure for processed data
    const data = {
        sourceClass: {
            nodesMap: {},
            links: [],
            colorMap: {}
        },
        'sourceClass-targetClass': {
            nodesMap: {},
            links: [],
            colorMap: {}
        }
    };

    // Log progress message
    log(`Processing ${dataPath}...`);

    // Create read stream and interface
    const readableStream = fs.createReadStream(dataPath);
    readableStream.setEncoding('utf8');
    const lineReader = require('readline').createInterface({
        input: readableStream
    });

    /**
     * processDataRow
     * Processes single row of data given a groupBy
     * @param {*} row
     * @param {*} data
     * @param {*} groupBy
     */
    function processDataRow(row, _data, groupBy) {
        const data = _data[groupBy];

        // Map available column names
        const applicationSetName = row.applicationSetName;
        const applicationName = row.applicationName;
        const artifactFileName = row.artifactFileName;
        const artifactId = row.artifactId;
        const artifactGroup = row.artifactGroup;
        const artifactVersion = row.artifactVersion;
        const sourceClass = escapeHtml(row.sourceClass);
        const sourceMethod = escapeHtml(row.sourceMethod);
        const targetClass = escapeHtml(row.targetClass);
        const targetMethod = escapeHtml(row.targetMethod);

        const artifactFullName = `${artifactGroup}.${artifactId}`;
        const simpleTargerClass = extractSimpleClassName(targetClass);
        const sourceMethodFullName = `${sourceClass}#${sourceMethod}`;
        const targetMethodFullName = `${targetClass}#${targetMethod}`;

        // Define source and target IDs to establish node identity and linkage
        let sourceNodeId = `${artifactGroup}_${artifactId}`;
        let targetNodeId = `${applicationName}_${targetClass}_${targetMethod}`;
        if (groupBy === 'sourceClass-targetClass') {
            // Make source node id unique relative to it's target node
            sourceNodeId = `${targetClass}_${artifactGroup}_${artifactId}`;
            targetNodeId = `${targetClass}_${targetMethod}`;
        }

        const sourceNode = data.nodesMap[sourceNodeId];
        const targetNode = data.nodesMap[targetNodeId];

        if (!data.colorMap[applicationName]) {
            data.colorMap[applicationName] = autoColorScale(applicationName);
        }

        if (sourceNode) {
            sourceNode.couplingMethods.push(sourceMethodFullName);

            const couplingLinkMap = sourceNode.couplingLinkMap[targetNodeId];
            if (couplingLinkMap) {
                if (!couplingLinkMap.includes(sourceMethodFullName)) {
                    couplingLinkMap.push(sourceMethodFullName);
                }
            } else {
                sourceNode.couplingLinkMap[targetNodeId] = [sourceMethodFullName];
            }
        } else {
            data.nodesMap[sourceNodeId] = {
                id: sourceNodeId,
                name: sourceNodeId,
                target: false,
                applicationName,
                artifact: artifactFullName,
                couplingClass: '',
                couplingMethods: [sourceMethodFullName],
                couplingLinkMap: {
                    [targetNodeId]: [sourceMethodFullName]
                },
                color: data.colorMap[applicationName],
                val: 1.7
            };
        }

        if (targetNode) {
            targetNode.couplingMethods.push(targetMethodFullName);

            const couplingLinkMap = targetNode.couplingLinkMap[sourceNodeId];
            if (couplingLinkMap) {
                if (!couplingLinkMap.includes(targetMethodFullName)) {
                    couplingLinkMap.push(targetMethodFullName);
                }
            } else {
                targetNode.couplingLinkMap[sourceNodeId] = [targetMethodFullName];
            }
        } else {
            data.nodesMap[targetNodeId] = {
                id: targetNodeId,
                name: targetNodeId,
                target: true,
                couplingClass: targetClass,
                couplingMethods: [targetMethodFullName],
                couplingLinkMap: {
                    [sourceNodeId]: [targetMethodFullName]
                },
                val: 0.4,
                color: '#2E8484'
            };
        }

        data.links.push({
            source: sourceNodeId,
            target: targetNodeId,
            curvature: 0.1
        });

        // Increment summary data
        linesProcessed++;
    }

    // Read a single line
    lineReader.on('line', function(line) {
        let row;

        // Attempt row parse
        try {
            row = JSON.parse(line);
        } catch (e) {
            logError(`Failed to parse JSON line: "${line}".  Continuing...`, e);
            return;
        }

        processDataRow(row, data, 'sourceClass');
        processDataRow(row, data, 'sourceClass-targetClass');
    });

    lineReader.on('close', async () => {
        function postProcess(_data, groupBy) {
            const data = _data[groupBy];
            data.nodes = Object.keys(data.nodesMap).map((nodesMapKey) => {
                return data.nodesMap[nodesMapKey];
            });
            for (let i = 0; i < data.nodes.length; i++) {
                data.nodes[i].couplingMethodUsages = convertToUsageCount(data.nodes[i].couplingMethods);
                delete data.nodes[i].couplingMethods; // clean up
            }
            delete data.nodesMap; // clean up
        }

        postProcess(data, 'sourceClass');
        postProcess(data, 'sourceClass-targetClass');

        // Finish procesing data
        done({
            data,
            dataName,
            linesProcessed
        });
    });
}

/**
 * processFiles()
 * Given an array of files from jarviz output, process data into node/links and render using webpack
 * @param {array} files - array of file locations
 * @param {string} outputPath - absolute path to output HTML graph
 */
function processFiles(files, outputPath) {
    if (files.length > 0) {
        const file = files.pop();
        processData(file, async ({data, dataName, linesProcessed}) => {
            await writeToDisk({data, dataName, outputPath});
            logSuccess(`Processed ${linesProcessed} rows and wrote to disk "${outputPath}"`);
            // Process next file
            processFiles(files, outputPath);
        });
    }
}

/**
 * processAll()
 * Processes all files under a given data path
 * @param {string} inputPath - absolute path to input jarviz JSON
 * @param {string} outputPath - absolute path to output HTML graph
 */
function processAll(inputPath, outputPath) {
    const dataFiles = glob.sync(path.join(inputPath, '*.jsonl'));
    log(`processing ${dataFiles} -> ${outputPath}`);
    if (dataFiles.length === 0) {
        logCritical(`Could not find any files match *.jsonl at path: ${inputPath}`);
        return;
    }
    // process files
    processFiles(dataFiles, outputPath);
}

module.exports = {
    processData,
    processFiles,
    processAll
};
