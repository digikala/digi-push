# digikala/digi-push for React Native

**digikala/digi-push** is a derived from [Code Push](https://microsoft.github.io/code-push/). Taking into account the solution to the problems of downloading and uploading the bundle and the desired changes for the users devices.

## Platforms Supported

- [x] iOS
- [x] Android

## Usage

**Installation**

```
npm i @digikala/digi-push --registry https://npm.pkg.github.com
```

If you are familiar with [CodePush](https://microsoft.github.io/code-push/), you already know how to use `digikala/digi-push`.

**android**

Override `getJSBundleFile` method in `MainApplicaion`:

```java
@Override
protected String getJSBundleFile() {
    return DigiCodePush.INSTANCE.getJSBundleFilePath(MainApplication.this, super.getJSBundleFile());
}
```

**ios**

Override `sourceURLForBridge` method in `AppDelegate`

```objc
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

Import the `DigiCodePush` from `@digikala/digi-push` and use `DigiCodePush.checkForUpdates` to check and get if new version of bundle exist.

```jsx
import { AppRegistry } from 'react-native'
import { name as appName } from './app.json'
import App from './src/App'
import DigiCodePush from '@digikala/digi-push'

DigiCodePush.checkForUpdates(
  server_url, //your desired server url
  request_options //your desired request options like headers and ...
)

AppRegistry.registerComponent(appName, () => App)
```

> **Warning**
> The response of your desired server should be like this:

```json
{
  "status": 200,
  "data": {
    "bundle": {
      "bundle_url": "platform specific bundle.zip"
    }
  }
}
```

## CLI

Command Line Interface (CLI) for `digikala/digi-push`.

**Bundle**

To create the desired bundle to be set on the server, enter the following command in the root of the project:

```
npx digi-push bundle -o OUTPUT_PATH -p PLATFORM
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

## Versioning

This project follows [semantic versioning](https://semver.org/).

**Upcoming:**

Feature is coming :))))

[lean-core-issue]: https://github.com/facebook/react-native/issues/23313
