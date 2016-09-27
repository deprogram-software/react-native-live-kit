//
//  UserTableViewCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UserModel.h"
@interface UserTableViewCell : UITableViewCell
@property (unsafe_unretained, nonatomic) IBOutlet UIImageView *UserImg;


@property (unsafe_unretained, nonatomic) IBOutlet UILabel *UserText;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *UserDetail;
- (void)loadDataFromModel:(UserModel*)model;
@end
