/**
 * @file rtsp流播放界面
 * @date 2019/6/25 10:31
 * @author lsfern
 * @lastModify lsfern 2019/6/25 10:31
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
import RCTGsyView from "../rct/RCTGsyUserView";

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
let gsyModule = NativeModules.GsyModule;
export default class GsyUserPage extends Component<Props> {
    constructor(props) {
        super(props);
        this.state = {
            isFullScreen: false,
            currentState: CURRENT_STATE_NORMAL,
            currentPosition: -1,
            duration: -1,
            imgName:"111"
        }
    }

    componentDidMount(): void {

        this.currentState = DeviceEventEmitter.addListener('currentState', (msg) => {
            if (msg) {
                this.setState({currentState: msg.currentState})
            }
        });
        this.videoProgress = DeviceEventEmitter.addListener('videoProgress', (msg) => {
            if (msg) {
                this.setState({currentPosition: msg.currentPosition, duration: msg.duration})
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
                gsyModule.pause();
            } else {
                gsyModule.resume();
            }
        });
    }

    componentWillUnmount(): void {
        BackHandler.removeEventListener('hardwareBackPress', this.onBackAndroid);
        this.currentState.remove();
        this.videoProgress.remove();
        this.appState.remove();
        this.isFullScreen.remove();
        gsyModule.stopAndRelease();
    }

    onBackAndroid = () => {
        if (this.state.isFullScreen) {
            gsyModule.backAndroid();
            return true;
        }
        Actions.pop();
        return true;
    };

    _setScreen = () => {
        const currentState = this.state.currentState;
        if(currentState === CURRENT_STATE_ERROR){
            alert('播放失败，请重试');
            return;
        }
        this.setState({isFullScreen: true,}, () => {
            gsyModule.setFullScreen();
        })
    };
    _setTitle = () => {
        const currentState = this.state.currentState;
        if (currentState === CURRENT_STATE_PLAYING) {
            return '暂停';
        } else if (currentState === CURRENT_STATE_PAUSE) {
            return '恢复播放';
        } else if (currentState === CURRENT_STATE_PLAYING_BUFFERING_START) {
            return '缓冲中';
        } else if (currentState === CURRENT_STATE_ERROR) {
            alert('播放失败，请重试');
            return '播放';
        } else if (currentState === CURRENT_STATE_AUTO_COMPLETE) {
            return '播放完毕';
        } else {
            return '播放';
        }
    };
    millisecondToDate = (msd) => {
        let time = parseFloat(msd) / 1000;   //先将毫秒转化成秒
        if (null != time && "" !== time) {
            if (time > 60 && time < 60 * 60) {
                time = parseInt(time / 60.0) + ":" + parseInt((parseFloat(time / 60.0) -
                    parseInt(time / 60.0)) * 60);
            } else if (time >= 60 * 60 && time < 60 * 60 * 24) {
                time = parseInt(time / 3600.0) + ":" + parseInt((parseFloat(time / 3600.0) -
                    parseInt(time / 3600.0)) * 60) + ":" +
                    parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
                        parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60);
            } else {
                time = "00:" + parseInt(time);
            }
        }
        return time;
    };

    render() {
        return (
            <View>
                <RCTGsyView ref={(gsyUserView) => {
                    this.gsyUserView = gsyUserView;
                }} style={{width: width, height: 200}} setPlayRtsp={this.props.data}
                />
                <View style={{flexDirection: "row", justifyContent: "space-between"}}>
                    <View style={{marginTop: 10}}>
                        <TouchableOpacity onPress={() => gsyModule.play()}>
                            <Text
                                style={{fontSize: 20, color: "#f00"}}
                            >{this._setTitle()}</Text>
                        </TouchableOpacity>
                    </View>
                    <View style={{marginTop: 10}}>
                        <Button
                            onPress={() => this._setScreen()}
                            title="全屏"
                            color="#841584"
                        />
                    </View>
                    <View style={{marginTop: 10}}>
                        <Button
                            onPress={() => {
                                const currentState = this.state.currentState;
                                if(currentState === CURRENT_STATE_ERROR){
                                    alert('播放器错误，暂无法截图');
                                    return;
                                }
                                gsyModule.shotImage("GsyRnDemo",this.state.imgName+new Date())
                            }}
                            title="截图"
                            color="#841584"
                        />
                    </View>

                </View>
                <View style={{justifyContent: 'center', alignItems: 'center'}}>
                    {
                        this.state.currentPosition !== -1 && this.state.duration !== -1 ?
                            <View style={{marginTop: 10}}>
                                <Text
                                    style={{fontSize: 20, color: "#00f"}}
                                >{this.millisecondToDate(this.state.currentPosition)}/{this.millisecondToDate(this.state.duration)}</Text>
                            </View> : <Text
                                style={{fontSize: 20, color: "#00f"}}
                            >00:00/00:00</Text>
                    }
                </View>
            </View>
        );
    }
}
