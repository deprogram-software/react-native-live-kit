/**
 * Created by ltjin on 16/8/31.
 */
import React, { Component } from 'react';
import { requireNativeComponent } from 'react-native';

var Player = requireNativeComponent('RCTPlayerView', YYPlayer);

export default class YYPlayer extends Component {
    static propTypes = {
        uri: React.PropTypes.string.isRequired
    }

    render() {
        return <Player {...this.props}/>;
    }
}