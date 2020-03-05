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
import ReactDOM from 'react-dom';

import LOGO from './jarviz-logo.png';
import JarvizGraph from './graph.js';
import './index.css';

const jarvizData = window.data;

const defaultGrouping = 'sourceClass';

const groupingText = {
    sourceClass: 'Field Level Dependencies Clustered by Applications',
    targetClass: 'Applications Clustered by Target Dependencies'
};

class MainView extends React.Component {
    state = {
        data: false,
        grouping: defaultGrouping,
        titleText: groupingText[defaultGrouping],
        renderMode: '2d'
    };

    componentDidMount = () => {
        if (!jarvizData) {
            window
                .fetch('/data')
                .then((response) => {
                    return response.json();
                })
                .then((json) => {
                    this.setState({
                        data: json.data
                    });
                });
        } else {
            this.setState({
                data: jarvizData
            });
        }
    };

    handleRenderSelectChange = (ev) => {
        var renderMode = ev.target.value;
        this.setState({
            renderMode
        });
    };

    handleGroupSelectChange = (ev) => {
        var grouping = ev.target.value;

        this.setState({
            grouping,
            titleText: groupingText[grouping]
        });

        console.log('Change Grouping To:', grouping);
        this.graph.draw(grouping);
    };

    handleColorSelection = (colorKey) => {
        this.setState({
            selectedColorKey: colorKey
        });
    };

    getData = () => {
        return this.state.data && this.state.data[this.state.grouping];
    };

    render() {
        const data = this.getData();
        return (
            <div>
                <img id="jarviz-logo" src={LOGO} />
                <div id="title">
                    <h3>{this.state.titleText}</h3>
                </div>
                <div id="filters">
                    <label htmlFor="render-by-select">Render mode: </label>
                    <select id="render-by-select" onChange={this.handleRenderSelectChange}>
                        <option value="2d">2d</option>
                        <option value="3d">3d</option>
                        <option value="vr">vr</option>
                    </select>
                    <label htmlFor="group-by-select">Cluster by: </label>
                    <select id="group-by-select" onChange={this.handleGroupSelectChange}>
                        <option value="sourceClass">Application</option>
                        <option value="sourceClass-targetClass">Target Dependency</option>
                    </select>
                </div>
                <legend id="legend">
                    {data &&
                        data.colorMap &&
                        Object.keys(data.colorMap).map((key) => {
                            return (
                                <span className="legend-container" key={key} onClick={this.handleColorSelection.bind(null, key)}>
                                    <label className="legend-color" style={{backgroundColor: data.colorMap[key]}}></label>
                                    <label className="legend-text">{key}</label>
                                </span>
                            );
                        })}
                </legend>
                <JarvizGraph data={this.state.data} grouping={this.state.grouping} renderMode={this.state.renderMode} />
            </div>
        );
    }
}

document.addEventListener('DOMContentLoaded', () => {
    ReactDOM.render(<MainView />, document.querySelector('#content'));
});
