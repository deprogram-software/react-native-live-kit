//
//  PrMessageCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PMessageModel.h"
@interface PrMessageCell : UITableViewCell

@property(nonatomic,strong)UILabel *messageLabel;
@property(nonatomic,strong)UIImageView *headImg;
@property(nonatomic,strong)PMessageModel *messageModel;
@end
