
//  LiveKit
//
//  Created by 文宗 on 16/9/16.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "CellFrame.h"

@implementation CellFrame
- (void)setStatus:(SDStatus *)status{
    
    //在外部传递数据的时候就计算子控件的大小
    _status = status;
    
    [self settingFrame];
    
}

//文字宽高
- (CGSize)sizeWithText:(NSString *)text font:(UIFont *)font maxSize:(CGSize)maxSize{
    
    NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:font, NSFontAttributeName, nil];
    return [text boundingRectWithSize:maxSize options:NSStringDrawingUsesLineFragmentOrigin attributes:dic context:nil].size;
}

- (void)settingFrame{
    
    //间隙
    CGFloat padding = 10;
    //头像
    CGFloat iconX = padding;
    CGFloat iconY = padding;
    CGFloat iconW = 35;
    CGFloat iconH = 35;
    _iconF = CGRectMake(iconX, iconY, iconW, iconH);
    
    //  文字内容
    CGFloat textX = padding*2 + 35;
    CGFloat texty = CGRectGetMaxY(_iconF) -35;
    CGSize textSize = [self sizeWithText:_status.text font:[UIFont systemFontOfSize:14] maxSize:CGSizeMake([UIScreen mainScreen].bounds.size.width,MAXFLOAT)];
    CGFloat textW = textSize.width;
    //[UIScreen mainScreen].bounds.size.width-100; //textSize.width;
    CGFloat textH = textSize.height;
    _textF = CGRectMake(textX, texty, textW, textH);
    
    
    //计算整个cell的高度
    if (textH<35)
    {
        _cellHeight = 55;
    }
    else
    {
        _cellHeight = CGRectGetMaxY(_textF) +padding;
    }
}
@end
