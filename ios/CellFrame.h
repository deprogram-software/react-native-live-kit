//
//  CellFrame.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "SDStatus.h"
@interface CellFrame : NSObject
@property (nonatomic, strong) SDStatus *status;
//头像位置
@property (nonatomic, assign, readonly) CGRect iconF;
//评论位置
@property (nonatomic, assign, readonly) CGRect textF;


//cell的高度
@property (nonatomic, assign, readonly) CGFloat cellHeight;

@end
