#ifndef WEB_DOWNLOAD_CONTROLLER_H
#define WEB_DOWNLOAD_CONTROLLER_H

/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
#import <Cocoa/Cocoa.h>

/*
    Version $Id$
 */
@interface WebDownloadController : NSObject
{
@protected
    NSMutableArray *downloadViews;
    NSWindow *downloadWindow;
}

- (id)init;

- (void)beginDownload:(NSURLDownload *)download;

- (void)endDownload:(NSURLDownload *)download;

- (NSView *)getDownloadView:(NSURLDownload *)download;

- (NSView *)createDownloadView:(NSURLDownload *)download;

- (NSWindow *)createDownloadWindow;

@end


@interface CollectionView : NSView

- (void)layout;

@end

#endif /* WEB_DOWNLOAD_CONTROLLER_H */
