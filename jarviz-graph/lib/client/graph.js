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

import React from 'react';
import {ForceGraph2D, ForceGraph3D, ForceGraphVR} from 'react-force-graph';
import './graph.css';

let tooltip = null;
const TWO_PI = 2 * Math.PI;
const NODE_R = 5;
const SOURCE_NODE_HIGHLIGHT_R = NODE_R * 1.7;
const TARGET_NODE_HIGHLIGHT_R = NODE_R * 0.9;
let highlightNodes = [];
let highlightLink = null;

function initTooltip() {
    tooltip = document.createElement('div');
    tooltip.classList.add('graph-tooltip-custom');
    tooltip.style.opacity = 0;
    document.body.appendChild(tooltip);
    tooltip.show = function(content, position) {
        tooltip.style.opacity = 1;
        tooltip.style.left = position.x + 'px';
        tooltip.style.top = position.y + 'px';
        tooltip.innerHTML = content;
    };
    tooltip.hide = function() {
        tooltip.style.opacity = 0;
    };
}

export default class JarvizGraph extends React.Component {
    state = {
        centerAt: false
    };
    constructor(props) {
        super(props);
        this.graphElement = React.createRef();
        this.forceGraphEl = React.createRef();
    }
    render() {
        if (!this.props.data) {
            return null;
        }
        const data = this.props.data[this.props.grouping];
        if (!data) {
            return null;
        }
        let Graph = false;
        switch (this.props.renderMode) {
            case 'vr':
                Graph = ForceGraphVR;
                break;
            case '3d':
                Graph = ForceGraph3D;
                break;
            case '2d':
            default:
                Graph = ForceGraph2D;
                break;
        }
        return (
            <div id="graph" ref={this.graphElement}>
                <Graph
                    ref={this.forceGraphEl}
                    graphData={data}
                    nodeRelSize={NODE_R}
                    centerAt={this.state.centerAt}
                    linkWidth={this.props.renderMode !== 'vr' ? (link) => (link === highlightLink ? 4 : 1) : 1}
                    nodeLabel={(node) => {
                        var html = '';
                        if (!node.target) {
                            html += '<p class="grid-item">App:</p><p class="grid-item">' + node.applicationName + '</p>';
                            html += '<p class="grid-item">Artifact:</p><p class="grid-item">' + node.artifact + '</p>';
                        }

                        html += '<p class="grid-item">Methods:</p><p class="grid-item">' + node.couplingMethodUsages.join('<br />') + '</p>';
                        return html;
                    }}
                    linkLabel={(link) => {
                        var html = '';
                        if (!link.source.target) {
                            html += '<p class="grid-item">App:</p><p class="grid-item">' + link.source.applicationName + '</p>';
                            html += '<p class="grid-item">Artifact:</p><p class="grid-item">' + link.source.artifact + '</p>';
                        }

                        html +=
                            '<p class="grid-item">Source Methods:</p>' +
                            '<p class="grid-item">' +
                            link.source.couplingLinkMap[link.target.id].join('<br />') +
                            '</p>';

                        if (link.source.target) {
                            html += '<p class="grid-item">Target Class:</p><p class="grid-item">' + link.target.couplingClass + '</p>';
                        }
                        html +=
                            '<p class="grid-item">Target Methods:</p>' +
                            '<p class="grid-item">' +
                            link.target.couplingLinkMap[link.source.id].join('<br />') +
                            '</p>';

                        return html;
                    }}
                    onNodeHover={(node) => {
                        this.graphElement.current.style.cursor = node ? 'pointer' : null;
                        highlightNodes = node ? [node] : [];
                    }}
                    onNodeClick={(node) => {
                        // Center/zoom on node
                        this.forceGraphEl.current.centerAt(node.x, node.y, 1000);
                        this.forceGraphEl.current.zoom(3, 2000);
                    }}
                    onLinkHover={(link) => {
                        highlightLink = link;
                        highlightNodes = link ? [link.source, link.target] : [];
                    }}
                    nodeCanvasObjectMode={(node) => (highlightNodes.indexOf(node) !== -1 ? 'before' : undefined)}
                    nodeCanvasObject={(node, ctx) => {
                        const nodeHighlightR = node.target ? TARGET_NODE_HIGHLIGHT_R : SOURCE_NODE_HIGHLIGHT_R;
                        // add ring just for highlighted nodes
                        ctx.beginPath();
                        ctx.arc(node.x, node.y, nodeHighlightR, 0, TWO_PI);
                        ctx.fillStyle = 'black';
                        ctx.fill();
                    }}
                    nodeAutoColorBy={'colorBy'}
                    linkCurvature={'curvature'}
                    linkDirectionalArrowLength={2}
                    linkDirectionalArrowRelPos={1}
                />
            </div>
        );
    }
}
