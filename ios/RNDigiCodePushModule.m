//
//  RNDigiCodePushModule.m
//  RNDigiCodePushModule
//
//  Copyright © 2022 Mostafa Taghipour. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNDigiCodePushModule, NSObject)

RCT_EXTERN_METHOD(
                  downloadBundle: (NSString *)bundleUrl
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
  )


RCT_EXTERN_METHOD(
                  setBundle: (NSString *)bundlePath
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
  )


RCT_EXTERN_METHOD(reloadBundle)


@end
