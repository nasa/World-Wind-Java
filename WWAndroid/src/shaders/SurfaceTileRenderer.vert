/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

/*
 * OpenGL ES Shading Language v1.00 vertex shader for SurfaceTileRenderer. Transforms surface geometry vertices from
 * model coordinates to eye coordinates, and specifies a varying tile texture coordinate vector that can be used to
 * sample a 2D texture for each fragment in a surface tile's sector.
 *
 * version $Id: SurfaceTileRenderer.vert 15985 2011-09-14 00:26:45Z dcollins $
 */

/*
 * Input vertex attribute defining the surface vertex point in model coordinates. This attribute is specified in
 * SurfaceGeometry.render.
 */
attribute vec4 vertexPoint;
/*
 * Input vertex attribute defining the surface vertex normalized texture coordinate. This attribute is specified in
 * SurfaceGeometry.render.
 */
attribute vec4 vertexTexCoord;
/*
 * Input uniform matrix defining the current modelview-projection transform matrix. Maps model coordinates to eye
 * coordinates.
 */
uniform mat4 mvpMatrix;
/*
 * Input uniform matrix defining the tile coordinate transform matrix. Maps normalized surface texture coordinates to
 * normalized tile coordinates.
 */
uniform mat4 tileCoordMatrix;
/*
 * Input uniform matrix defining the texture coordinate transform matrix. Maps normalized surface texture coordinates to
 * tile texture coordinates.
 */
uniform mat4 texCoordMatrix;

/*
 * Output varying vector to SurfaceTileRenderer.frag defining the normalized tile coordinate for each fragment. This is
 * specified for each vertex and is interpolated for each rasterized fragment of each primitive. Although the input
 * attribute used to compute this value is a vec4, we output this as a vec2 to avoid unnecessary swizzling in the
 * fragment shader.
 */
varying vec2 tileCoord;
/*
 * Output variable vector to SurfaceTileRenderer.frag defining the texture coordinate for each fragment. This is
 * specified for each vertex and is interpolated for each rasterized fragment of each primitive. Although the input
 * attribute used to compute this value is a vec4, we output this as a vec2 to avoid unnecessary swizzling in the
 * fragment shader.
 */
varying vec2 texCoord;

/*
 * OpenGL ES vertex shader entry point. Called for each vertex processed when this shader's program is bound.
 */
void main()
{
    /* Transform the surface vertex point from model coordinates to eye coordinates. */
    gl_Position = mvpMatrix * vertexPoint;

    /* Transform the surface vertex texture coordinate from normalized surface coordinates to normalized tile coordinates. */
    tileCoord = (tileCoordMatrix * vertexTexCoord).st;

    /* Transform the surface vertex texture coordinate from normalized surface coordinates to texture coordinates. */
    texCoord = (texCoordMatrix * vertexTexCoord).st;
}
