//
//  RCTLiveView.m
//  LiveKit
//
//  Created by ltjin on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//
#import "RCTLiveView.h"
#import "PLMediaStreamingKit.h"
#import "Reachability.h"
#import "BotttonToolViewL.h"
#import "Masonry.h"
#import "SRWebSocket.h"

#import "Message.h"
#import "MessageFrame.h"
#import "DMessageCell.h"

#import "CollectionViewCell.h" //cell
#import "CollectionHeaderViewCell.h" //headerCell
#import "CollectionFooterViewCell.h" //footerCell

#import "PlatFormUtil.h"
#import "AFNetworking.h"
#import "WebService.h"
const char *stateNames[] = {
    "Unknow",
    "Connecting",
    "Connected",
    "Disconnecting",
    "Disconnected",
    "Error"
};

const char *networkStatus[] = {
    "Not Reachable",
    "Reachable via WiFi",
    "Reachable via CELL"
};

#define kReloadConfigurationEnable  0

// 假设在 videoFPS 低于预期 50% 的情况下就触发降低推流质量的操作，这里的 40% 是一个假定数值，你可以更改数值来尝试不同的策略
#define kMaxVideoFPSPercent 0.5

//假设当 videoFPS 在 10s 内与设定的 fps 相差都小于 5% 时，就尝试调高编码质量
#define kMinVideoFPSPercent 0.05
#define kHigherQualityTimeInterval  10

#define kBrightnessAdjustRatio  1.03
#define kSaturationAdjustRatio  1.03

@interface RCTLiveView ()
<
PLCameraStreamingSessionDelegate,
PLStreamingSendingBufferDelegate,UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout,UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate,SRWebSocketDelegate
>
{
    NSMutableArray  *_allMessagesFrame;
    NSArray *array;
}
@property (nonatomic, strong) PLCameraStreamingSession  *session;
@property (nonatomic, strong) Reachability *internetReachability;
@property (nonatomic, strong) dispatch_queue_t sessionQueue;
@property (nonatomic, strong) NSArray<PLVideoStreamingConfiguration *>   *videoStreamingConfigurations;
@property (nonatomic, strong) NSDate*keyTime;
@property (nonatomic, assign) BOOL audioEffectOn;

@property(nonatomic, weak) BotttonToolViewL*toolView;


@property (nonatomic,strong)UICollectionView *collectionView; //加载视图的主控件
@property (nonatomic,strong)UICollectionViewFlowLayout *layout;
@property (nonatomic,strong)UIView *bgView; //加载collectionView的背景View

//私信列表
@property(nonatomic,strong)NSMutableArray *messageArray;   //信息数组
@property(nonatomic,strong)UITableView *tableView;        //tableView

//Double chat
@property(nonatomic,strong)NSMutableArray *DoubleChatMessageArray;  //聊天信息数组
@property(nonatomic,strong)UITableView *DoubleChatTableView;  //DoubleChat 表格
@property(nonatomic,strong)UIButton*send;
@property(nonatomic,strong)UIView*backView;

@property(nonatomic,strong) SRWebSocket *webSocket;

@end

@implementation RCTLiveView

- (instancetype)init
{
    if(self = [super init]){
        NSLog(@"<<<<<< Init >>>>>>");
        [PLStreamingEnv initEnv];
        
        CGSize videoSize = CGSizeMake(480 , 640);
        UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
        if (orientation <= AVCaptureVideoOrientationLandscapeLeft) {
            if (orientation > AVCaptureVideoOrientationPortraitUpsideDown) {
                videoSize = CGSizeMake(640 , 480);
            }
        }
        self.videoStreamingConfigurations = @[
                                              [[PLVideoStreamingConfiguration alloc] initWithVideoSize:videoSize expectedSourceVideoFrameRate:30 videoMaxKeyframeInterval:45 averageVideoBitRate:400 * 1000 videoProfileLevel:AVVideoProfileLevelH264Baseline31],
                                              [[PLVideoStreamingConfiguration alloc] initWithVideoSize:CGSizeMake(800 , 480) expectedSourceVideoFrameRate:30 videoMaxKeyframeInterval:72 averageVideoBitRate:600 * 1000 videoProfileLevel:AVVideoProfileLevelH264Baseline31],
                                              [[PLVideoStreamingConfiguration alloc] initWithVideoSize:videoSize expectedSourceVideoFrameRate:30 videoMaxKeyframeInterval:90 averageVideoBitRate:800 * 1000 videoProfileLevel:AVVideoProfileLevelH264Baseline31],
                                              ];
        self.sessionQueue = dispatch_queue_create("pili.queue.streaming", DISPATCH_QUEUE_SERIAL);
        
        // 网络状态监控
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
        self.internetReachability = [Reachability reachabilityForInternetConnection];
        [self.internetReachability startNotifier];
        [self Reconnect];
       
    }
    
    return self;
}

//直播流信息
- (void)setStream:(NSDictionary *)stream
{
    if (stream != _stream) {
        _stream = stream;
    }
    [self _readyStreaming];
}

//结束直播
-(void)setStop:(BOOL)stop
{
    _stop = stop;
    if(_stop){
        NSLog(@"结束直播.........");
        [self stopSession];
    }
}

//直播信息：主播信息，直播id
-(void)setLiveInfo:(NSDictionary *)liveInfo
{
    _liveInfo = liveInfo;
}

- (void) _readyStreaming
{
    if(self.stream){
        NSLog(@"开始直播........");
        PLStream *stream = [PLStream streamWithJSON:self.stream];
        
        void (^permissionBlock)(void) = ^{
            dispatch_async(self.sessionQueue, ^{
                PLVideoCaptureConfiguration *videoCaptureConfiguration = [PLVideoCaptureConfiguration defaultConfiguration];
                videoCaptureConfiguration.sessionPreset = AVCaptureSessionPresetHigh;
                
                PLAudioCaptureConfiguration *audioCaptureConfiguration = [PLAudioCaptureConfiguration defaultConfiguration];
                // 视频编码配置
                PLVideoStreamingConfiguration *videoStreamingConfiguration = [self.videoStreamingConfigurations lastObject];
                // 音频编码配置
                PLAudioStreamingConfiguration *audioStreamingConfiguration = [PLAudioStreamingConfiguration defaultConfiguration];
                AVCaptureVideoOrientation orientation = (AVCaptureVideoOrientation)(([[UIDevice currentDevice] orientation] <= UIDeviceOrientationLandscapeRight && [[UIDevice currentDevice] orientation] != UIDeviceOrientationUnknown) ? [[UIDevice currentDevice] orientation]: UIDeviceOrientationPortrait);
                // 推流 session
                self.session = [[PLCameraStreamingSession alloc] initWithVideoCaptureConfiguration:videoCaptureConfiguration audioCaptureConfiguration:audioCaptureConfiguration videoStreamingConfiguration:videoStreamingConfiguration audioStreamingConfiguration:audioStreamingConfiguration stream:stream videoOrientation:orientation];
                self.session.delegate = self;
                self.session.bufferDelegate = self;
                
                
                [self.session toggleCamera];
                
                //开启美颜功能
                [self.session setBeautifyModeOn:YES];
                
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    UIView *previewView = self.session.previewView;
                    previewView.autoresizingMask = UIViewAutoresizingFlexibleHeight| UIViewAutoresizingFlexibleWidth;
                    [self addSubview:previewView];
                    
                    NSString *log = [NSString stringWithFormat:@"Zoom Range: [1..%.0f]", self.session.videoActiveFormat.videoMaxZoomFactor];
                    NSLog(@"%@", log);
                    
                    [self startSession];
//                     [self tabbar];
                   // [self Reconnect];
                    
                });
            });
        };
        
        void (^noAccessBlock)(void) = ^{
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"No Access", nil)
                                                                message:NSLocalizedString(@"!", nil)
                                                               delegate:nil
                                                      cancelButtonTitle:NSLocalizedString(@"Cancel", nil)
                                                      otherButtonTitles:nil];
            [alertView show];
        };
        
        switch ([PLCameraStreamingSession cameraAuthorizationStatus]) {
            case PLAuthorizationStatusAuthorized:
                permissionBlock();
                break;
            case PLAuthorizationStatusNotDetermined: {
                [PLCameraStreamingSession requestCameraAccessWithCompletionHandler:^(BOOL granted) {
                    granted ? permissionBlock() : noAccessBlock();
                }];
            }
                break;
            default:
                noAccessBlock();
                break;
        }
    }
}

-(void)tabbar{
    BotttonToolViewL*toolView = [[BotttonToolViewL alloc]init];
    toolView.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.7];
    [toolView setClickToolBlock:^(LiveToolType type){
        switch (type){
                //私信
         case LiveToolTypePrivateTalk:
            {
                [self ChatWithOther];
            }
            break;
            //踢人
         case LiveToolTypeTiRen:
            {
                [self addSubview:self.collectionView];
                
                [_toolView removeFromSuperview];
            }
                break;
        }
    }];
    
//    [toolView mas_makeConstraints:^(MASConstraintMaker *make) {
//                    make.left.equalTo(@0);
//                    make.right.equalTo(@0);
//                    make.bottom.equalTo(@-2);
//                    make.height.equalTo(@50);
//    }];
      _toolView = toolView;
    
    [self insertSubview:_toolView aboveSubview:self.session.stream];
    
}
/******************ChatWithOther*****************/
-(void)ChatWithOther{
    [_toolView removeFromSuperview];
    self.backgroundColor =[UIColor whiteColor];
    _allMessagesFrame = [NSMutableArray array];
    
    for (NSDictionary *dict in array) {
        
        MessageFrame *messageFrame = [[MessageFrame alloc] init];
        Message *message = [[Message alloc] init];
        message.dict = dict;
        messageFrame.message = message;
        [_allMessagesFrame addObject:messageFrame];
    }
   
    _tableView = [[UITableView alloc]initWithFrame:CGRectMake(0, [UIScreen mainScreen].bounds.size.height - 260, [UIScreen mainScreen].bounds.size.width, 300)];
    //_tableView.backgroundColor = [UIColor lightGrayColor];
    _tableView.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.5];
    _tableView.delegate = self;
    _tableView.dataSource=self;
    _tableView.separatorStyle=UITableViewCellSeparatorStyleSingleLine;
    [self addSubview:_tableView];
    
   
   /* for (int i=0; i<15; i++) {
        [self sendMsg];
    }*/
}

#pragma mark-- MessageListData
//这里接受服务器的数据   列表的实现

- (void) sendMsg{
    // 1、增加 数据源
    NSString *content =@"你好，在啊" ;//_inputMsg.text;
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    NSDate *date = [NSDate date];
    fmt.dateFormat = @"MM-dd";
    NSString *time = [fmt stringFromDate:date];
    [self addMessageWithContent:content time:time];
    // 2、刷新表格
    [self.tableView reloadData];
    // 3、滚动至当前行
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
    [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
- (void)addMessageWithContent:(NSString *)content time:(NSString *)time{
    
    MessageFrame *mf = [[MessageFrame alloc] init];
    Message *msg = [[Message alloc] init];
    msg.content = content;
    msg.time = time;
    msg.icon = @"ww.png";
    msg.type = MessageTypeOther;
    mf.message = msg;
    [_allMessagesFrame addObject:mf];
    
}

-(UICollectionView *)collectionView
{
    if (_collectionView == nil)
    {
        
        self.collectionView = [[UICollectionView alloc]initWithFrame:CGRectMake(0, [UIScreen mainScreen].bounds.size.height - 260, [UIScreen mainScreen].bounds.size.width, 300) collectionViewLayout:self.layout];
        self.collectionView.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.5];
        self.collectionView.delegate =self;
        self.collectionView.dataSource = self;
        //注册自定义cell
        [self.collectionView registerClass:[CollectionViewCell class] forCellWithReuseIdentifier:@"CollectionViewCell"];
        //注册区头视图
        [self.collectionView registerClass:[CollectionHeaderViewCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"headerCell"];
        //注册区尾视图
        [self.collectionView registerClass:[CollectionFooterViewCell class] forSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"footerCell"];
    }
    return _collectionView;
}
-(UICollectionViewFlowLayout *)layout
{
    if (_layout == nil) {
        self.layout = [[UICollectionViewFlowLayout alloc]init];
    }
    return _layout;
}
#pragma mark --tableView代理方法

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 40.0f;
    
    
}
-(UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView*headView = nil;
    headView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 40)];
    headView.backgroundColor = [UIColor grayColor];
    UIButton *button1 = [UIButton buttonWithType:UIButtonTypeCustom];
    button1.frame = CGRectMake(self.bounds.size.width/2-30, 5, 60, 30);
    [button1 setTitle:@"小周" forState:UIControlStateNormal];
    button1.titleLabel.adjustsFontSizeToFitWidth = YES;
    button1.titleLabel.font = [UIFont systemFontOfSize: 20.0];
    [headView addSubview:button1];
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.frame = CGRectMake(self.bounds.size.width-35, 5, 30, 30);
    [button setImage:[UIImage imageNamed:@"backButton"] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
    [headView addSubview:button];
    return headView;
}
#pragma mark- 消失对话界面方法实现
-(void)buttonAction:(UIButton*)btn{
    
    for(UIView *mylabelview in [self subviews])
    {
        
       if ([mylabelview isKindOfClass:[_inputMsg class]]) {
            [mylabelview removeFromSuperview];
        }
       if ([mylabelview isKindOfClass:[_tableView class]]) {
            [mylabelview removeFromSuperview];
        }
        if ([mylabelview isKindOfClass:[_send class]]) {
            [mylabelview removeFromSuperview];
        }
    }
    [self tabbar];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([tableView isEqual:_tableView]) {
    static NSString *CellIdentifier = @"Cell";
    DMessageCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil) {
        cell = [[DMessageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    cell.messageFrame = _allMessagesFrame[indexPath.row];
    
    return cell;
    }else{
        
        static NSString *CellIdentifier = @"Cell";
        DMessageCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
        
        if (cell == nil) {
            cell = [[DMessageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        }
        cell.messageFrame = _allMessagesFrame[indexPath.row];
        
        return cell;
    }
}
-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([tableView isEqual:_tableView]) {
    
       return [_allMessagesFrame[indexPath.row] cellHeight];
    }else{
        
        return [_allMessagesFrame[indexPath.row] cellHeight];
    }
}
//返回组中的个数
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _allMessagesFrame.count;
}
// 返回组数
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self endEditing:YES];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSLog(@"点击了：%ld", indexPath.row);
    NSLog(@"你点击了什么什么");
    //跳转到双人聊天界面
    [self DoubleChat];
    
}
//双人聊天界面
-(void)DoubleChat{
    
    //移除聊天列表
    [_tableView removeFromSuperview];

    self.backgroundColor =[UIColor whiteColor];
    _allMessagesFrame = [NSMutableArray array];
    
    for (NSDictionary *dict in array) {
        
        MessageFrame *messageFrame = [[MessageFrame alloc] init];
        Message *message = [[Message alloc] init];
        message.dict = dict;
        messageFrame.message = message;
        [_allMessagesFrame addObject:messageFrame];
    }
    //键盘视图背景
    _backView =[[UIView alloc]initWithFrame:CGRectMake(0, self.frame.size.height-50, self.frame.size.width, 50)];
    _backView.backgroundColor =[UIColor colorWithRed:0.777 green:0.777 blue:0.777 alpha:1.0];
    
    //键盘更改通知 --- 键盘即将要更改时的通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillChange:) name:UIKeyboardWillChangeFrameNotification object:nil];
    
    _DoubleChatTableView =[[UITableView alloc]initWithFrame:CGRectMake(0, [UIScreen mainScreen].bounds.size.height - 260, [UIScreen mainScreen].bounds.size.width, 300)];
    _DoubleChatTableView.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.5];
    _DoubleChatTableView.delegate = self;
    _DoubleChatTableView.dataSource=self;
    //_tableView.allowsSelection = NO;
    _DoubleChatTableView.separatorStyle=UITableViewCellSeparatorStyleNone;
    [self addSubview:_DoubleChatTableView];
    
    //手势是结束键盘的弹出   点击tableView消失键盘
    [_DoubleChatTableView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endEdit)]];
    [self addSubview:_backView];
    
    //   输入框     的设计
    _inputMsg =[[UITextField alloc]initWithFrame:CGRectMake(10, self.frame.size.height-40 , self.frame.size.width-100, 30)];
    _inputMsg.borderStyle =UITextBorderStyleRoundedRect;
    _inputMsg.delegate = self;
    _inputMsg.returnKeyType = UIReturnKeySend;
    _inputMsg.enablesReturnKeyAutomatically = YES;
    _inputMsg.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 8, 1)];
    _inputMsg.leftViewMode = UITextFieldViewModeAlways;
    [self addSubview:_inputMsg];
    _send =[[UIButton alloc]initWithFrame:CGRectMake(self.frame.size.width-80, self.frame.size.height-40, 70, 30)];
    [_send setTitle:@"发送" forState:UIControlStateNormal];
    
    //发送消息的事件
    [_send addTarget:self action:@selector(DoubleSendMsg) forControlEvents:UIControlEventTouchUpInside];
    _send.layer.cornerRadius = 5;
    _send.layer.masksToBounds=YES;
    _send.backgroundColor=[UIColor colorWithRed:0.0 green:0.523 blue:0.003 alpha:1.0];
    [self addSubview:_send];
}

//来自服务器的数据
- (void)oubleSendMsg{
    // 1、增加数据源
    NSString *content = @"我来自服务器";
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    NSDate *date = [NSDate date];
    fmt.dateFormat = @"MM-dd"; // @"yyyy-MM-dd HH:mm:ss"
    NSString *time = [fmt stringFromDate:date];
    [self oubleAddMessageWithContent:content time:time];
    // 2、刷新表格
    [self.DoubleChatTableView reloadData];
    // 3、滚动至当前行
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
    [self.DoubleChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
}
- (void)oubleAddMessageWithContent:(NSString *)content time:(NSString *)time{
    
    MessageFrame *mf = [[MessageFrame alloc] init];
    Message *msg = [[Message alloc] init];
    msg.content = content;
    msg.time = time;
    msg.icon = @"ww.png";
    msg.type = MessageTypeOther;
    mf.message = msg;
    [_allMessagesFrame addObject:mf];
}


#pragma mark--键盘上的发送按钮对应的事件
- (void)DoubleSendMsg{
    [self DouSendMessage];
    // 1、增加数据源
    NSString *content = _inputMsg.text;
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    NSDate *date = [NSDate date];
    fmt.dateFormat = @"MM-dd"; // @"yyyy-MM-dd HH:mm:ss"
    NSString *time = [fmt stringFromDate:date];
    [self DoubleAddMessageWithContent:content time:time];
    // 2、刷新表格
    [self.DoubleChatTableView reloadData];
    // 3、滚动至当前行
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
    [self.DoubleChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    // 4、清空文本框内容
    _inputMsg.text = nil;
}
#pragma mark 给数据源增加内容
- (void)DoubleAddMessageWithContent:(NSString *)content time:(NSString *)time{
    
    MessageFrame *mf = [[MessageFrame alloc] init];
    Message *msg = [[Message alloc] init];
    msg.content = content;
    msg.time = time;
    msg.icon = @"head";
    msg.type = MessageTypeMe;
    mf.message = msg;
    [_allMessagesFrame addObject:mf];
}
-(void)sendMessage{
    
    NSLog(@"键盘的内容：%@",self.inputMsg.text);
    self.inputMsg.text = @"";
    
}
//取消键盘的第一响应
- (void) textFieldDoneEditing:(id)sender{
    [sender resignFirstResponder];
}
- (void) backgroundTouch:(id)sender{
    [_inputMsg resignFirstResponder];
}
#pragma mark - UITextField的代理方法
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self DouSendMessage];
    [self endEditing:YES];
    // 1、增加数据源
    NSString *content = _inputMsg.text;
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    NSDate *date = [NSDate date];
    fmt.dateFormat = @"MM-dd"; // @"yyyy-MM-dd HH:mm:ss"
    NSString *time = [fmt stringFromDate:date];
    [self DoubleAddMessageWithContent:content time:time];
    // 2、刷新表格
    [self.DoubleChatTableView reloadData];
    // 3、滚动至当前行
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
    [self.DoubleChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    // 4、清空文本框内容
    _inputMsg.text = nil;
    
    return YES;
}

//键盘编辑结束
- (void)endEdit
{
    [self endEditing:YES];
}

///**
// *  键盘发生改变执行
// */
- (void)keyboardWillChange:(NSNotification *)note
{
    NSLog(@"%@", note.userInfo);
    NSDictionary *userInfo = note.userInfo;
    CGFloat duration = [userInfo[@"UIKeyboardAnimationDurationUserInfoKey"] doubleValue];
    
    CGRect keyFrame = [userInfo[@"UIKeyboardFrameEndUserInfoKey"] CGRectValue];
    
    CGFloat moveY = keyFrame.origin.y - self.frame.size.height;
    [UIView animateWithDuration:duration animations:^{
        [UIView animateWithDuration:duration animations:^{
            _inputMsg.transform = CGAffineTransformMakeTranslation(0, moveY);
            _send.transform = CGAffineTransformMakeTranslation(0, moveY);
            _backView.transform = CGAffineTransformMakeTranslation(0, moveY);
            _DoubleChatTableView.transform = CGAffineTransformMakeTranslation(0, moveY-90);
            
        }];
    }];
}
#pragma mark --collectionView代理方法
//返回区的个数
-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}
//返回每个分区cell的个数
-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return 10;
}
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{

    CollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CollectionViewCell" forIndexPath:indexPath];
//#warming需要踢人的头像都放在这里的一个数组里；
    
    return cell;
}
- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    
    // 如果当前想要的是头部视图
    if (kind == UICollectionElementKindSectionHeader) {
        CollectionHeaderViewCell *headerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"headerCell" forIndexPath:indexPath];
        headerView.backgroundColor = [UIColor lightGrayColor];
        
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.frame = CGRectMake(headerView.frame.size.width-40,headerView.center.y-15 ,30,30);
        [button setImage:[UIImage imageNamed:@"backButton"] forState:UIControlStateNormal];
        [button addTarget:self action:@selector(buttonClick) forControlEvents:UIControlEventTouchDown];
        [headerView addSubview:button];
        return headerView;
    } else { // 返回每一组的尾部视图
        CollectionFooterViewCell *footerView =  [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"footerCell" forIndexPath:indexPath];
            return footerView;
    }
}
-(void)buttonClick
{
    NSLog(@"点击了退出按钮");
   // [ self  presentModalViewController : nextWebView   animated : YES ];
    [self.collectionView removeFromSuperview];
    [self tabbar];
}
-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    //获取cell
    CollectionViewCell *cell = (CollectionViewCell *)[self.collectionView cellForItemAtIndexPath:indexPath];
    cell.button.selected = !cell.button.selected;
    NSLog(@"点击了collectionItem");
}
#pragma mark -FlowLayoutDlegate
-(CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(([UIScreen mainScreen].bounds.size.width-30)/5,([UIScreen mainScreen].bounds.size.width-30)/5 );
}
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(5, 5, 5, 5);
}
//最小行间距
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return 5;
}
//最小列间距
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section
{
    return 0;
}
//返回区头高度
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section
{
    return CGSizeMake(0, 50);
}
//返回区尾高度
-(CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout referenceSizeForFooterInSection:(NSInteger)section
{
    return CGSizeMake(0, 50);
}


//私聊 消息向服务器发送消息
-(void)DouSendMessage{
    NSURL*url = [NSURL URLWithString:@"http://139.129.233.39:9001/appUser/sendPrviteMail"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //网络 请求
    NSDictionary *param = @{
                            @"conttext":self.inputMsg.text,
                            @"liveid":@"14746202030320",
                            @"receiveid" : @"11",
                            @"senderid" : @"27",
                            };
    NSArray*keyArray = [param allKeys];
    NSArray*sortArray = [keyArray sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        return [obj1 compare:obj2 options:NSNumericSearch];
    }];
    NSMutableArray*valueArray = [NSMutableArray array];
    for (NSString*sortString in sortArray) {
        [valueArray addObject:[param objectForKey:sortString]];
    }
    NSMutableArray*signArray = [NSMutableArray array];
    NSString*sign = [[NSString alloc]init];
    for (int i=0; i<sortArray.count; i++) {
        NSString*keyValueStr = [NSString stringWithFormat:@"%@=%@",sortArray[i],valueArray[i]];
        [signArray addObject:keyValueStr];
        sign = [signArray componentsJoinedByString:@"&"];
    }
    NSDictionary * paradic = [PlatFormUtil generalConfigParameters:param byAppend:sign];
    request.HTTPBody = [NSJSONSerialization dataWithJSONObject:paradic options:NSJSONWritingPrettyPrinted error:nil];
    [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse * _Nullable response, NSData * _Nullable data, NSError * _Nullable connectionError) {
        
        //NSLog(@"发送消息的响应：%@",response);
        if (!connectionError) {
            NSLog(@"网络消息数据发送成功");
        }else{
            NSLog(@"数据发送错误是：%@",connectionError);
        }
    }];
}


- (void)dealloc {
    [self deallocSession];
}

- (void)deallocSession{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    dispatch_sync(self.sessionQueue, ^{
        [self.session destroy];
    });
    self.session = nil;
    self.sessionQueue = nil;
    
    self.webSocket.delegate = nil;
    [self.webSocket close];
    self.webSocket = nil;
    NSLog(@"连接关闭");
}

#pragma mark--  about webSocket

- (void)Reconnect{
    NSLog(@"初始化");
    self.webSocket.delegate = nil;
    [self.webSocket close];
    self.webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"ws://139.129.233.39:8110/WebSocket/message?user=27&liveid=14746202030320&username=C4E3BAC3"]]];//C4E3BAC3
    self.webSocket.delegate = self;
    [self.webSocket open];
}

#pragma mark - SRWebSocketDelegate
- (void)webSocketDidOpen:(SRWebSocket *)webSocket{
    
    NSLog(@"连接");
}
//连接失败
- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error{
    NSLog(@":( Websocket 连接失败的原因是： %@", error);
  
    self.webSocket = nil;
}
/*
 {
 //webSocket 私聊返回的数据格式
 "type": "private_mail",
 "user": "11",
 "liveid": "14746117583504",
 "receiveid": "27",
 "conttext": "几斤几两了",
 "username": "我是主播",
 "photo": "http://ocih2wfo8.bkt.clouddn.com/1472295468726"
 
 /*
 {
 "type":"gift_send",
 "user":"27",
 "quantity":"1",
 "giftname":"测试",
 "liveid":"14746202030320",
 "giftid":"1",
 "username":"七天之约",
 "photo":"http://ocih2wfo8.bkt.clouddn.com/1472295468726"
 }
 
 }
 */ //接收到新消息的处理
- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message{
    NSString *content1 = nil;
    NSString *msgString = message;
    NSData *data = [msgString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *toUse = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:nil];
    if (toUse == nil) {return;}
    NSString*str = [toUse objectForKey:@"type"];
    if([@"private_mail" isEqualToString:str]){
             Message*message = [[Message alloc]init];
             message.content =[toUse objectForKey:@"conttext"];
             NSString *content = [toUse objectForKey:@"conttext"];//_inputMsg.text;
             NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
             NSDate *date = [NSDate date];
             fmt.dateFormat = @"MM-dd";
             NSString *time = [fmt stringFromDate:date];
             MessageFrame *mf = [[MessageFrame alloc] init];
             Message *msg = [[Message alloc] init];
             msg.content = content;
             msg.time = time;
              msg.icon = @"iconww";
             msg.type = MessageTypeOther;
             mf.message = msg;
             [_allMessagesFrame addObject:mf];
             [_DoubleChatTableView reloadData];
             NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
             [_DoubleChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
    if ([@"gift_send" isEqualToString:str]) {
        NSString *content = [toUse objectForKey:@"giftname"];
        NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
        NSDate *date = [NSDate date];
        fmt.dateFormat = @"MM-dd";
        NSString *time = [fmt stringFromDate:date];
        //[self addMessageWithContent:content time:time];
        MessageFrame *mf = [[MessageFrame alloc] init];
        Message *msg = [[Message alloc] init];
        msg.content = content;
        msg.time = time;
        msg.icon = @"ww.png";
        msg.type = MessageTypeOther;
        mf.message = msg;
        [_allMessagesFrame addObject:mf];
        // 2、刷新表格
        [self.tableView reloadData];
        // 3、滚动至当前行
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
        [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }else if ([@"private_mail" isEqualToString:str]){
        NSString *content = [toUse objectForKey:@"conttext"];
        NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
        NSDate *date = [NSDate date];
        fmt.dateFormat = @"MM-dd";
        NSString *time = [fmt stringFromDate:date];
        MessageFrame *mf = [[MessageFrame alloc] init];
        Message *msg = [[Message alloc] init];
        msg.content = content;
        msg.time = time;
        msg.icon = @"ww.png";
        msg.type = MessageTypeOther;
        mf.message = msg;
        [_allMessagesFrame addObject:mf];
        // 2、刷新表格
        [self.tableView reloadData];
        // 3、滚动至当前行
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
        [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        
    }
}

//连接关闭
- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean{
    NSLog(@"连接关闭:%@",reason);
    self.webSocket = nil;
}

- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)pongPayload{
    
    NSString *reply = [[NSString alloc] initWithData:pongPayload encoding:NSUTF8StringEncoding];
    NSLog(@"接受的数据：%@",reply);
}

#pragma mark - Notification Handler

- (void)reachabilityChanged:(NSNotification *)notif{
    Reachability *curReach = [notif object];
    NSParameterAssert([curReach isKindOfClass:[Reachability class]]);
    NetworkStatus status = [curReach currentReachabilityStatus];
    
    if (NotReachable == status) {
        // 对断网情况做处理
        [self stopSession];
    }
    
    NSString *log = [NSString stringWithFormat:@"Networkt Status: %s", networkStatus[status]];
   // NSLog(@"%@", log);
}

#pragma mark - <PLStreamingSendingBufferDelegate>

- (void)streamingSessionSendingBufferDidFull:(id)session {
    NSString *log = @"Buffer is full";
    //NSLog(@"%@", log);
}

- (void)streamingSession:(id)session sendingBufferDidDropItems:(NSArray *)items {
    NSString *log = @"Frame dropped";
  //  NSLog(@"%@", log);
}

#pragma mark - <PLCameraStreamingSessionDelegate>

- (void)cameraStreamingSession:(PLCameraStreamingSession *)session streamStateDidChange:(PLStreamState)state {
    NSString *log = [NSString stringWithFormat:@"Stream State: %s", stateNames[state]];
   // NSLog(@"%@", log);
}

- (void)cameraStreamingSession:(PLCameraStreamingSession *)session didDisconnectWithError:(NSError *)error {
    NSString *log = [NSString stringWithFormat:@"Stream State: Error. %@", error];
   // NSLog(@"%@", log);
    [self startSession];
}

- (CVPixelBufferRef)cameraStreamingSession:(PLCameraStreamingSession *)session cameraSourceDidGetPixelBuffer:(CVPixelBufferRef)pixelBuffer {
    return pixelBuffer;
}

- (void)cameraStreamingSession:(PLCameraStreamingSession *)session streamStatusDidUpdate:(PLStreamStatus *)status {
    NSString *log = [NSString stringWithFormat:@"%@", status];
   // NSLog(@"%@", log);
#if kReloadConfigurationEnable
    NSDate *now = [NSDate date];
    if (!self.keyTime) {
        self.keyTime = now;
    }
    
    double expectedVideoFPS = (double)self.session.videoConfiguration.videoFrameRate;
    double realtimeVideoFPS = status.videoFPS;
    if (realtimeVideoFPS < expectedVideoFPS * (1 - kMaxVideoFPSPercent)) {
        // 当得到的 status 中 video fps 比设定的 fps 的 50% 还小时，触发降低推流质量的操作
        self.keyTime = now;
        
        [self lowerQuality];
    } else if (realtimeVideoFPS >= expectedVideoFPS * (1 - kMinVideoFPSPercent)) {
        if (-[self.keyTime timeIntervalSinceNow] > kHigherQualityTimeInterval) {
            self.keyTime = now;
            
            [self higherQuality];
        }
    }
#endif  // #if kReloadConfigurationEnable
}

#pragma mark -

- (void)higherQuality {
    NSUInteger idx = [self.videoStreamingConfigurations indexOfObject:self.session.videoStreamingConfiguration];
    NSAssert(idx != NSNotFound, @"Oops");
    
    if (idx >= self.videoStreamingConfigurations.count - 1) {
        return;
    }
    PLVideoStreamingConfiguration *newStreamingConfiguration = self.videoStreamingConfigurations[idx + 1];
    [self.session reloadVideoStreamingConfiguration:newStreamingConfiguration videoCaptureConfiguration:nil];
}

- (void)lowerQuality {
    NSUInteger idx = [self.videoStreamingConfigurations indexOfObject:self.session.videoStreamingConfiguration];
    NSAssert(idx != NSNotFound, @"Oops");
    
    if (0 == idx) {
        return;
    }
    PLVideoStreamingConfiguration *newStreamingConfiguration = self.videoStreamingConfigurations[idx - 1];
    [self.session reloadVideoStreamingConfiguration:newStreamingConfiguration videoCaptureConfiguration:nil];
}

#pragma mark - Operation

- (void)stopSession {
    dispatch_async(self.sessionQueue, ^{
        self.keyTime = nil;
        [self.session stop];
    });
}

- (void)startSession {
    
    self.keyTime = nil;
    dispatch_async(self.sessionQueue, ^{
        dispatch_async(self.sessionQueue, ^{
            [self.session startWithCompleted:^(BOOL success) {
                NSLog(@"直播成功开始.........");
            }];
        });
    });
}

@end
