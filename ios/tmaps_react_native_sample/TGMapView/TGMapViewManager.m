//
//  TGMapViewManager.m
//  tmaps_react_native_sample
//
//  Created by Tagipedia on 2/13/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTViewManager.h>
#import "TGMapView.h"

@interface TGMapViewManager : RCTViewManager
@end

@implementation TGMapViewManager

RCT_EXPORT_MODULE()

RCT_EXPORT_VIEW_PROPERTY(onProgressChanged, RCTBubblingEventBlock)

- (UIView *)view
{
  
  return [[TGMapView alloc] init];
}

RCT_CUSTOM_VIEW_PROPERTY(mapId, NSString*, TGMapView)
{
  view.mapId = json;
}

RCT_CUSTOM_VIEW_PROPERTY(featureId, NSString*, TGMapView)
{
  view.featureId = json;
}

RCT_CUSTOM_VIEW_PROPERTY(primaryColor, NSString*, TGMapView)
{
  view.primaryColor = json;
}

RCT_CUSTOM_VIEW_PROPERTY(tenants, NSArray*, TGMapView)
{
  view.tenants = json;
}

RCT_CUSTOM_VIEW_PROPERTY(secondaryColor, NSString*, TGMapView)
{
  view.secondaryColor = json;
}

@end
