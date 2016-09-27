//
//  giftCollectionViewCell.m
//  LiveKit
//
//  Created by 文宗 on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "giftCollectionViewCell.h"

@implementation giftCollectionViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.imageView.layer.cornerRadius =10;
    self.imageView.layer.masksToBounds = YES;
}
-(void)setImageView:(UIImageView *)imageView{

    _imageView = [imageView copy];
    self.imageView.image = [UIImage imageNamed:imageView];
}
-(void)setGift:(UILabel *)gift{
    _gift = [UILabel copy];
    self.gift.text =  gift.text;
}
-(void)setPrice:(UILabel *)price{
    
    _price  =[UILabel copy];
    self.price.text = price.text;

}
@end
