//
//  LiveCellFrame.m
//  LiveKit
//
//  Created by 文宗 on 16/9/20.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "LiveCellFrame.h"

@implementation LiveCellFrame

- (void)setStatus:(sSDStatus *)status{
    
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
    CGSize textSize = [self sizeWithText:_status.text font:[UIFont systemFontOfSize:14] maxSize:CGSizeMake([UIScreen mainScreen].bounds.size.width/2-65,MAXFLOAT)];
    CGFloat textW = textSize.width;
    CGFloat textH = textSize.height;
    _textF = CGRectMake(textX, texty, textW, textH);
    
    /*/  用户文字内容
    CGFloat UserX = padding*2 + 35;
    CGFloat Usery = CGRectGetMaxY(_iconF);
    CGSize UserSize = [self sizeWithText:_status.text font:[UIFont systemFontOfSize:14] maxSize:CGSizeMake([UIScreen mainScreen].bounds.size.width,Usery)];
    CGFloat UserW = textSize.width;
    CGFloat UserH = textSize.height;
    _textF = CGRectMake(UserX, Usery, UserW, UserH);

    */
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
