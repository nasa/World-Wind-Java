/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

/*
 * OpenGL ES Shading Language v1.00 fragment shader for TiledTessellator picking. Displays either a constant unique RGB
 * color or a unique RGB color assigned to each primitive (triangle).
 *
 * version $Id$
 */

precision mediump float;

/*
 * Input varying vector from TiledTessellatorPick.vert defining the color for each primitive (triangle). This is
 * specified for each vertex and is interpolated for each rasterized fragment of each primitive.
 */
varying vec4 primColor;

/*
 * OpenGL ES fragment shader entry point. Called for each fragment rasterized when this shader's program is bound.
 */
void main()
{
    /* Assign the fragment color to the varying vertex color. */
    gl_FragColor = primColor;
}
