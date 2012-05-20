/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;

import java.io.IOException;
import java.util.*;

public class KMLModelPlacemarkImpl extends WWObjectImpl implements KMLRenderable
{
    protected final KMLModel model;
    protected final KMLPlacemark parent;
//    protected ColladaSceneShape shape;

    /**
     * Create an instance.
     *
     * @param tc        the current {@link KMLTraversalContext}.
     * @param placemark the <i>Placemark</i> element containing the <i>Point</i>.
     * @param geom      the {@link gov.nasa.worldwind.ogc.kml.KMLPoint} geometry.
     *
     * @throws NullPointerException     if the geometry is null.
     * @throws IllegalArgumentException if the parent placemark or the traversal context is null.
     */
    public KMLModelPlacemarkImpl(KMLTraversalContext tc, KMLPlacemark placemark, KMLAbstractGeometry geom)
    {
        if (tc == null)
        {
            String msg = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (placemark == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (geom == null)
        {
            String msg = Logging.getMessage("nullValue.GeometryIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.model = (KMLModel) geom;
        this.parent = placemark;
    }
//
//    protected ColladaSceneShape createShape(DrawContext dc)
//    {
//        String address = this.model.getLink().getAddress(dc);
//
//        if (WWUtil.isEmpty(address))
//            return null;
//
//        ColladaSceneShape shape = new ColladaSceneShape(new ColladaFilePathResolver()
//        {
//            public String resolveFilePath(String path) throws IOException
//            {
//                 return KMLModelPlacemarkImpl.this.model.getRoot().getSupportFilePath(path);
//            }
//        });
//
//        shape.setModelAddress(address);
//
//        KMLLocation location = this.model.getLocation();
//        if (location != null)
//        {
//            Double lat = location.getLatitude();
//            Double lon = location.getLongitude();
//            Double alt = location.getAltitude();
//            shape.setModelPosition(Position.fromDegrees(
//                lat != null ? lat : 0, lon != null ? lon : 0, alt != null ? alt : 0));
//        }
//        else
//        {
//            shape.setModelPosition(Position.ZERO);
//        }
//
//        KMLOrientation orientation = this.model.getOrientation();
//        if (orientation != null)
//        {
//            if (orientation.getHeading() != null)
//                shape.setHeading(Angle.fromDegrees(orientation.getHeading()));
//            if (orientation.getTilt() != null)
//                shape.setPitch(Angle.fromDegrees(orientation.getTilt()));
//            if (orientation.getRoll() != null)
//                shape.setRoll(Angle.fromDegrees(orientation.getRoll()));
//        }
//
//        KMLScale scale = this.model.getScale();
//        if (scale != null)
//        {
//            double x = scale.getX() != null ? scale.getX() : 1;
//            double y = scale.getY() != null ? scale.getY() : 1;
//            double z = scale.getZ() != null ? scale.getZ() : 1;
//            shape.setModelScale(new Vec4(x, y, z));
//        }
//
//        if (this.model.getResourceMap() != null)
//            shape.setResourceMap(this.createResourceMap(this.model));
//
//        return shape;
//    }

    protected Map<String, Object> createResourceMap(KMLModel model)
    {
        Map<String, Object> map = new HashMap<String, Object>();

        for (KMLAlias alias : model.getResourceMap().getAliases())
        {
            if (alias != null && !WWUtil.isEmpty(alias.getSourceRef()))
            {
                String targetHref = this.formAliasTarget(model, alias);
                if (!WWUtil.isEmpty(targetHref))
                    map.put(alias.getSourceRef(), targetHref);
            }
        }

        return map.size() > 0 ? map : null;
    }

    protected String formAliasTarget(KMLModel model, KMLAlias alias)
    {
        try
        {
            String targetHref = model.getRoot().getSupportFilePath(alias.getTargetHref());
            return !WWUtil.isEmpty(targetHref) ? targetHref : alias.getTargetHref();
        }
        catch (IOException e)
        {
            return alias.getTargetHref();
        }
    }

    public void preRender(KMLTraversalContext tc, DrawContext dc)
    {
    }

    public void render(KMLTraversalContext tc, DrawContext dc)
    {
//        if (this.model.getLink() == null)
//            return;
//
//        String address = this.model.getLink().getAddress(dc);
//        if (WWUtil.isEmpty(address))
//            return;
//
//        if (this.shape == null || !this.shape.getModelAddress().equals(address))
//        {
//            ColladaSceneShape newShape = this.createShape(dc);
//            this.shape = newShape;
//        }
//
//        if (this.shape != null)
//            this.shape.render(dc);
    }
//
//    Double latitude;
//    Double longitude;
//    Double altitude;
//
//    KMLRenderable linkedObject;
//    boolean startedLoading = false;
//    boolean needToFinishInit = true;
//
//    public void preRender(KMLTraversalContext tc, DrawContext dc)
//    {
//        if (!startedLoading && linkedObject == null)
//        {
//            KMLLink link = model.getLink();
//            if (link != null)
//            {
//                startedLoading = true;
//                final String address = link.getAddress(dc);
//
//                KMLRoot.executor.execute(new Runnable()
//                {
//                    public void run()
//                    {
//                        KMLRoot root = parent.getRoot();
//                        Object linkedFile = root.resolveReference(address);
//
//                        KMLModel model = KMLModelPlacemarkImpl.this.model;
//                        KMLLocation location = (KMLLocation) model.getField("Location");
//
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                        altitude = location.getAltitude();
//
//                        ((ColladaRoot) linkedFile).setPosition(latitude, longitude, altitude);
//                        ((ColladaRoot) linkedFile).parseShape();
//
//                        linkedObject = (KMLRenderable) linkedFile;  // do this after all the preparsing is done
//                    }
//                });
//            }
//        }
//
//        if (needToFinishInit
//            && linkedObject != null)  // if the worker thread is done we have stuff to do in the prerender thread.
//        {
//            needToFinishInit = false;
//            //get the file url, then make a factory just like we do for kml- spin off a thread?
//
//            KMLModel model = this.model;
//            KMLLocation location = (KMLLocation) model.getField("Location");
//
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            altitude = location.getAltitude();
//
//            ((ColladaRoot) linkedObject).setPosition(latitude, longitude, altitude);
//        }
//    }
}