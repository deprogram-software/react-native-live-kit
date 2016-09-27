
//  LiveKit
//
//  Created by 文宗 on 16/9/16.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CellFrame.h"
@interface MessageTableViewCell : UITableViewCell



@property (nonatomic, strong) CellFrame *statusFrame;

@property (nonatomic, readonly, retain) UIImageView *iconView; //头像

@property (nonatomic, readonly, retain) UILabel *CommentLabel; //发送评论的内容

+ (MessageTableViewCell *)cellWithTableView:(UITableView *)tableView;

@end
