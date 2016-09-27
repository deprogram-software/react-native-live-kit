//
//  RCTPlayerView.h
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "RCTView.h"

@interface RCTPlayerView : RCTView

@property (nonatomic, copy) NSString *uri;
@property (nonatomic, strong) NSDictionary *liveInfo;
@property (nonatomic, retain)  UITextField *inputMsg;
@property (nonatomic, retain)  UILabel *outputMsg;


//- (int) connectServer: (NSString *) hostIP port:(int) hostPort;

//- (void) reConnect;

- (void) showMessage:(NSString *) msg;  //显示消息
- (void) sendMsg;                        //发送消息
- (void) textFieldDoneEditing:(id)sender; //键盘
- (void) backgroundTouch:(id)sender;    // 触摸返回
@end
