import React, { Component } from 'react';
import {
  StyleSheet,
  } from 'react-native';
import MapView from '../NativeComponents/Map'

export default class MapComponent extends Component {
  render() {
    return (
      <MapView ref={this.props.ref} style={styles.innerContainer} mapId={this.props.mapId} tenants={this.props.tenants} featureId={this.props.featureId}
        primaryColor={this.props.primaryColor} routeTo={this.props.routeTo}
        secondaryColor={this.props.secondaryColor} />
    );
  }
}

const styles = StyleSheet.create({
  innerContainer: {
    flex: 1,
  },
});
