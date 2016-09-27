//
//  LiveCellFrame.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "sSDStatus.h"


@interface LiveCellFrame : NSObject
@property (nonatomic, strong) sSDStatus *status;
//头像位置
@property (nonatomic, assign, readonly) CGRect iconF;
//评论位置
@property (nonatomic, assign, readonly) CGRect textF;

//@property(nonatomic,assign,readonly) CGRect UserF;
//cell的高度
@property (nonatomic, assign, readonly) CGFloat cellHeight;


@end
