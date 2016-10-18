#import "AppInfo.h"

@implementation AppInfo

- (void)getAppInfo:(CDVInvokedUrlCommand*)command
{
    NSDictionary *appInfoDict = NSBundle.mainBundle.infoDictionary;

    NSString *identifier = appInfoDict[@"CFBundleIdentifier"];
    NSString *version = appInfoDict[@"CFBundleShortVersionString"];
    NSString *build = appInfoDict[@"CFBundleVersion"];
    NSString *appVersion = [self getAppVersion];
    
    NSDictionary *appInfo = @{@"identifier": identifier,
                              @"version": version,
                              @"build": build,
                              @"market": appVersion};

    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:appInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getVersion:(CDVInvokedUrlCommand*)command
{
    NSString* versionName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];

    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:versionName];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
- (void)getBuild:(CDVInvokedUrlCommand*)command
{
    NSString* build = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];

    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:build];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getIdentifier:(CDVInvokedUrlCommand*)command
{
    NSString* identifier = NSBundle.mainBundle.infoDictionary[@"CFBundleIdentifier"];
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:identifier];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
    
-(NSString*) getAppVersion{
    NSDictionary* infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString* appID = infoDictionary[@"CFBundleIdentifier"];
    
    NSURL* url = [NSURL URLWithString:[NSString stringWithFormat:@"http://itunes.apple.com/lookup?bundleId=%@", appID]];
    NSData* data = [NSData dataWithContentsOfURL:url];
    NSDictionary* lookup = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    
    NSString* appStoreVersion = [NSString stringWithFormat:@""];
    if ([lookup[@"resultCount"] integerValue] == 1){
        appStoreVersion = lookup[@"results"][0][@"version"];
    }
    return appStoreVersion;
}

@end
