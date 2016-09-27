//
//  giftCollectionViewCell.h
//  LiveKit
//
//  Created by 文宗 on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface giftCollectionViewCell : UICollectionViewCell
@property (unsafe_unretained, nonatomic) IBOutlet UIImageView *imageView;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *price;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *gift;
@end
