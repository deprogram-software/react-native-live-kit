
#import "ISNull.h"

@implementation ISNull
+(BOOL)isNilOfObject:(NSObject *)object
{

      if (!object) {
        
         return YES;
    //是不是数组类
    }else if ([object isKindOfClass:[NSArray class]])
    {
        
        NSArray *array=(NSArray *)object;
        
        if (array.count==0) {
            
            return YES;
        }else
        {
            return NO;
        }
    // 判断是不是字典
    }else if ([object isKindOfClass:[NSDictionary class]])
    {
    
      NSDictionary *dict=(NSDictionary *)object;
        
        if ([dict allKeys].count==0) {
            
            return YES;
        }else
        {
            return NO;
        }
    //判断是不是字符串
    }else if ([object isKindOfClass:[NSString class]])
    {
        NSString *string=(NSString *)object;
        if (string.length==0) {
            return YES;
        }else
        {
            return NO;
        }
        
    }
    return YES;
}
@end
