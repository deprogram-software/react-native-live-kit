//
//  UserTableViewCell.m
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "UserTableViewCell.h"

@implementation UserTableViewCell
- (void)loadDataFromModel:(UserModel *)model{
  //  [_UserImg setImageWithURL:[NSURL URLWithString:model.UserImg]];
    
    
 //   _nameLabel.text = model.name_c;
 //   _realnameLabel.text = model.title;
 //   _tagsLabel.text = model.tags;
}
- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
