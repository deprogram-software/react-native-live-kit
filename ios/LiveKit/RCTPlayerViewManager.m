//
//  RCTPlayerViewManager.m
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "RCTPlayerViewManager.h"

#import "RCTPlayerView.h"

#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "UIView+React.h"

@implementation RCTPlayerViewManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
    return [[RCTPlayerView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(uri, NSString)
RCT_EXPORT_VIEW_PROPERTY(liveInfo, NSDictionary)

@end
