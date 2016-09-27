//
//  giftCollectionViewLayout.m
//  LiveKit
//
//  Created by  on 16/9/8.
//  Copyright © 2016年 Yuanyin Guoji. All rights reserved.
//

#import "giftCollectionViewLayout.h"
@interface giftCollectionViewLayout()

@property (nonatomic,strong)NSMutableArray *attrsArray;

@end

@implementation giftCollectionViewLayout

-(NSMutableArray*)attrsArray{
    
    if (!_attrsArray) {
        _attrsArray = [NSMutableArray array];
    }
    return _attrsArray;
}
-(void)prepareLayout{
    [super prepareLayout];
    [self.attrsArray removeAllObjects];
    
    NSInteger count = [self.collectionView numberOfItemsInSection:0];
    for (int i = 0 ;i < count ;i++) {
        NSIndexPath*indexPath = [NSIndexPath indexPathForItem:i inSection:0];
        UICollectionViewLayoutAttributes *attrs = [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
        CGFloat width = self.collectionView.frame.size.width * 0.2;
        CGFloat height = width;
        CGFloat x = i*width;
        CGFloat y = 0;
        if (i>4) {
            x = (i-5)*width;
            y = height;
            
            
        }
        attrs.frame = CGRectMake(x, y, width, height);
        [self.attrsArray addObject:attrs];
    }
}
//返回所有视图的属性
- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    return self.attrsArray;
}
-(CGSize)collectionViewContentSize{
    
    return CGSizeMake(60, 60);
}

@end












