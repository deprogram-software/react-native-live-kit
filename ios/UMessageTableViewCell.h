//
//  UMessageTableViewCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UserModel.h"
@interface UMessageTableViewCell : UITableViewCell
@property(nonatomic,strong)UILabel *messageLabel;
@property(nonatomic,strong)UIImageView *headImg;
@property(nonatomic,strong)UILabel*messageText;
@end
