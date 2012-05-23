/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
#import "PropertyChangeListener.h"

/*
    Version $Id$
 */
@implementation PropertyChangeListener

/* JNI class, member, and method info global variables. The first parameter specifies the global variable name. */
static JNF_CLASS_CACHE(PropertyChangeListener_class, "java/beans/PropertyChangeListener");
static JNF_MEMBER_CACHE(PropertyChangeListener_propertyChange, PropertyChangeListener_class, "propertyChange", "(Ljava/beans/PropertyChangeEvent;)V");

- (void)propertyChange
{
    // Keep the AppKit thread attached to the JVM as long as it's alive. This causes subsequent calls to JNFObtainEnv to
    // return quickly, and reduces the overhead of frequently using PropertyChangeListener to execute Java code on the AppKit
    // thread.
    JNFThreadContext tc = JNFThreadDetachOnThreadDeath;
    JNIEnv *env = NULL;
    @try
    {
        env = JNFObtainEnv(&tc);
        JNFCallVoidMethod(env, [self jObject], PropertyChangeListener_propertyChange, NULL);
    }
    @finally
    {
        if (env != NULL)
            JNFReleaseEnv(env, &tc);
    }
}

@end