//
//  DigiCodePush.swift
//  RNDigiCodePushModule
//
//  Created by Mostafa Taghipour on 2/26/1401 AP.
//  Copyright © 1401 AP Mostafa Taghipour. All rights reserved.
//

import Foundation


private let BUNDLE_LAST_ADDRESS_KEY = "DigiCodePush_LastAddress"
private let BUNDLE_BUILD_NUMBER_KEY = "DigiCodePush_BuildNmber"

@objc
public class DigiCodePush: NSObject {
    
    
    @objc
    /// Get last bundle file url or default bundle url
    /// - Parameter defaultUrl: default  bundle url
    /// - Returns: last file url or return defult bundle file url
    public class func bundleURL(withDefaultUrl defaultUrl : URL?) -> URL? {
        let lastBuildNumber = UserDefaults.standard.integer(forKey: BUNDLE_BUILD_NUMBER_KEY)
        
        guard let lastUrl = UserDefaults.standard.url(forKey: BUNDLE_LAST_ADDRESS_KEY),
              FileManager.default.fileExists(atPath: lastUrl.path),
              lastBuildNumber == appBuildNumber else{
                  
                  return defaultUrl
              }
        
        return lastUrl
    }
    
    internal class var documentUri : URL? {
        return try? FileManager.default.url(for: .documentDirectory,
                                               in: .userDomainMask,
                                               appropriateFor: nil,
                                               create: false)
    }
    
    
    private class var appBuildNumber : Int? {
        guard let bundleVersion = Bundle.main.infoDictionary?["CFBundleVersion"] as? String , let _buildNumber = Int(bundleVersion) else { return nil  }
        
        return _buildNumber
    }
    
    
    internal class var bundleFileName: String {
        return "\(Bundle.main.bundleIdentifier?.replacingOccurrences(of:".", with:"-") ?? "app")-bundle"
    }
    
    
    internal class func storeBundleUri(bundleUri:URL){
        guard let buildNumber = appBuildNumber else {
            return
        }
        
        UserDefaults.standard.set(bundleUri, forKey: BUNDLE_LAST_ADDRESS_KEY)
        UserDefaults.standard.set(buildNumber, forKey: BUNDLE_BUILD_NUMBER_KEY)
    }
    
    
    internal class func findFileWithExtensionInDirectory(dir: String, ext: String) -> String? {
        let fileManager = FileManager.default
        guard let enumerator: FileManager.DirectoryEnumerator = fileManager.enumerator(atPath: dir) else {
            return nil
        }
        while let element = enumerator.nextObject() as? String, element.hasSuffix(ext) {
            // do something
            return element
        }
        
        return nil
    }
    
    
    internal class func extractZipFile(atPath path:URL , toDestination destination : URL ,  completionHandler completion: @escaping (_ path:URL? , _ error:Error? ) -> Void){
        do {
            
            try FileManager.default.createDirectory(at: destination, withIntermediateDirectories: true, attributes: nil)
            
            SSZipArchive.unzipFile(atPath: path.path, toDestination: destination.path) { progress, unz_file_info, _, _ in
                
            } completionHandler: { _, done, error in
                
                if  error == nil &&  FileManager.default.fileExists(atPath: destination.path) {
                    
                    completion(destination , nil)
                    
                }
                else{
                    completion(nil , error)
                }
            }
            
            
        }
        catch let error{
            completion(nil , error)
        }
    }
}
