//
//  RCTPlayerView.m
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "RCTPlayerView.h"

#import "UIView+React.h"
#import "PLPlayerEnv.h"
#import "PLPlayerKit.h"
#import "HappyDNS.h"

@interface RCTPlayerView () <PLPlayerDelegate>

@property (nonatomic, strong) PLPlayer  *player;

@end

@implementation RCTPlayerView

- (instancetype)init
{
    if(self = [super init]){
        NSLog(@"<<<<<< Init >>>>>>");
    }
    
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
}

- (void)setUri:(NSString *)uri
{
    if (uri != _uri) {
        _uri = uri;
        [self buildPlayer];
    }
}

- (void)buildPlayer
{
    // 初始化 PLPlayerOption 对象
    PLPlayerOption *option = [PLPlayerOption defaultOption];
    
    // 更改需要修改的 option 属性键所对应的值
    [option setOptionValue:@15 forKey:PLPlayerOptionKeyTimeoutIntervalForMediaPackets];
    [option setOptionValue:@1000 forKey:PLPlayerOptionKeyMaxL1BufferDuration];
    [option setOptionValue:@1000 forKey:PLPlayerOptionKeyMaxL2BufferDuration];
    [option setOptionValue:@(YES) forKey:PLPlayerOptionKeyVideoToolbox];
    [option setOptionValue:@(kPLLogInfo) forKey:PLPlayerOptionKeyLogLevel];
    [option setOptionValue:[QNDnsManager new] forKey:PLPlayerOptionKeyDNSManager];
    
    // 初始化 PLPlayer
    NSLog(@"URL >>>>>> %@", self.uri);
    
    self.player = [PLPlayer playerWithURL: [NSURL URLWithString:self.uri] option:option];
    
    NSLog(@"player >>>>>> %@", self.player);
    
    // 设定代理 (optional)
    self.player.delegate = self;
    
    self.player.playerView.contentMode = UIViewContentModeScaleAspectFit;
    
    //获取视频输出视图并添加为到当前 UIView 对象的 Subview
    [self addSubview:self.player.playerView];
    
    [self.player play];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.player stop];
}

#pragma PLPlayerDelegate
-(void)player:(PLPlayer *)player stoppedWithError:(NSError *)error
{
    NSLog(@"出错了。。。。。%@", [error localizedDescription]);
}

@end
