# digi-push for React Native

**CodePush**  is a cloud service that enables Cordova and React Native developers to deploy mobile app updates directly to their users devices..

**digikala/digi-push**  is a derived from CodePush. Taking into account the solution to the problems of downloading and uploading the bundle and the desired changes for the users devices.

## Platforms Supported

- [x] iOS
- [x] Android

## Getting Started

```
npm install @digikala/digi-push --registry https://npm.pkg.github.com
```

## Versioning

This project follows [semantic versioning](https://semver.org/).

**Breaking History:**

Current Version: V1.2.1

**Upcoming:**

Feature is coming :))))

## Usage

If you are familiar with [CodePush](https://microsoft.github.io/code-push/), you already know how to use `digikala/digi-push`.

**android**
Add this function in `MainApplicaion`:
```jsx
        override fun getJSBundleFile(): String? {
            return DigiCodePush.getJSBundleFilePath(this@MainApplication,super.getJSBundleFile())
        }
```
**ios**
Add line 8 in `sourceURLForBridge `
```jsx
- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
 
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
#else
 
  return  [DigiCodePush bundleURLWithDefaultUrl:[[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"]];
 
#endif
}
```
**React Native**
Import the `DigiCodePush ` component from `@digikala/digi-push` and use it like so and It should be noted that for line 10  you must enter your desired domain:
> **Warning**
> The response of your desired server should be like this:
```json
{
  "status": 200,
  "data": {
    "bundle": {
      "bundle_url": "https://dkstatics-public.digikala.com/digikala-static/acc9393fc392da30528749393e78bcf78caf3e93_1657447109.zip"
    }
  }
}
```

```jsx
import { AppRegistry } from 'react-native'
import { name as appName } from './app.json'
import App from './src/App'
import DigiCodePush from '@digikala/digi-push'
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
```
**Bundle**
To create the desired bundle to be set on the server, enter the following command in the root of the project:
```
npx digi-push bundle -o ~/Desktop -p android
```


## Contributors

Thanks goes to these wonderful people:

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
<table><tr>
<td align="center"><a href="https://github.com/MostafaTaghipour"><img src="https://avatars.githubusercontent.com/u/18639408?v=4" width="100px;" alt="Mostafa Taghipour"/><br /><sub><b>Mostafa Taghipour</b></sub></a><br /><a href="https://github.com/MostafaTaghipour" </a></td>
<td align="center"><a href="https://github.com/mldb"><img src="https://avatars.githubusercontent.com/u/8201960?v=4" width="100px;" alt="Milad Bagheri"/><br /><sub><b>Milad Bagheri</b></sub></a><br /><a href="https://github.com/mldb" </a></td>
</tr></table>

<!-- ALL-CONTRIBUTORS-LIST:END -->




[lean-core-issue]: https://github.com/facebook/react-native/issues/23313
