//
//  Message.m
//  LiveKit
//
//  Created by 文宗 on 16/9/21.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "Message.h"

@implementation Message
- (void)setDict:(NSDictionary *)dict{
    
    _dict = dict;
    
    self.icon = dict[@"icon"];
    self.time = dict[@"time"];
    self.content = dict[@"content"];
    self.type = [dict[@"type"] intValue];
}
@end
