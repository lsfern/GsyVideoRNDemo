import React, {Component} from 'react';
import Routers from './src/Routers';

export default class App extends Component<Props> {
  constructor(props) {
    super(props);
  }
  render() {
    return (
        <Routers/>
    );
  }
}
