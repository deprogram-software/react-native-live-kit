//
//  MessageModel.h
//  LiveKit
//
//  Created by 文宗 on 16/9/13.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MessageModel : NSObject
//名字
@property(nonatomic,copy)NSString *name;
//状态
@property(nonatomic,copy)NSString *state;
//消息
@property(nonatomic,copy)NSString *message;


@end
