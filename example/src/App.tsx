import React from 'react'
import { Text, View } from 'react-native'
//@ts-ignore
import { bundleCode } from '../package.json'

const App = () => {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' , backgroundColor : 'white' }}>
      <Text>bundle code: {bundleCode}</Text>
    </View>
  )
}

export default App
