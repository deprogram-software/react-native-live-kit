//
//  BotttonToolView.h
//  PLPlayerKit
//
//  Created by 文宗 on 16/8/31.
//  Copyright © 2016年 0dayZh. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef NS_ENUM(NSUInteger, LiveToolType) {
    LiveToolTypePublicTalk,
    LiveToolTypeGift,
    LiveToolTypePrivateTalk,
};
@interface BotttonToolView : UIView
@property(nonatomic, copy)void (^clickToolBlock)(LiveToolType type);

@end
