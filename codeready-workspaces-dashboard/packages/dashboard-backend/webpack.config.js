const path = require('path');
const webpack = require('webpack');
const nodeExternals = require('webpack-node-externals');

const config = {
    entry: path.join(__dirname, 'src/index.ts'),
    devtool: 'source-map',
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: [
                    {
                        loader: 'ts-loader',
                    }
                ],
                exclude: /node_modules/
            }
        ]
    },
    resolve: {
        extensions: ['.ts', '.js'],
    },
    resolveLoader: {},
    plugins: [
        new webpack.ProgressPlugin(),
    ],
    target: 'node',
    externals: [nodeExternals()],
    output: {
        filename: 'server.js',
        library: 'dashboard-backend',
        libraryTarget: 'umd',
        globalObject: 'this',
        path: path.resolve(__dirname, 'lib')
    },
};

module.exports = (env = {}) => {
  if (env.yarnV1 === 'true') {
    console.log('\nStart building the package assuming that yarn v1 will be used...\n');
  } else {
    console.log('\nStart building the package assuming that yarn v2 will be used...\n');

    const PnpPlugin = require('pnp-webpack-plugin');
    config.resolve.plugins = [PnpPlugin];
    config.resolveLoader.plugins = [PnpPlugin.moduleLoader(module)];
  }

  return config;
};
