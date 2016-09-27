//
//  LiveKit
//
//  Created by 文宗 on 16/9/16.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//
#import <Foundation/Foundation.h>

@interface SDStatus : NSObject

//评论内容
@property (nonatomic, copy) NSString *text;

//头像
@property (nonatomic, copy) NSString *icon;

- (instancetype)initWithDic:(NSDictionary *)dic;

+ (instancetype)statusWithDic:(NSDictionary *)dic;

@end
