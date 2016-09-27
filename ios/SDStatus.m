
//  LiveKit
//
//  Created by 文宗 on 16/9/16.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "SDStatus.h"

@implementation SDStatus

- (instancetype)initWithDic:(NSDictionary *)dic{
    if (self = [super init]) {
        self.icon = dic[@"icon"];
        self.text = dic[@"text"];
    }
    return self;
}

+ (instancetype)statusWithDic:(NSDictionary *)dic{
    return [[self alloc] initWithDic:dic];
}
@end
