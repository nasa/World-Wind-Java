#ifndef OGL_UTIL_H
#define OGL_UTIL_H

/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
#import <Cocoa/Cocoa.h>
#import <OpenGL/gl.h>

/*
    Version $Id$
 */

extern void loadBitmapInGLTexture(GLenum target, NSBitmapImageRep *bitmap);

#endif /* OGL_UTIL_H */