//
//  TGMapView.m
//  tmaps_react_native_sample
//
//  Created by Tagipedia on 2/13/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TGMapView.h"
#import <WebKit/WebKit.h>
#import <WebKit/WKScriptMessageHandler.h>

@interface TGMapView () <WKScriptMessageHandler, WKNavigationDelegate>
@property (nonatomic, weak) WKWebView* webView;
@end


@implementation TGMapView

- (instancetype)initWithCoder:(NSCoder *)coder
{
    self = [super initWithCoder:coder];
    if (self) {
      [self loadWebView];
    }
    return self;
}

- (id)init
{
    self = [super init];
    if (self) {
      [self loadWebView];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
    [self loadWebView];
  }
  return self;
}

-(void) loadWebView {
    WKWebView* webView = [[WKWebView alloc] init];
    self.webView = webView;
    self.webView.navigationDelegate = self;
    webView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.webView addObserver:self forKeyPath:@"estimatedProgress" options:NSKeyValueObservingOptionNew context:NULL];
    
    [self addSubview:webView];
    
    NSLayoutConstraint *width =[NSLayoutConstraint
                                constraintWithItem:webView
                                attribute:NSLayoutAttributeWidth
                                relatedBy:0
                                toItem:self
                                attribute:NSLayoutAttributeWidth
                                multiplier:1.0
                                constant:0];
    NSLayoutConstraint *height =[NSLayoutConstraint
                                 constraintWithItem:webView
                                 attribute:NSLayoutAttributeHeight
                                 relatedBy:0
                                 toItem:self
                                 attribute:NSLayoutAttributeHeight
                                 multiplier:1.0
                                 constant:0];
    NSLayoutConstraint *top = [NSLayoutConstraint
                               constraintWithItem:webView
                               attribute:NSLayoutAttributeTop
                               relatedBy:NSLayoutRelationEqual
                               toItem:self
                               attribute:NSLayoutAttributeTop
                               multiplier:1.0f
                               constant:0.f];
    NSLayoutConstraint *leading = [NSLayoutConstraint
                                   constraintWithItem:webView
                                   attribute:NSLayoutAttributeLeading
                                   relatedBy:NSLayoutRelationEqual
                                   toItem:self
                                   attribute:NSLayoutAttributeLeading
                                   multiplier:1.0f
                                   constant:0.f];
    [self addConstraint:width];
    [self addConstraint:height];
    [self addConstraint:top];
    [self addConstraint:leading];
    [self setTagipediaObjectAndLoadMap];
}


-(void) setTagipediaObjectAndLoadMap {
    NSString* __tbString = @"window.__tb__ = {dispatch: function(action) {webkit.messageHandlers.__tb__.postMessage(action); } }";
    
    //[self injectScript:__tbString atTime:WKUserScriptInjectionTimeAtDocumentStart];
    [self injectScript:__tbString atTime:WKUserScriptInjectionTimeAtDocumentEnd];
    
    
    WKWebView* webview = (WKWebView*) self.webView;
    [webview.configuration.userContentController addScriptMessageHandler:self name:@"__tb__"];
    
    [self navigateWebView];
    
}

-(void) injectScript:(NSString*) script atTime:(WKUserScriptInjectionTime) time {
    WKWebView* webview = (WKWebView*) self.webView;
    WKUserScript* userScript = [[WKUserScript alloc] initWithSource:script injectionTime:time forMainFrameOnly:YES];
    [webview.configuration.userContentController addUserScript:userScript];
}

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if ([message.body isKindOfClass:[NSDictionary class]]) {
        [self mapViewController:self didReceiveDispatchWithCommand:(NSDictionary *)message.body];
    }
}


-(void) navigateWebView {
    NSString* path = [[NSBundle mainBundle] pathForResource:@"tmapswww/index" ofType:@"html"];
    NSError* error;
    NSString* htmlString = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:&error];
    
    htmlString = [self transformHtmlStringForTMaps:htmlString];
    
    if (!error) {
        NSURL* baseUrl = [[NSBundle mainBundle] URLForResource:@"tmapswww/index" withExtension:@"html"];
        [self.webView loadHTMLString:htmlString baseURL:baseUrl];
    }
}

-(NSString*) transformHtmlStringForTMaps:(NSString*) htmlString {
    NSMutableString* result = [NSMutableString stringWithString:htmlString];
    
    [result replaceOccurrencesOfString:@"embed-class-placeholder" withString:@"embed-class-placeholder ios-embed" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [result length])];
    
    return [result copy];
}


-(void) dispatch:(NSDictionary *)command {
    
    [self performSelectorOnMainThread:@selector(dispatchInternal:) withObject:command waitUntilDone:NO];
}

-(void) dispatchInternal:(NSDictionary *)command {
    
    NSError*error;
    NSData *commandJsonData = [NSJSONSerialization dataWithJSONObject:command options:0 error:&error];
    NSString* commandJson = [[NSString alloc] initWithData:commandJsonData encoding:NSUTF8StringEncoding];
    NSString* commandString = [NSString stringWithFormat:@"Tagipedia.dispatch(%@)", commandJson];
    //NSLog(@"%@", commandString);
    [self.webView evaluateJavaScript:commandString completionHandler:^(id result, NSError* error) {
        if (error) {
            NSLog(@"Error: %@", error);
        }
    }];
}

- (void)webViewWebContentProcessDidTerminate:(WKWebView *)webView {
     NSLog(@"%s",__FUNCTION__);
}


- (NSArray *)getTenantsJSON
{
  NSString *path = [[NSBundle mainBundle] pathForResource:@"tenants" ofType:@"json"];
  NSData *data = [NSData dataWithContentsOfFile:path];
  return [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
}

-(void) mapViewController:(TGMapView *)controller didReceiveDispatchWithCommand:(NSDictionary *)command {
  NSLog(@"received dispatch %@", command);
  // You can dispatch whatever command
  NSString* type = (NSString*)[command valueForKey:@"type"];
  if ([type isEqualToString:@"READY"] && self.mapId != nil) {
    //        // change app theme
    //        [controller dispatch:@{
    //                               @"type": @"SET_THEME",
    //                               @"theme": @{
    //                                       @"primary":@"brown",
    //                                       }
    //                               }];
//    [controller dispatch:@{
//                        @"type": @"SET_TENANT_DATA",
//                        @"payload": self.tenants
//                      }];
    // load map
    [controller dispatch:@{
                           @"type": @"LOAD_MAP",
                           @"map_id": self.mapId,
                          }];
//    [controller dispatch:@{
//                           @"type": @"SET_DEVICE_DATA",
//                           @"device_id": [NSString stringWithFormat:@"%@",[[[UIDevice currentDevice] identifierForVendor] UUIDString]],
//                           @"device_type": @"IOS"
//                           }];
  } else if ([type isEqualToString:@"MAP_LOADED"]) {
    //        [controller dispatch:@{
    //                               @"type": @"CHANGE_RENDER_MODE",
    //                               @"modeToRender": @"2D"
    //                               }];
    //        [controller dispatch:@{
    //                               @"type": @"SET_ZOOM",
    //                               @"zoom": @20,
    ////                               @"zoom_type": @"FLY_TO",
    //                               }];
    //        [controller dispatch:@{
    //                               @"type": @"SET_CENTER",
    //                               @"center": @[lng, lat],
    //                               }];
    if (self.featureId){
      [controller dispatch:@{
                             @"type": @"HIGHLIGHT_FEATURE",
                             @"feature_id": self.featureId
                             }];
    }
  } else if ([type isEqualToString:@"FEATURES_TAPPED"]) {
    // highlight feature
    [controller dispatch:@{
                           @"type": @"HIGHLIGHT_FEATURE",
                           @"feature_id": [(NSDictionary*)[(NSArray*)command[@"features"] objectAtIndex:0] valueForKey:@"id"]
                           }];
  } else if ([type isEqualToString:@"ASSOCIATED_FEATURE_TAPPED"]) {
    
  } else if ([type isEqualToString:@"FEATURE_MARKED"]) {
    
  } else if ([type isEqualToString:@"FEATURE_HIGHLIGHTED"]) {
    
  } else if ([type isEqualToString:@"ZOOM_ENDED"]) {
    
  } else if ([type isEqualToString:@"CENTER_ENDED"]) {
    
  } else if ([type isEqualToString:@"CATEGORY_MARKED"]) {
    
  } else if ([type isEqualToString:@"ERROR"]) {
    
  } else if ([type isEqualToString:@"PROFILE_BUTTON_CLICKED"]) {
    // this is example for custom dispatch type that you dispatched in your custom templete when custom profile button clicked
  }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
  if ([keyPath isEqualToString:@"estimatedProgress"] && object == self.webView) {
    if (self.onProgressChanged) {
      self.onProgressChanged(@{
                               @"progress": @(self.webView.estimatedProgress)
                               });
    }
  }
}


- (void)dealloc {
  [self.webView removeObserver:self forKeyPath:@"estimatedProgress"];
}

@end
