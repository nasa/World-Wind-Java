#ifndef PROPERTY_CHANGE_LISTENER_H
#define PROPERTY_CHANGE_LISTENER_H

/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
#import <Cocoa/Cocoa.h>
#import <JavaNativeFoundation/JavaNativeFoundation.h> // Helper framework for Cocoa and JNI development.

/*
    Version $Id$
 */
@interface PropertyChangeListener : JNFJObjectWrapper
{
}

- (void)propertyChange;

@end

#endif /* PROPERTY_CHANGE_LISTENER_H */
