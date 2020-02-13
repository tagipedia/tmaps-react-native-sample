import PropTypes from 'prop-types';
import {requireNativeComponent, ViewPropTypes} from 'react-native';

var MapView = {
  propTypes: {
    mapId: PropTypes.string,
    tenants: PropTypes.array,
    featureId: PropTypes.string,
    primaryColor: PropTypes.string,
    secondaryColor: PropTypes.string,
    routeTo: PropTypes.string,
    ...ViewPropTypes, // include the default view properties
  },
};

module.exports = requireNativeComponent('TGMapView', MapView);
