/**
 * @file 首页
 * @date 2019/5/30 9:17
 * @author ZWW
 * @lastModify ZWW 2019/5/30 9:17
 */
import React, {Component} from 'react';
import {Alert, Button, NativeModules, TextInput, View} from 'react-native';
import {Actions} from "react-native-router-flux";
import PermissionManager from '../util/PermissionManager';

export default class HomePage extends Component<Props> {
    constructor(props) {
        super(props);
        this.state = {
            inputRstp: "",
            userData: {
                playUrl: "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov",
                videoTitle: "测试Rtsp",
            },

            inputVideo: "",
            videoData: {
                playUrl: "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
                smallTitle: "测试小视频",
                smallTitleColor:"#000fff",
                smallTitleSize: 40,
                bigTitle: "测试大视频",
                bigTitleColor: "#ff0000",
                bigTitleSize: 30,
                isLoop: true,
            }
        }
    }

    componentDidMount() {
        PermissionManager.checkNeedPermission(1);
    }

    render() {
        return (
            <View style={{margin: 10}}>
                <View style={{marginTop: 30}}>
                    <TextInput
                        style={{height: 40, borderColor: 'gray', borderWidth: 1}}
                        onChangeText={(text) => this.setState({inputRstp: text})}
                        value={this.state.inputRstp}
                        placeholder={"请输入rtsp地址,为空则是默认地址"}
                    />
                    <Button
                        onPress={() => {
                            PermissionManager.checkNeedPermission(1).then(isAllow => {
                                if (isAllow) {
                                    PermissionManager.checkNeedPermission(3).then(isAllow => {
                                        if (isAllow) {
                                            let inputRtsp = this.state.inputRstp;
                                            let data = this.state.userData;
                                            if (inputRtsp) {
                                                let newData = {};
                                                newData.playUrl = inputRtsp;
                                                newData.videoTitle = data.videoTitle;
                                                this.setState({inputRstp: ''}, () => {
                                                    Actions.gsyUserPage({"data": newData});
                                                });
                                                return;
                                            }
                                            this.setState({inputRstp: ''}, () => {
                                                Actions.gsyUserPage({"data": data});
                                            });
                                        }
                                    })
                                }
                            })

                        }}
                        title="播放rtsp流"
                        color="#841584"
                        style={{marginTop: 20}}
                    />
                </View>

                <View style={{marginTop: 50}}>
                    <Button
                        onPress={() => {
                            PermissionManager.checkNeedPermission(1).then(isAllow => {
                                if (isAllow) {
                                    PermissionManager.checkNeedPermission(3).then(isAllow => {
                                        if (isAllow) {
                                            Actions.gsyVideoPage({"data": this.state.videoData});
                                        }
                                    })
                                }
                            })

                        }}
                        title="播放视频"
                        color="#841584"
                        style={{marginTop: 20}}
                    />
                </View>
            </View>
        );
    }
}
