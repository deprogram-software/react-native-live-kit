/**
 * Created by ltjin on 16/9/7.
 */

import React, { Component } from 'react';
import { requireNativeComponent } from 'react-native';

var Live = requireNativeComponent('RCTLiveView', YYLive);

export default class YYLive extends Component {
    static propTypes = {
        stream: React.PropTypes.object.isRequired
    }

    render() {
        return <Live {...this.props}/>;
    }
}