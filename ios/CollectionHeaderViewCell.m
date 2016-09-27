//
//  LiveKit
//
//  Created by 文宗 on 16/9/21.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "CollectionHeaderViewCell.h"

@implementation CollectionHeaderViewCell
-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        UILabel *label = [[UILabel alloc]initWithFrame:frame];
        label.textAlignment = NSTextAlignmentCenter;
        label.text = @"观看观众";
        [self addSubview:label];
    }
    return self;
}
@end
