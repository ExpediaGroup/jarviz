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

const cssnano = require('cssnano');
const HtmlWebpackInlineSourcePlugin = require('html-webpack-inline-source-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const SystemBellPlugin = require('system-bell-webpack-plugin');

const pkg = require('./package.json');
const {processData} = require('./lib/index');

const devMode = process.env.NODE_ENV !== 'production';

module.exports = {
    devtool: devMode ? 'source-map' : 'none',
    mode: devMode ? 'development' : 'production',
    entry: path.join(__dirname, 'lib/client/index.js'),
    output: {
        path: path.join(__dirname, 'build/client'),
        filename: `jarviz-client.js`
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                loader: 'babel-loader',
                exclude: /node_modules/
            },
            {
                test: /\.less$/,
                use: [
                    devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
                    'css-loader',
                    {
                        loader: 'postcss-loader',
                        options: {
                            ident: 'postcss',
                            plugins: [cssnano({safe: true})]
                        }
                    },
                    {
                        loader: 'less-loader'
                    }
                ]
            },
            {
                test: /\.css$/,
                loaders: ['style-loader', 'css-loader']
            },
            {
                test: /\.png$/,
                loader: 'url-loader?limit=999999999999&mimetype=image/png'
            },
            {
                test: /\.jpg$/,
                loader: 'file-loader'
            },
            {
                test: /\.svg$/,
                loader: 'url-loader?limit=999999999999'
            },
            {
                test: /\.eot$/,
                loader: 'url-loader?limit=100000'
            },
            {
                test: /\.ttf$/,
                loader: 'url-loader?limit=100000'
            },
            {
                test: /\.js$/,
                loader: 'eslint-loader',
                query: {
                    emitWarning: true,
                    quiet: true
                },
                exclude: /node_modules|tests/
            }
        ]
    },
    plugins: [
        new SystemBellPlugin(),
        new HtmlWebpackPlugin({
            inlineSource: devMode ? '' : '.(js|css)$',
            prerender: true,
            title: `${pkg.name} - ${pkg.description}`,
            filename: devMode ? 'index.html' : 'jarviz-graph.html',
            template: path.join(__dirname, 'lib/client/index.html'),
            templateParameters: {
                jarvizData: devMode ? 'false' : '{{{JARVIZ_DATA}}}'
            },
            showErrors: devMode
        }),
        new HtmlWebpackInlineSourcePlugin()
    ],
    devServer: {
        contentBase: path.join(__dirname, 'lib/client'),
        port: 8080,
        open: true,
        before: function(app) {
            app.get('/data', function(req, res) {
                const fileName = req.query.name || 'jarviz-graph-data-1';
                console.log(`Requesting "${fileName}"`);
                processData(path.join(__dirname, `lib/mock/${fileName}.jsonl`), ({data, dataName}) => {
                    console.log('Processed', dataName);
                    res.json({data});
                });
            });
        }
    }
};
