#ifndef WEB_DOWNLOAD_CANCEL_DELEGATE_H
#define WEB_DOWNLOAD_CANCEL_DELEGATE_H

/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
#import <Cocoa/Cocoa.h>

/*
    Version $Id$
 */
@protocol WebDownloadCancelDelegate

- (void)downloadCancelRequested:(NSURLDownload *)download

@end


#endif /* WEB_DOWNLOAD_CANCEL_DELEGATE_H */
