//
//  BotttonToolView.m
//  PLPlayerKit
//
//  Created by wz on 16/8/31.
//  Copyright © 2016年 0dayZh. All rights reserved.
//

#import "BotttonToolView.h"
#define SCREENWIDTH  [UIScreen mainScreen].bounds.size.width
#define SCREENHEIGH  [UIScreen mainScreen].bounds.size.height
@implementation BotttonToolView
static NSString *identifier1 = @"Cell1";
static NSString *identifier2 = @"Cell2";
static NSString *identifier3 = @"Cell3";
- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self setup];
    }
    return self;
}
- (NSArray *)tools
{
    return @[@"chat",@"gift", @"message"];
}
- (void)setup
{
    CGFloat wh = 35;
    CGFloat margin = (SCREENWIDTH - wh * self.tools.count) / (self.tools.count + 1.0);
    CGFloat x = 0;
    CGFloat y = 0;
    for (int i = 0; i<self.tools.count; i++) {
        x = margin + (margin + wh) * i;
        if (i==1) {
            y = -15;
        }else{
            y = 0;
        }
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
