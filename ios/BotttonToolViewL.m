//
//  BotttonToolView.m
//  PLPlayerKit
//
//  Created by wz on 16/8/31.
//  Copyright © 2016年 0dayZh. All rights reserved.
//

#import "BotttonToolViewL.h"
#define SCREENWIDTH  [UIScreen mainScreen].bounds.size.width
#define SCREENHEIGH  [UIScreen mainScreen].bounds.size.height
@implementation BotttonToolViewL

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self setup];
    }
    return self;
}
- (NSArray *)tools
{
    return @[@"Dmessages",@"kicking"];
}
- (void)setup
{
    CGFloat wh = 50;
    CGFloat margin = (SCREENWIDTH - wh * self.tools.count) / (self.tools.count + 1.0);
    CGFloat x = 0;
    CGFloat y = 0;
    for (int i = 0; i<self.tools.count; i++) {
        x = margin + (margin + wh) * i;
        UIImageView *toolView = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, wh, wh)];
        toolView.userInteractionEnabled = YES;
        toolView.tag = i;
        toolView.image = [UIImage imageNamed:self.tools[i]];
        [toolView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(click:)]];
        [self addSubview:toolView];
    }
}

- (void)click:(UITapGestureRecognizer *)tapRec
{
    if (self.clickToolBlock) {
        self.clickToolBlock(tapRec.view.tag);
    }
}

@end
