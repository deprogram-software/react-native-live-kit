//
//  sSDStatus.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface sSDStatus : NSObject

//评论内容
@property (nonatomic, copy) NSString *text;

//@property(nonatomic,copy)NSString*UserName;
//头像
@property (nonatomic, copy) NSString *icon;

- (instancetype)initWithDic:(NSDictionary *)dic;

+ (instancetype)statusWithDic:(NSDictionary *)dic;
@end
