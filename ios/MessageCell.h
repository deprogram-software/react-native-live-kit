//
//  MessageCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/13.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MessageModel.h"
@interface MessageCell : UITableViewCell

@property(nonatomic,strong)UILabel *messageLabel;
@property(nonatomic,strong)UIImageView *headImg;
@property(nonatomic,strong)MessageModel *messageModel;

@end
