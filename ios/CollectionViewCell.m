//
//  LiveKit
//
//  Created by 文宗 on 16/9/21.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//
#import "CollectionViewCell.h"

@implementation CollectionViewCell
-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
       UIImageView *imageView = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,frame.size.width, frame.size.width)];
        imageView.image = [UIImage imageNamed:@"head"];
        imageView.layer.masksToBounds = YES;
        imageView.layer.cornerRadius = 30.0;
        imageView.layer.borderWidth = 4.0;
        imageView.layer.borderColor = [[UIColor whiteColor] CGColor];
        imageView.userInteractionEnabled = YES;
        [self.contentView addSubview:imageView];
        self.button =[UIButton buttonWithType:UIButtonTypeCustom];
        _button.frame  = CGRectMake(frame.size.width - 25, frame.size.height - 25, 20, 20);
        [imageView addSubview:_button];
        //[_button setImage:[UIImage imageNamed:@"ww"] forState:UIControlStateNormal];
        [_button setImage:[UIImage imageNamed:@"backButton"] forState:UIControlStateSelected];
        [_button addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchDown];
    }
    return self;
}
-(void)buttonClick:(UIButton *)sender
{
    sender.selected = !sender.selected;
}
@end
