
//  LiveKit.m
//  LiveKit
//  Created by ltjin on 16/8/31.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.

#import "RCTPlayerView.h"
#import "UIView+React.h"
#import "PLPlayerEnv.h"
#import "PLPlayerKit.h"
#import "HappyDNS.h"
#import "BotttonToolView.h"
#import "Masonry.h"
#import "GiftModel.h"
#import "PlatFormUtil.h"
#import "WebService.h"
#import "AFNetworking.h"
#import "UIImageView+WebCache.h"
#import "SRWebSocket.h"
#import "MessageModel.h"
#import "MessageCell.h"
#import "CellFrame.h"
#import "MessageTableViewCell.h"
#define ScreenW [UIScreen mainScreen].bounds.size.width
#define ScreenH [UIScreen mainScreen].bounds.size.height

bool _isSelected = NO;
@interface RCTPlayerView () <PLPlayerDelegate,UITextFieldDelegate,SRWebSocketDelegate,UITableViewDelegate,UITableViewDataSource>
{
    UITableView *tableView;
    UITableView *tableView1;
}
@property (nonatomic, strong) PLPlayer  *player;
@property(nonatomic, weak) BotttonToolView *toolView;
@property(nonatomic,copy) UIButton*button;
@property(nonatomic,strong)UIView*view1;
@property(nonatomic,strong)UIView*view2;
@property(nonatomic,strong)UIView*view3;
@property(nonatomic,copy) NSMutableArray*GiftArray;
@property(nonatomic,strong)UIImage*image;
@property(nonatomic,strong) UITextField *commentTextField;
@property(nonatomic,strong) UIView*bottomView;
@property (nonatomic,strong)UIButton*commentButton;
@property (nonatomic,strong) NSMutableArray *dataArr;
@property(nonatomic,strong) SRWebSocket*webSocket;
@property(nonatomic,strong)UIButton*send;
@property(nonatomic,strong)UIView*backView;

@property(nonatomic,strong)NSMutableArray *messageArray;   //信息数组
@property(nonatomic,strong)UITableView *tableView;        //tableView
@property(nonatomic,strong)UIView*View12;
@property(nonatomic,strong)MessageModel *messageModel;    //信息模型
@property (nonatomic,strong)NSMutableArray *dataArrayy;//弹幕的数组信息
@property (nonatomic,assign)BOOL isHeistory; //判断当前cell是不是查看历史聊天cell
@end
@implementation RCTPlayerView
- (BOOL)prefersStatusBarHidden{
    return YES;
}
- (instancetype)init
{
    if(self = [super init]){
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

-(void)setLiveInfo:(NSDictionary *)liveInfo
{
    _liveInfo = liveInfo;
}
- (void)buildPlayer
{
    // 初始化 PLPlayerOption    对象
    PLPlayerOption *option = [PLPlayerOption defaultOption];
    
    // 更改需要修改的 option   属性键所对应的值
    [option setOptionValue:@15 forKey:PLPlayerOptionKeyTimeoutIntervalForMediaPackets];
    [option setOptionValue:@1000 forKey:PLPlayerOptionKeyMaxL1BufferDuration];
    [option setOptionValue:@1000 forKey:PLPlayerOptionKeyMaxL2BufferDuration];
    [option setOptionValue:@(YES) forKey:PLPlayerOptionKeyVideoToolbox];
    [option setOptionValue:@(kPLLogInfo) forKey:PLPlayerOptionKeyLogLevel];
    [option setOptionValue:[QNDnsManager new] forKey:PLPlayerOptionKeyDNSManager];
    self.player = [PLPlayer playerWithURL: [NSURL URLWithString:self.uri] option:option];
    self.player.delegate = self;
    self.player.playerView.contentMode = UIViewContentModeScaleAspectFit;
    //获取视频输出视图并添加为到当前 UIView 对象的 Subview
    [self addSubview:self.player.playerView];
    [self.player play];
    [self tabbar];
    [self array];
    [self danmu];
    [self Reconnert];
}
/***************弹幕相关*******************************************************/

-(void)danmu{
    tableView1 =[[UITableView alloc]initWithFrame:CGRectMake(5, [UIScreen mainScreen].bounds.size.height/2+40, [UIScreen mainScreen].bounds.size.width/2, [UIScreen mainScreen].bounds.size.height/4) style:UITableViewStylePlain];
    tableView1.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.1];
    tableView1.delegate  = self;
    tableView1.dataSource =self;
    tableView1.separatorStyle = NO;
    tableView1.showsVerticalScrollIndicator = NO;
    [self addSubview:tableView1];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(updataTableView:) name:@"message" object:nil];
    
    //创建定时器，模拟收到消息
    // NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:2 target:self selector:@selector(runLoop) userInfo:nil repeats:YES];
    // [[NSRunLoop mainRunLoop]addTimer:timer forMode:NSRunLoopCommonModes];
    
    _dataArrayy = [NSMutableArray array];
    NSMutableArray *array  = [NSMutableArray arrayWithArray:@[@{@"text":@"欢迎来到直播间",@"icon":@"head"}]];
    for (NSDictionary *dic in array) {
        //相当于模型数组转换
        SDStatus *SDstatus = [SDStatus statusWithDic:dic];
        CellFrame *statusFrame = [[CellFrame alloc] init];
        //计算宽高
        statusFrame.status = SDstatus;
        [_dataArrayy addObject:statusFrame];
    }
    
}
-(void)runLoop
{
    //这个是服务器推过来的消息
    //接受到消息后发送通知，让tableView改变
    //dic 相当于接受过来的消息
    NSDictionary *dic = @{@"text":@"周三房价。。",@"icon":@"head"};
    //相当于模型数组转换
    SDStatus *SDstatus = [SDStatus statusWithDic:dic];
    CellFrame *statusFrame = [[CellFrame alloc] init];
    //计算宽高
    statusFrame.status = SDstatus;
    [_dataArrayy addObject:statusFrame];
    [tableView1 reloadData];
    [[NSNotificationCenter defaultCenter]postNotificationName:@"message" object:nil userInfo:dic];
}
-(void)updataTableView:(id)sender
{
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_dataArrayy.count-1 inSection:0];
    if (!self.isHeistory)
    {
        [tableView1 scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}
/**************************************************************************
 
 WebSocket相关
 
 
 ***************************************************************************/
#pragma mark -- WebSocket 的初始化

-(void)Reconnert{
    self.webSocket.delegate = nil;
    [self.webSocket close];
    //网络 请求
    NSDictionary *param = @{
                            @"user":@"121",
                            @"liveid":@"11",
                            @"username" : @"11",
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
    NSDictionary*paradic = [PlatFormUtil WebSocketGeneralConfigParameters:param byAppend:sign];
    NSString*finaStrin = [PlatFormUtil parseDictionaryToFormattedString:paradic];
    NSLog(@"%@",finaStrin);
    self.webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"ws://192.168.1.3:8110/WebSocket/message?%@",finaStrin]]]];
#warning mark-- 上面需要填写用户信息
    self.webSocket.delegate = self;
    [self.webSocket open];
}
-(void)tabbar{
    
    BotttonToolView*toolView = [[BotttonToolView alloc]init];
    toolView.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.7];
    [toolView setClickToolBlock:^(LiveToolType type) {
        switch (type) {
                // 私信 （ 其实 就是 两个人 聊天）
            case LiveToolTypePrivateTalk:
            {
                
                if(1){
                    for(UIView *mylabelview in [self subviews])
                    {
                        if ([mylabelview isKindOfClass:[_toolView class]]) {
                            [mylabelview removeFromSuperview];
                        }
                    }
                    _messageArray =[NSMutableArray array];
                    //键盘视图
                    _backView =[[UIView alloc]initWithFrame:CGRectMake(0, self.frame.size.height-50, self.frame.size.width, 50)];
                    _backView.backgroundColor =[UIColor colorWithRed:0.777 green:0.777 blue:0.777 alpha:1.0];
                    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillChange:) name:UIKeyboardWillChangeFrameNotification object:nil];
                    _tableView =[[UITableView alloc]initWithFrame:CGRectMake(0, self.frame.size.height-200, self.frame.size.width, self.frame.size.height-150)];
                    _tableView.delegate = self;
                    _tableView.dataSource=self;
                    _tableView.allowsSelection = NO;
                    _tableView.separatorStyle=UITableViewCellSeparatorStyleNone;
                    [self addSubview:_tableView];
                    [_tableView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endEdit)]];
                    // [self addSubview:_backView];
                    
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
                    [_send addTarget:self action:@selector(sendMsg) forControlEvents:UIControlEventTouchUpInside];
                    _send.layer.cornerRadius = 5;
                    _send.layer.masksToBounds=YES;
                    _send.backgroundColor=[UIColor colorWithRed:0.0 green:0.523 blue:0.003 alpha:1.0];
                    [self addSubview:_send];
                    //[self Reconnert];//让 WebSocket 服务器连接
                }
            }
                break;
                
#warning 私信待完善 。 。 。
                
                //  聊天  其实就是直接发布评论（弹幕的实现））
            case LiveToolTypePublicTalk:
                
            {
                [self danmu];
                for(UIView *mylabelview in [self subviews])
                {
                    if ([mylabelview isKindOfClass:[_toolView class]]) {
                        [mylabelview removeFromSuperview];
                    }
                }
                [self setupButtomView];
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:@"UIKeyboardWillShowNotification" object:nil];
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHidden:) name:@"UIKeyboardWillHideNotification" object:nil];
            }
                break;
                //礼物的界面的实现
            case LiveToolTypeGift:
            {
                
                //移除 ButtonTool
                for(UIView *mylabelview in [self subviews])
                {
                    if ([mylabelview isKindOfClass:[_toolView class]]) {
                        [mylabelview removeFromSuperview];
                    }
                }
                for(UIView *mylabelview in [self subviews])
                {
                    if ([mylabelview isKindOfClass:[tableView1 class]]) {
                        [mylabelview removeFromSuperview];
                    }
                }
                
                
                _view1 = [[UIView alloc]initWithFrame:CGRectMake(0, ScreenH-240, ScreenW,220)];
                _view1.backgroundColor = [UIColor colorWithWhite:0.f alpha:0.5];
                for (NSInteger index = 0; index<_GiftArray.count; index++) {
                    GiftModel*model = _GiftArray[index];
                    _button = [UIButton buttonWithType:UIButtonTypeCustom];
                    NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:model.photoname]];
                    UIImage*image = [UIImage imageWithData:data];
                    [_button setImage:image forState:UIControlStateNormal];
                    CGFloat viewW = ScreenW/5-2;
                    CGFloat viewH = viewW;
                    _button.tag = index;
                    int cols = 5;
                    CGFloat colMargin = 1;
                    CGFloat rowMargin = 1;
                    NSUInteger i = index;
                    NSUInteger col = i % cols;
                    CGFloat viewX = col * (viewW + colMargin);
                    NSInteger row = index / cols;
                    CGFloat viewY = row * (viewH + rowMargin);
                    _button.frame = CGRectMake(viewX+2, viewY+2, viewW, viewH);
                    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handletapPressGesture:)];
                    [_view1 addGestureRecognizer:tapGesture];
#pragma mark--  礼物名字
                    UILabel*label = [[UILabel alloc]initWithFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y+viewH-24, viewW, 10)];
                    label.text = model.name1;
                    label.font = [UIFont systemFontOfSize:10];
                    label.textColor = [UIColor whiteColor];
                    label.textAlignment = UITextAlignmentCenter;
                    [_button addSubview:label];
#pragma mark--  礼物价格
                    UILabel*labelPrice = [[UILabel alloc]initWithFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y+viewH-10, viewW, 10)];
                    labelPrice.text = [NSString stringWithFormat:@"￥%@",model.price];
                    labelPrice.font = [UIFont systemFontOfSize:10];
                    labelPrice.textColor = [UIColor whiteColor];
                    labelPrice.textAlignment = UITextAlignmentCenter;
                    [_button addSubview:labelPrice];
                    [_view1 addSubview:_button];
                }
                [self addSubview:_view1];
                _view2 = [[UIView alloc]initWithFrame:CGRectMake(0, ScreenH-60, ScreenW,60)];
                _view2.backgroundColor = [UIColor lightGrayColor];
                UIButton*button = [UIButton buttonWithType:UIButtonTypeCustom];
                button.backgroundColor = [UIColor blueColor];
                button.frame = CGRectMake(self.bounds.size.width-90, 10, 80, 40);
                [button setTitle:@"赠送" forState:UIControlStateNormal];
                [button addTarget:self action:@selector(buttonA:) forControlEvents:UIControlEventTouchUpInside];
                [_view2 resignFirstResponder];
                [_view2 addSubview:button];
                [self addSubview:_view2];
                UILabel*label = [[UILabel alloc]initWithFrame:CGRectMake(self.bounds.origin.x+10, 10, 120, 40)];
                label.text =@"余额：88趣币";
                [_view2 addSubview:label];
                [self addSubview:_view2];
            }
                break;
            default:
                break;
        }
    }];
    [self insertSubview:toolView aboveSubview:self.player.playerView];
    [toolView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(@0);
        make.bottom.equalTo(@-2);
        make.height.equalTo(@45);
    }];
    
    _toolView = toolView;
}

- (void) textFieldDoneEditing:(id)sender{
    [sender resignFirstResponder];
}

- (void) backgroundTouch:(id)sender{
    [_inputMsg resignFirstResponder];
}

- (void)keyboardWillChange:(NSNotification *)note
{
    NSLog(@"%@", note.userInfo);
    NSDictionary *userInfo = note.userInfo;
    CGFloat duration = [userInfo[@"UIKeyboardAnimationDurationUserInfoKey"] doubleValue];
    
    CGRect keyFrame = [userInfo[@"UIKeyboardFrameEndUserInfoKey"] CGRectValue];
    CGFloat moveY = keyFrame.origin.y - self.frame.size.height;
    
    [UIView animateWithDuration:duration animations:^{
        _tableView.transform = CGAffineTransformMakeTranslation(0, moveY-50);
        _inputMsg.transform = CGAffineTransformMakeTranslation(0, moveY);
        _send.transform = CGAffineTransformMakeTranslation(0, moveY);
        _backView.transform = CGAffineTransformMakeTranslation(0, moveY);
    }];
}
- (void)endEdit
{
    [self endEditing:YES];
}

#pragma mark - UITextField的代理方法
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if ([textField isEqual:_inputMsg]) {
        [self endEditing:YES];
        //5.自动滚到最后一行
        NSIndexPath *lastPath = [NSIndexPath indexPathForRow:_messageArray.count - 1 inSection:0];
        
        [self sendMessage];
        if (self.messageArray.count != 0) {
            [_tableView scrollToRowAtIndexPath:lastPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        }
        MessageModel *mModel =[[MessageModel alloc]init];
        mModel.name = @"123456";
        mModel.message = self.inputMsg.text;
        mModel.state = @"0";
        [_messageArray addObject:mModel];
        [self.tableView reloadData];
        textField.text = @"";
        
        return YES;
    }else{
        //聊天键盘消失 显示键盘输入框
        [self endEditing:YES];
    }
    return nil;
}

- (void) sendMsg{
    [self sendMessage];  //向服务器发送消息
    MessageModel *mModel =[[MessageModel alloc]init];
    mModel.name = @"123456";
    mModel.message = self.inputMsg.text;
    mModel.state = @"0";
    [_messageArray addObject:mModel];
    [self.tableView reloadData];
    self.inputMsg.text = @"";
    
}


//礼物发送按钮的点击事件
-(void)buttonA:(UIButton*)btn{
    
    [self senderGift];
}
//********************* 评论 键盘 的 相关 事件 *********************************//
//键盘的布局
- (void)setupButtomView{
    
    self.bottomView = [[UIView alloc] initWithFrame:CGRectMake(0, self.frame.size.height - 65, CGRectGetWidth(self.frame), 65)];
    self.bottomView.backgroundColor = [UIColor grayColor];
    [self addSubview:self.bottomView];
    //添加textfield
    self.commentTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 10, self.frame.size.width - 100, 45)];
    self.commentTextField.backgroundColor = [UIColor whiteColor];
    self.commentTextField.layer.cornerRadius = 10;
    [self.bottomView addSubview:self.commentTextField];
    //textField遵循协议
    self.commentTextField.delegate = self;
    //添加button
    self.commentButton = [UIButton buttonWithType:UIButtonTypeSystem];
    self.commentButton.frame = CGRectMake(self.frame.size.width - 80, 10, 70, 45);
    self.commentButton.backgroundColor = [UIColor whiteColor];
    self.commentButton.layer.cornerRadius = 10;
    [self.commentButton setTitle:@"发送" forState:UIControlStateNormal];
    [self.commentButton addTarget:self action:@selector(commentButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.bottomView addSubview:self.commentButton];
    //关闭button的用户交互
    self.commentButton.userInteractionEnabled = NO;
    
}
//键盘即将出现的时候
- (void)keyboardWillShow:(NSNotification *)sender{
    
    CGRect keyboardRect = [(sender.userInfo[UIKeyboardFrameBeginUserInfoKey]) CGRectValue];
    //改变bttomView的y值，防止被键盘遮住
    CGRect bottomViewRect = self.bottomView.frame;
    bottomViewRect.origin.y = self.frame.size.height - keyboardRect.size.height - bottomViewRect.size.height;
    
    self.bottomView.frame = bottomViewRect;
    
    tableView1.frame = CGRectMake(5, [UIScreen mainScreen].bounds.size.height/2-150, [UIScreen mainScreen].bounds.size.width/2, [UIScreen mainScreen].bounds.size.height/4);
}
//键盘即将消失的时候
- (void)keyboardWillHidden:(NSNotification *)sender{
    CGRect bottomViewRect = self.bottomView.frame;
    bottomViewRect.origin.y = self.frame.size.height-65;
    self.bottomView.frame = bottomViewRect;
    tableView1.frame = CGRectMake(5, [UIScreen mainScreen].bounds.size.height/2+40, [UIScreen mainScreen].bounds.size.width/2, [UIScreen mainScreen].bounds.size.height/4);
    
}
//发送按钮的回调方法

- (void)commentButtonAction:(UIButton *)sender{
    //取消第一响应者
    // [self.commentTextField resignFirstResponder];
    //这里把输入框的内容加入数组 并发到服务器
    [self ChatSendMessage];
    NSDictionary *dic = @{@"text":self.commentTextField.text,@"icon":@"head.png"};
    SDStatus *SDstatus = [SDStatus statusWithDic:dic];
    CellFrame *statusFrame = [[CellFrame alloc] init];
    statusFrame.status = SDstatus;
    [_dataArrayy addObject:statusFrame];
    [tableView1 reloadData];
    [[NSNotificationCenter defaultCenter]postNotificationName:@"message" object:nil userInfo:dic];
    self.commentTextField.text = @"";
    
}
//文本框内容发生变化
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    
    //当文本框只有一个字符的时候，我们需要判定该字符是添加的还是需要删除的。如果是添加，需要打开用户交互，如果是删除，需要关闭用户交互
    if (string.length){//添加字符串，打开用户交互
        self.commentButton.userInteractionEnabled = YES;
    }
    else{
        if (textField.text.length <= 1) {
            self.commentButton.userInteractionEnabled = NO;
        }
    }
    return YES;
}
///////********************头视图 的相关实现 代码 **************************//////////

#pragma mark tableView数据源代理
-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    if([tableView isEqual:self.tableView]){
        return 40.0f;
    }
    return 0.0f;
}
-(UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    UIView*headView = nil;
    headView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 40)];
    headView.backgroundColor = [UIColor grayColor];
    UIButton *button1 = [UIButton buttonWithType:UIButtonTypeCustom];
    button1.frame = CGRectMake(self.bounds.size.width/2-30, 5, 60, 30);
    [button1 setTitle:@"用户小芳" forState:UIControlStateNormal];
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
    [self danmu];
    [self tabbar];
}
#pragma mark-- TableView 协议方法的实现
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if([tableView isEqual:tableView1]){
        
        return self.dataArrayy.count;
        
    }else{
        return self.messageArray.count;
    }
}
-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    if([tableView isEqual:tableView1])
    {
        MessageTableViewCell *cell = [MessageTableViewCell cellWithTableView:tableView];
        cell.statusFrame = _dataArrayy[indexPath.row];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.backgroundColor =  [UIColor colorWithWhite:0.f alpha:0.5];
        //cell.backgroundColor = [UIColor clearColor];
        //cell.textLabel.textColor = [UIColor redColor];
        //判断是不是查看历史记录，如果是的话不更新cell，不是的话cell向上移动
        if (indexPath.row == _dataArrayy.count -1)
        {
            self.isHeistory = NO;
        }
        else
        {
            self.isHeistory = YES;
        }
        return cell;
    }else{
        MessageCell*cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
        
        if (!cell) {
            cell = [[MessageCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
        }
        cell.messageModel = self.messageArray[indexPath.row];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        return cell;
    }
    return nil;
}
-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if ([tableView isEqual:tableView1]) {
        CellFrame *statusFrame = _dataArrayy[indexPath.row];
        return statusFrame.cellHeight;
    }else{
        MessageModel *messModel =self.messageArray[indexPath.row];
        CGRect rect = [messModel.message boundingRectWithSize:CGSizeMake(200, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14]} context:nil];
        return 50+rect.size.height;
    }
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if ([tableView isEqual:tableView1]){
        return 1;
    }else{
        return 1;
    }
    
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    //断开WebSocket的销毁
    self.webSocket.delegate = nil;
    [self.webSocket close];
    self.webSocket = nil;
    
}
#pragma PLPlayerDelegate
-(void)player:(PLPlayer *)player stoppedWithError:(NSError *)error
{
    NSLog(@"出错了。。。。。%@", [error localizedDescription]);
}
#pragma mark--WebSocket代理方法的实现
//代理方法实现
#pragma mark - SRWebSocketDelegate
- (void)webSocketDidOpen:(SRWebSocket *)webSocket{
    
    NSLog(@"连接");
    
}
//连接失败
- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error{
    NSLog(@":( Websocket 连接失败的原因是： %@", error);
    self.webSocket = nil;
    
}
//接受到新消息的处理
-(void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message{
    NSString *msgString = message;
    NSData *data = [msgString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *toUse = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:nil];
    NSLog(@"数据：%@",toUse);
    if (toUse == nil) {return;}
    NSArray *array = [toUse objectForKey:@"list"];
    for (NSDictionary*dic in array) {
        MessageModel *mModel =[[MessageModel alloc]init];
        mModel.name = @"123456";
        mModel.message = @"你好";
        mModel.state = @"1";
        [_messageArray addObject:mModel];
        [self.tableView reloadData];
    }
}
//连接关闭
-(void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean{
    NSLog(@"连接关闭：%@",reason);
    self.webSocket = nil;
}
-(void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)pongPayload{
    NSString*reply = [[NSString alloc]initWithData:pongPayload encoding:NSUTF8StringEncoding];
    NSLog(@"接受的数据：%@",reply);
}

/*******************与服务器打交道的函数************************/
//礼物发送函数
-(void)senderGift{
    NSURL*url = [NSURL URLWithString:@"http://192.168.1.3:9001/appUser/senderGift"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //网络请求参数
    NSDictionary *param = @{
                            @"senderid":@"11" ,              //送礼人,
                            @"receiveid":@"12" ,            //收礼人,
                            @"giftid":@"11",               //礼物ID
                            @"totalfee":@"22",            //总金额，
                            @"price":@"120",             //单价，
                            @"quantity":@"10",          //数量,
                            @"liveid":@"4" ,           //直播标识
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
    }];
}

//私聊 消息向服务器发送消息
-(void)sendMessage{
    NSURL*url = [NSURL URLWithString:@"http://192.168.1.3:9001/appUser/sendPrviteMail"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //网络 请求
    NSDictionary *param = @{
                            @"conttext":self.inputMsg.text,
                            @"liveid":@"11",
                            @"receiveid" : @"11",
                            @"senderid" : @"4",
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
    }];
}

//聊天消息发送服务器
-(void)ChatSendMessage{
    NSURL*url = [NSURL URLWithString:@"http://192.168.1.3:9001/appUser/chatOnline"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //网络 请求
    NSDictionary *param = @{
                            @"conttext":self.commentTextField.text,
                            @"liveid":@"11",
                            @"accountid":@"4",
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
    }];
}

/***************************************
 礼物清单的获取
 ***************************************/
-(void*)array{
    NSMutableArray*arrayy = [NSMutableArray array];
    NSURL*url = [NSURL URLWithString:@"http://192.168.1.3:9001/appUser/queryGiftList"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //网络 请求
    NSDictionary *param = @{
                            @"pageNo" : @"1",
                            @"pageSize" : @"10",
                            };
    // NSString *append = [NSString stringWithFormat:@"%@=%@&%@=%@",@"pageNo",@"1",@"pageSize",@"10"];
    //  NSDictionary * paradic = [PlatFormUtil generalConfigParameters:param byAppend:append];
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
        NSDictionary*dict = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
        if (dict == nil) {return;}
        NSArray *array = [dict objectForKey:@"list"];
        for (NSDictionary*dic in array) {
            GiftModel*model = [[GiftModel alloc]init];
            model.giftid =  dic[@"giftid"];
            model.name1 = dic[@"name"];
            model.photoname = dic[@"photoname"];
            model.price = dic[@"price"];
            [arrayy addObject:model];
            NSLog(@"%@",arrayy);
        }
        _GiftArray = arrayy;
        
    }];
    _GiftArray = arrayy;
    return nil;
}
@end
