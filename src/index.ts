import { NativeModules } from 'react-native'

// import native module
const { RNDigiCodePushModule: DigiCodePush } = NativeModules

/**
 * check app run in production mode
 *
 * @returns boolean
 */
const checkIsProductionMode = (): boolean => {
  return !__DEV__
}

/**
 * Check for updates
 * check bundle version and download and s
 * et new bundle if new version exist
 *
 * @param url url of code push api
 * @param options include headers, et cetera
 */
const checkForUpdates = async (url: string, options?: Object) => {
  // only production mode suppurted
  if (!checkIsProductionMode()) return

  // get active bundle url from server
  const bundle = await getActiveBundleUrl(url, options)

  /*  
      if there is no bundle url that mean 
      current bundle is last version 
      and bundle shouldn't be update
  */
  if (!bundle) return

  // download bundle file
  const bundlePath = await downloadBundle(bundle.url)

  // set new bundle and reload bridge to use new bundle
  if (bundlePath) {
    await setBundle(bundlePath)
    reloadBundle()
  }
}

/**
 * Download new bundle file
 *
 * @param bundleUrl bundle file url
 * @returns downloaded file path
 */
const downloadBundle = async (
  bundleUrl: string
): Promise<string | undefined> => {
  // only production mode suppurted
  if (!checkIsProductionMode()) return undefined

  try {
    //  Download new bundle file,
    const filePath = await DigiCodePush.downloadBundle(bundleUrl)

    return filePath
  } catch (error) {
    console.log(error)
  }

  return undefined
}

/**
 * Set new bundle on bridge
 *
 * @param bundlePath new bundle file path
 * @returns
 */
const setBundle = async (bundlePath: string): Promise<void> => {
  // only production mode suppurted
  if (!checkIsProductionMode()) return

  try {
    // set new bundle on bridge
    await DigiCodePush.setBundle(bundlePath)
  } catch (error) {
    console.log(error)
  }

  return
}

/**
 * reload bridge and use new bundle file
 *
 * @param bundlePath new bundle file path
 * @returns
 */
const reloadBundle = async (): Promise<void> => {
  // only production mode suppurted
  if (!checkIsProductionMode()) return

  try {
    // set new bundle on bridge
    DigiCodePush.reloadBundle()
  } catch (error) {
    console.log(error)
  }
}

/**
 * get active bundle url from server
 *
 * @param url url of code push api
 * @param options include headers, et cetera
 *
 * @returns bundle url and code or undefined
 */
const getActiveBundleUrl = async (
  url: string,
  options?: Object
): Promise<{ url: string; code: string } | undefined> => {
  // only production mode suppurted
  if (!checkIsProductionMode()) return undefined

  let res

  // check url is valid
  if (url.startsWith('http')) {
    try {
      const response = await fetch(url, options)

      const json = await response.json()

      console.log(JSON.stringify(json))

      const _url = json?.data?.bundle?.bundle_url
      const code = json?.data?.bundle?.code

      if (_url) {
        res = { code, url: _url }
      }
    } catch (error) {
      console.log(error)
    }
  }

  return res
}

// export interface
export default {
  checkForUpdates,
  downloadBundle,
  getActiveBundleUrl,
  reloadBundle,
  setBundle,
}
