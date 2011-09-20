/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

/*
 * OpenGL ES Shading Language v1.00 fragment shader for SurfaceTileRenderer. Displays the color of a specified 2D
 * texture for each fragment in a surface tile's sector. Displays transparent black (0, 0, 0, 0) if the fragment is
 * outside of the sector.
 *
 * version $Id: SurfaceTileRenderer.frag 15986 2011-09-14 00:35:33Z dcollins $
 */

precision mediump float;

/*
 * Input uniform sampler defining the tile's 2D texture sampler, specified in SurfaceTileRenderer.java. This
 * variable's value represents the texture unit (0, 1, 2, etc.) that the tile's texture is bound to.
 */
uniform sampler2D tileTexture;

/*
 * Input varying vector from SurfaceTileRenderer.vert defining the tile's normalized coordinate for the current
 * fragment. Values in the range [0, 1] are within the tile's sector.
 */
varying vec2 tileCoord;
/*
 * Input varying vector from SurfaceTileRenderer.vert defining the texture coordinate for the current fragment. This
 * texture coordinate is associated with the tileTexture uniform sampler.
 */
varying vec2 texCoord;

/*
 * Local function that returns the tile's RGBA color at the specified texture coordinate. The tile coordinate vector is
 * used to determine if the current fragment is inside or outside the tile's sector. This returns transparent black
 * (0, 0, 0, 0) if the fragment is outside the tile's sector.
 */
vec4 tileColor(sampler2D sampler, vec2 tileCoord, vec2 texCoord)
{
    /* Compute a value of 1.0 or 0.0 depending on whether the current fragment is inside or outside the tile's sector, */
    /* respectively. */
    float factor = float(tileCoord.s >= 0.0 && tileCoord.s <= 1.0 && tileCoord.t >= 0.0 && tileCoord.t <= 1.0);

    /* Multiply the tile texture's RGBA values by the computed factor. This masks out the tile's texture color for */
    /* fragments outside the tile's sector. We multiply rather than branching because GPU processors are more efficient */
    /* at fetching a potentially unused texel color than predicting a branch. */
    return texture2D(sampler, texCoord) * factor;
}

/*
 * OpenGL ES fragment shader entry point. Called for each fragment rasterized when this shader's program is bound. This
 * fragment shader makes two assumptions about the current GL state:
 * 1) GL blending is enabled.
 * 2) GL blend func is configured as sfactor=GL_ONE, dfactor=GL_ONE_MINUS_SRC_ALPHA (pre-multiplied blending mode).
 */
void main()
{
    /* Assign the fragment color to the tile color. We avoid branching and calling discard when the alpha value is zero */
    /* because calling discard in a branch has been shown to increase the frame time by 3x on the Samsung Galaxy Tab */
    /* 10.1. */
    gl_FragColor = tileColor(tileTexture, tileCoord, texCoord);
}
