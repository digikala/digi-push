/**
 * @format
 */

import { AppRegistry } from 'react-native'
import { name as appName } from './app.json'
import App from './src/App'
import DigiCodePush from '@dkmobile/digi-push'
import { bundleCode } from './package.json'
import VersionInfo from 'react-native-version-info'
import { Platform } from 'react-native'

DigiCodePush.checkForUpdates(
  `https://demo-sirius.digikala.com/v1/bundle/?code=${bundleCode}`,
  {
    headers: {
      Client: Platform.OS,
      ApplicationVersion: VersionInfo.buildVersion.toString(),
    },
  }
)

AppRegistry.registerComponent(appName, () => App)
