//
//  sSDStatus.m
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "sSDStatus.h"

@implementation sSDStatus
- (instancetype)initWithDic:(NSDictionary *)dic{
    if (self = [super init]) {
        self.icon = dic[@"icon"];
        self.text = dic[@"text"];
       // self.UserName = dic[@"UserName"];
        
    }
    return self;
}

+ (instancetype)statusWithDic:(NSDictionary *)dic{
    return [[self alloc] initWithDic:dic];
}
@end
