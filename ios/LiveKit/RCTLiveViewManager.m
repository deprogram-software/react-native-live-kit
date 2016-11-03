//
//  RCTLiveViewManager.m
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "RCTLiveViewManager.h"

#import "RCTLiveView.h"

@implementation RCTLiveViewManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
    return [[RCTLiveView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(stream, NSDictionary)

@end
