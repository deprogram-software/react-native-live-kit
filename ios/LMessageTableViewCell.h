//
//  LMessageTableViewCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LiveCellFrame.h"
@interface LMessageTableViewCell : UITableViewCell
@property (nonatomic, strong) LiveCellFrame *statusFrame;

@property (nonatomic, readonly, retain) UIImageView *iconView; //头像

@property (nonatomic, readonly, retain) UILabel *CommentLabel; //发送评论的内容
//@property(nonatomic,readonly,retain)UILabel *UserName;

+ (LMessageTableViewCell *)cellWithTableView:(UITableView *)tableView;
@end
