//  PLPlayerKit
//
//  Created by 文宗 on 16/8/29.
//  Copyright © 2016年 0dayZh. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PlatFormUtil : NSObject
+ (NSDictionary *)generalConfigParameters:(NSDictionary *)dic byAppend:(NSString *)append;

+ (NSDictionary *)WebSocketGeneralConfigParameters:(NSDictionary *)dic byAppend:(NSString *)append;

+ (NSString *)parseDictionaryToFormattedString:(NSDictionary *)dic;
@end
