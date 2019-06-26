/**
 * @file 视频播放界面
 * @date 2019/6/25 11:32
 * @author lsfern
 * @lastModify lsfern 2019/6/25 11:32
 */
"use strict";
import React, {Component} from 'react';
import {
    BackHandler,
    Button,
    DeviceEventEmitter,
    Dimensions,
    NativeModules,
    Text,
    TouchableOpacity,
    View
} from 'react-native';
import {Actions} from "react-native-router-flux";
import RCTGsyVideoView from "../rct/RCTGsyVideoView";

const {width, height} = Dimensions.get('window');

//正常
const CURRENT_STATE_NORMAL = 0;
//准备中
const CURRENT_STATE_PREPAREING = 1;
//播放中
const CURRENT_STATE_PLAYING = 2;
//开始缓冲
const CURRENT_STATE_PLAYING_BUFFERING_START = 3;
//暂停
const CURRENT_STATE_PAUSE = 5;
//自动播放结束
const CURRENT_STATE_AUTO_COMPLETE = 6;
//错误状态
const CURRENT_STATE_ERROR = 7;
let gsyVideoModule = NativeModules.GsyVideoModule;
export default class GsyUserPage extends Component<Props> {
    constructor(props) {
        super(props);
        this.state = {
            isFullScreen: false,
            currentState: CURRENT_STATE_NORMAL,
        }
    }

    componentDidMount(): void {

        this.currentState = DeviceEventEmitter.addListener('currentState', (msg) => {
            if (msg) {
                this.setState({currentState: msg.currentState})
            }
        });
        this.isFullScreen = DeviceEventEmitter.addListener('isFullScreen', (msg) => {
            if (msg) {
                this.setState({isFullScreen: msg.isFullScreen})
            }
        });
        BackHandler.addEventListener('hardwareBackPress', this.onBackAndroid);
        //应用处于后台停止播放并重置播放状态
        this.appState = DeviceEventEmitter.addListener("isBackground", (result) => {
            if (result) {
                gsyVideoModule.pause();
            } else {
                gsyVideoModule.resume();
            }
        });
    }

    componentWillUnmount(): void {
        BackHandler.removeEventListener('hardwareBackPress', this.onBackAndroid);
        this.currentState.remove();
        this.appState.remove();
        this.isFullScreen.remove();
        gsyVideoModule.stopAndRelease();
    }

    onBackAndroid = () => {
        if (this.state.isFullScreen) {
            gsyVideoModule.backAndroid();
            return true;
        }else{
            Actions.pop();
            return false;
        }
    };

    render() {
        return (
            <View>
                <View style={{
                    width: width,
                    height: 50,
                    backgroundColor: "#f00",
                    justifyContent: 'center',
                    alignItems: 'center'
                }}>
                    <Text style={{fontSize: 15, color: "#00f"}}>标题栏</Text>
                </View>
                <RCTGsyVideoView ref={(gsyVideoView) => {
                    this.gsyVideoView = gsyVideoView;
                }} style={{width: width, height: height}} setPlayVideo={this.props.data}
                />
            </View>
        );
    }
}
