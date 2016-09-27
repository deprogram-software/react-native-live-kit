//
//  RCTLiveView.h
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "RCTView.h"
#import "PLMediaStreamingKit.h"

@interface RCTLiveView : RCTView

@property (nonatomic, strong) NSDictionary *stream;
@property (nonatomic, strong) NSDictionary *liveInfo;
@property (nonatomic, assign) BOOL stop;
@property (nonatomic, retain)  UITextField *inputMsg;
@property (nonatomic, retain)  UILabel *outputMsg;


- (void) showMessage:(NSString *) msg;  //显示消息
- (void) sendMsg;                        //发送消息
- (void) textFieldDoneEditing:(id)sender; //键盘
- (void) backgroundTouch:(id)sender;    // 触摸返回
@end
