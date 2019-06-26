
"use strict";
import React, {Component} from "react";
import {Actions, Router, Scene} from "react-native-router-flux";
import {AppState, DeviceEventEmitter} from "react-native";
import HomePage from './view/HomePage';
import GsyUserPage from './view/GsyUserPage';
import GsyVideoPage from './view/GsyVideoPage';

class routers extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentAppState: AppState.currentState,
        }
    }
    shouldComponentUpdate(){
        return false;
    }

    componentDidMount() {
        AppState.addEventListener('change', this._handleAppStateChange);
    }

    componentWillUnmount() {
        AppState.removeEventListener('change', this._handleAppStateChange);
    }

    /**
     * 监听应用前后台
     * @param nextAppState
     * @private
     */
    _handleAppStateChange = (nextAppState) => {
        if (!this.state.currentAppState.match(/inactive|background/) && nextAppState !== 'active') {
            DeviceEventEmitter.emit("isBackground", true);
        } else {
            DeviceEventEmitter.emit("isBackground", false);
        }
        this.setState({currentAppState: nextAppState})
    };


    render() {
        return (
            <Router backAndroidHandler={this.backAndroidHandler}>
                <Scene key="root" hideNavBar>
                    <Scene key="homePage" component={HomePage} hideNavBar/>
                    <Scene key="gsyUserPage" component={GsyUserPage} hideNavBar/>
                    <Scene key="gsyVideoPage" component={GsyVideoPage} hideNavBar/>
                </Scene>
            </Router>
        );
    }
}

export default routers
