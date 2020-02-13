//
//  TGMapView.h
//  tmaps_react_native_sample
//
//  Created by Tagipedia on 2/13/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#ifndef TGMapView_h
#define TGMapView_h

#import <WebKit/WKScriptMessageHandler.h>
#import <UIKit/UIKit.h>
#import <WebKit/WKWebView.h>
#import <React/RCTComponent.h>

@class TGMapView;

@protocol TGMapViewDelegate<NSObject>
-(void) mapViewController:(TGMapView*) controller didReceiveDispatchWithCommand:(NSDictionary*) command;
@end

@interface TGMapView : UIView

-(void) dispatch:(NSDictionary*) command;
@property (nonatomic, strong) NSString* mapId;
@property (nonatomic, strong) NSString* featureId;
@property (nonatomic, strong) NSString* primaryColor;
@property (nonatomic, strong) NSString* secondaryColor;
@property (nonatomic, strong) NSArray* tenants;
@property (nonatomic, copy) RCTBubblingEventBlock onProgressChanged;

@end

#endif /* TGMapView_h */
