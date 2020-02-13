/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {
  StyleSheet,
  ScrollView,
  TextInput,
  View,
  Text,
  StatusBar,
  Button,
  Alert,
} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import MapPage from './components/MapPage';
const Stack = createStackNavigator();


class Home extends Component {
  render() {
    return (
      <View
        style={styles.scrollView}>
        <Text style={styles.textView}>
         Enter map id
       </Text>
       <TextInput
            style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
            onChangeText={mapId => this.setState({mapId})}
        />
        <Button
          title="Go"
          onPress={() => this.props.navigation.navigate('MapPage', {mapId: this.state.mapId})}
        />
      </View>
    );
  }
}


class App extends Component {
  render() {
    return (
      <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={Home}
        />
        <Stack.Screen name="MapPage" component={MapPage} />
      </Stack.Navigator>
    </NavigationContainer>
    );
  }
}



const styles = StyleSheet.create({
  scrollView: {
    padding: 20,
    justifyContent: 'center',
  },
  textView: {
    fontSize: 20,
  }
});

export default App;
