//
//  DownloadManager.swift
//  RNDigiCodePushModule
//
//  Created by Mostafa Taghipour on 2/26/1401 AP.
//  Copyright © 1401 AP Mostafa Taghipour. All rights reserved.
//

import Foundation

typealias DownloadCallback = (_ fileUrl: URL? , _ error: Error?)->()

class DownloadManager: NSObject {
    static var shared = DownloadManager()
    
    private var urlSession: URLSession!
    var tasks: [URLSessionTask] = []
    var callBacks: [String:DownloadCallback] = [:]
    
    override private init() {
        super.init()
        
        let config = URLSessionConfiguration.background(withIdentifier: "\(Bundle.main.bundleIdentifier!).background")
        
        // Warning: Make sure that the URLSession is created only once (if an URLSession still
        // exists from a previous download, it doesn't create a new URLSession object but returns
        // the existing one with the old delegate object attached)
        urlSession = URLSession(configuration: config, delegate: self, delegateQueue: OperationQueue())
        
        updateTasks()
    }
    
    func startDownload(url: URL , destination: String , completion: @escaping DownloadCallback) {
        
        let taskDesc = "\(url.absoluteString)|\(destination)"
        
        let task = urlSession.downloadTask(with: url)
        task.resume()
        task.taskDescription = taskDesc
        tasks.append(task)
        
        callBacks[taskDesc] = completion
    }
    
    private func updateTasks() {
        urlSession.getAllTasks { tasks in
            DispatchQueue.main.async {
                self.tasks = tasks
            }
        }
    }
}

extension DownloadManager: URLSessionDelegate, URLSessionDownloadDelegate {
    func urlSession(_: URLSession, downloadTask: URLSessionDownloadTask, didWriteData _: Int64, totalBytesWritten _: Int64, totalBytesExpectedToWrite _: Int64) {
        //        os_log("Progress %f for %@", type: .debug, downloadTask.progress.fractionCompleted, downloadTask)
    }
    
    func urlSession(_: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        
        guard let taskDesc = downloadTask.taskDescription else {
            return
        }
        let callBack = callBacks[taskDesc]
        
        do {
            
            let destinationPath = taskDesc.components(separatedBy: "|")[1]
            
            guard  let url = downloadTask.originalRequest?.url ,   let destinationURL = URL(string: destinationPath)?.appendingPathComponent(url.lastPathComponent)    else{
                return
            }
            
            // delete original copy
            try? FileManager.default.removeItem(at: destinationURL)
            
            // move from temp to destination
            try FileManager.default.moveItem(at: location, to: destinationURL)
            
            callBack?(destinationURL,nil)
            
        } catch let error {
            
            callBack?(nil,error)
            
        }
    }
    
    func urlSession(_: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        
        guard let error = error , let taskDesc = task.taskDescription else {
            return
        }
        let callBack = callBacks[taskDesc]
        
        callBack?(nil,error)
       
    }
}
