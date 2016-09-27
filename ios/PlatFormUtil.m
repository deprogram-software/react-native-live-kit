//  PLPlayerKit
//
//  Created by 文宗 on 16/8/29.
//  Copyright © 2016年 0dayZh. All rights reserved.
//

#import "PlatFormUtil.h"
#import <CommonCrypto/CommonDigest.h>
#import "WebService.h"

@implementation PlatFormUtil

// WebSocket 的 网络请求参数的加密
+ (NSDictionary *)WebSocketGeneralConfigParameters:(NSDictionary *)dic byAppend:(NSString *)append{
    NSMutableDictionary *tmp;
    
    if (dic) {
        tmp = [dic mutableCopy];
    }else{
        tmp = [NSMutableDictionary new];
    }
    
    NSString * sign = [NSString stringWithFormat:@"%@&%@",append,@"key=ECE893EB7CB170CA5DEF54309485C0AF"];
    sign = [self md532BitUpper:sign];
    
    [tmp setObject:sign forKey:@"clientSign"];
    return tmp;
}
+ (NSDictionary *)generalConfigParameters:(NSDictionary *)dic byAppend:(NSString *)append {

    NSMutableDictionary *tmp;

    if (dic) {
        tmp = [dic mutableCopy];
    }else{
        tmp = [NSMutableDictionary new];
    }

    NSString * sign = [NSString stringWithFormat:@"%@&%@",append,@"key=F81D34A4A965B1EAEEA6C94CF0471C11"];
    sign = [self md532BitUpper:sign];
    
    [tmp setObject:sign forKey:@"clientSign"];
    return tmp;

}
// md5加密函数
+ (NSString*)md532BitUpper:(NSString *)str
{
    
    const char* input = [str UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(input, (CC_LONG)strlen(input), result);
    
    NSMutableString *digest = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (NSInteger i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [digest appendFormat:@"%02x", result[i]];
    }
    
    return digest;
}
//字典转化成其他格式的字符串
//比如：将字典转换成字符串 key=value&key=value&key=value
+ (NSString *)parseDictionaryToFormattedString:(NSDictionary *)dic
{
    NSMutableArray *tempArray = [NSMutableArray array];
    NSArray *keyArray = [dic allKeys];
    for (NSString *key in keyArray) {
        NSString *valueString = [dic objectForKey:key];
        NSString *formattedString = [NSString stringWithFormat:@"%@=%@", key, valueString];
        [tempArray addObject:formattedString];
    }
    NSString *finalString = [tempArray componentsJoinedByString:@"&"];
    return finalString;
}

@end
