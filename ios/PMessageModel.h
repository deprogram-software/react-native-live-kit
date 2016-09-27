//
//  PMessageModel.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PMessageModel : NSObject
//名字
@property(nonatomic,copy)NSString *name;
//状态
@property(nonatomic,copy)NSString *state;
//消息
@property(nonatomic,copy)NSString *message;


@end
