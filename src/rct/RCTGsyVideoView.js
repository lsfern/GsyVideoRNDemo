/**
 * @file 播放视频
 * @date 2019/6/25 11:21
 * @author lsfern
 * @lastModify lsfern 2019/6/25 11:23
 */
import React, {PureComponent} from 'react';
import {Dimensions, requireNativeComponent} from 'react-native';

const GsyVideoView = requireNativeComponent('RCTGsyVideoView');
export default class RCTGsyVideoView extends PureComponent {
    render(): React.ReactNode {
        return <GsyVideoView style={{width: Dimensions.get('window').width, height: 300}}
                            {...this.props}/>;
    }
}
