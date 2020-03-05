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

/**
 * This will take a list of strings and generate a new set with the unique string values in alphabetical order
 * with their counts.
 * e.g.:
 * "foo", "bar", "bar", "foo", "foo", "abc" -> "abc", "bar (x2)", "foo (x3)"
 * @param {*} listOfStrings The list to be processed
 */
function convertToUsageCount(listOfStrings) {
    if (!listOfStrings) {
        return [];
    }

    const map = {};
    listOfStrings.forEach((e) => {
        if (map[e]) {
            map[e] = map[e] + 1;
        } else {
            map[e] = 1;
        }
    });

    const sortedList = sortMapByValuesIntoList(map, false).map((e) => {
        return e[1] > 1 ? `${e[0]} (x${e[1]})` : e[0];
    });

    return sortedList;
}

/**
 * This will transform a given string to be safe to use in HTML
 * by escaping special characters with HTML friendly values.
 * @param {*} unsafe The string to be HTML escaped
 */
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

/**
 * This will sort a given map using the values and return as a list of key-value pairs.
 * The sorting order is determined by the ascending parameter.
 * @param {*} obj The map to be sorted by its values
 * @param {*} ascending Determined sorting order
 */
function sortMapByValuesIntoList(obj, ascending = true) {
    // convert object into array
    const sortable = [];
    Object.keys(obj).map((key) => {
        sortable.push([key, obj[key]]);
    });

    // sort items by value
    sortable.sort(function(a, b) {
        const x = a[1];
        const y = b[1];
        if (ascending) {
            return x < y ? -1 : x > y ? 1 : 0;
        } else {
            return x > y ? -1 : x < y ? 1 : 0;
        }
    });
    return sortable; // a list of lists
}

/**
 * This will extract the simple class name from a fully qualified Java class name.
 * e.g.: com.expedia.jarviz.Hello -> Hello
 * @param {*} className
 */
function extractSimpleClassName(className) {
    const id = className ? className.lastIndexOf('.') : -1;
    if (id >= 0) {
        return className.substring(id);
    }
    return className;
}

module.exports = {
    convertToUsageCount,
    sortMapByValuesIntoList,
    extractSimpleClassName,
    escapeHtml
};
