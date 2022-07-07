//
//  RNDigiCodePushModule.swift
//  RNDigiCodePushModule
//
//  Copyright © 2022 Mostafa Taghipour. All rights reserved.
//

import Foundation


private let TAG = "RNDigiCodePush"

@objc(RNDigiCodePushModule)
class RNDigiCodePushModule: NSObject  , SSZipArchiveDelegate  {
    @objc var bridge: RCTBridge!
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    
    
    @objc
    /// Download bundle file
    /// - Parameters:
    ///   - bundleUrl: url of bundle file to download
    ///   - resolve: resolver
    ///   - reject: rejecter
    func downloadBundle(_ bundleUrl:String ,     resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock)  {
        
        
        guard let url = URL(string: bundleUrl), let documentsUri = DigiCodePush.documentUri  else{
            reject(TAG, "Missing url field", nil)
            return
        }
        
        DownloadManager.shared.startDownload(url: url , destination: documentsUri.absoluteString){(filePath , error)-> () in
            
            guard let filePath = filePath else {
                reject(TAG, "Download field", nil)
                return
            }
            
            
            let destinationUri = documentsUri.appendingPathComponent(DigiCodePush.bundleFileName)
            
            DigiCodePush.extractZipFile(atPath: filePath, toDestination: destinationUri) { path, error in
                
                
                guard let path = path, let fileName = DigiCodePush.findFileWithExtensionInDirectory(dir: path.path, ext: ".bundle") else {
                    reject(TAG, "Unzip field", nil)
                    return
                }
                
                let bundlePath = path.appendingPathComponent(fileName)
                
                resolve(bundlePath.path)
            }
            
        }
        
    }
    
    
    
    @objc
    /// Set new bundle
    /// - Parameters:
    ///   - bundlePath: path of new bundle file
    ///   - resolve: resolver
    ///   - reject: rejecter
    func setBundle(_ bundlePath:String , resolver resolve: @escaping RCTPromiseResolveBlock,
                   rejecter reject: @escaping RCTPromiseRejectBlock) {
        
        guard let bundleUri = URL(string: bundlePath), FileManager.default.fileExists(atPath: bundlePath) else {
            reject(TAG, "File not exist", nil)
            return
        }
        
        DigiCodePush.storeBundleUri(bundleUri: bundleUri)
        
        
        // This needs to be async dispatched because the bridge is not set on init
        // when the app first starts, therefore rollbacks will not take effect.
        DispatchQueue.main.async(execute: {
            // If the current bundle URL is using http(s), then assume the dev
            // is debugging and therefore, shouldn't be redirected to a local
            // file (since Chrome wouldn't support it). Otherwise, update
            // the current bundle URL to point at the latest update
            if !(self.bridge.bundleURL.scheme?.hasPrefix("http") ?? false) {
                self.bridge.setValue(bundleUri, forKey: "bundleURL")
            }
        })
        
        resolve(nil)
    }
    
    
    
    @objc
    /// Reload application
    func reloadBundle()  {
        // This needs to be async dispatched because the bridge is not set on init
        // when the app first starts, therefore rollbacks will not take effect.
        DispatchQueue.main.async(execute: {
            RCTTriggerReloadCommandListeners(TAG)
        })
    }
    
}



