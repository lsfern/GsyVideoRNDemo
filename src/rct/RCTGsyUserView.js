/**
 * @file 自定义gsy播放样式，播放rtsp实时流
 * @date 2019/6/25 10:24
 * @author lsfern
 * @lastModify lsfern 2019/6/25 10:24
 */
import React, {PureComponent} from 'react';
import {Dimensions, requireNativeComponent} from 'react-native';

const GsyUserView = requireNativeComponent('RCTGsyUserView');
export default class RCTGsyUserView extends PureComponent {
    render(): React.ReactNode {
        return <GsyUserView style={{width: Dimensions.get('window').width, height: 300}}
                            {...this.props}/>;
    }
}
