import React, { Component } from 'react';
import {
  View,
  StyleSheet,
  TouchableOpacity,
  UIManager,
  findNodeHandle,
  DeviceEventEmitter
} from 'react-native';

import MapComponent from './MapComponent'

export default class MapPage extends Component {

  constructor(props) {
    super(props);
    this.state = props.route.params
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.mapContainer}>
          <MapComponent ref= "mapView" mapId = {this.state.mapId} tenants={this.props.tenants} featureId={this.props.featureId || null}
            routeTo={this.props.routeTo || null} primaryColor={this.props.theme?.primary_color} secondaryColor={this.props.theme?.secondary_color} />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container:{
    flex: 1,
  },
  mapContainer: {
    flex: 1
  }
});
