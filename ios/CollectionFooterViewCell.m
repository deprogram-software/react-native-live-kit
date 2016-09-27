//
//  
//  LiveKit
//
//  Created by 文宗 on 16/9/21.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//


#import "CollectionFooterViewCell.h"

@implementation CollectionFooterViewCell
-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.frame = CGRectMake(10, 0, frame.size.width-20, frame.size.height);
        button.layer.masksToBounds = YES;
        button.layer.cornerRadius = 10.0;
        button.layer.borderWidth = 2.0;
        button.layer.borderColor = [[UIColor whiteColor] CGColor];
        [button setTitle:@"踢出" forState:UIControlStateNormal];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        button.backgroundColor =[UIColor yellowColor];
        [button addTarget:self action:@selector(buttonClick) forControlEvents:UIControlEventTouchDown];
        [self.contentView addSubview:button];
    }
    return self;
}
-(void)buttonClick
{
    NSLog(@"点击了踢出按钮");
}
@end
