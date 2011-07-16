/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.view;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class FlatOrbitViewTest extends junit.framework.TestCase
{
    /*************************************************************************************************************/
    /** Persistence Tests **/
    /*************************************************************************************************************/
    
    @org.junit.Test
    public void testRestore_NewInstance()
    {
        FlatOrbitView orbitView = new FlatOrbitView();
        assignExampleValues(orbitView);

        String stateInXml = orbitView.getRestorableState();
        orbitView = new FlatOrbitView();
        orbitView.restoreState(stateInXml);

        FlatOrbitView expected = new FlatOrbitView();
        assignExampleValues(expected);
        
        assertEquals(expected, orbitView);
    }

    @org.junit.Test
    public void testRestore_SameInstance()
    {
        FlatOrbitView orbitView = new FlatOrbitView();
        assignExampleValues(orbitView);

        String stateInXml = orbitView.getRestorableState();
        assignNullValues(orbitView);
        orbitView.restoreState(stateInXml);

        FlatOrbitView expected = new FlatOrbitView();
        assignExampleValues(expected);

        assertEquals(expected, orbitView);
    }

    @org.junit.Test
    public void testRestore_EmptyStateDocument()
    {
        FlatOrbitView orbitView = new FlatOrbitView();
        assignExampleValues(orbitView);

        String emptyStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<emptyDocumentRoot/>";
        orbitView.restoreState(emptyStateInXml);

        // No attributes should have changed.
        FlatOrbitView expected = new FlatOrbitView();
        assignExampleValues(expected);

        assertEquals(expected, orbitView);
    }

    @org.junit.Test
    public void testRestore_InvalidStateDocument()
    {
        try
        {
            String badStateInXml = "!!invalid xml string!!";
            FlatOrbitView orbitView = new FlatOrbitView();
            orbitView.restoreState(badStateInXml);

            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    @org.junit.Test
    public void testRestore_PartialStateDocument()
    {
        FlatOrbitView orbitView = new FlatOrbitView();
        assignNullValues(orbitView);

        String partialStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<restorableState>" +
                "<stateObject name=\"fieldOfView\">10.5</stateObject>" +
                "<stateObject name=\"zoom\">1000.5</stateObject>" +
                "<unknownElement name=\"unknownName\">unknownValue</unknownElement>" +
            "</restorableState>";
        orbitView.restoreState(partialStateInXml);

        FlatOrbitView expected = new FlatOrbitView();
        assignNullValues(expected);
        expected.setFieldOfView(Angle.fromDegrees(10.5));
        expected.setZoom(1000.5);

        assertEquals(expected, orbitView);
    }

    @org.junit.Test
    public void testRestore_SaveOrbitView_RestoreFlatOrbitView()
    {
        BasicOrbitView basicOrbitView = new BasicOrbitView();
        assignExampleValues(basicOrbitView);

        String stateInXml = basicOrbitView.getRestorableState();
        FlatOrbitView flatOrbitView = new FlatOrbitView();
        assignNullValues(flatOrbitView);
        flatOrbitView.restoreState(stateInXml);

        //noinspection RedundantCast
        assertEquals((OrbitView) flatOrbitView, (OrbitView) basicOrbitView);
    }

    @org.junit.Test
    public void testRestore_OldVersionStateDocument()
    {
        BasicOrbitView orbitView = new BasicOrbitView();
        String stateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<restorableState>" +
                "<stateObject name=\"center\">" +
                    "<stateObject name=\"latitude\">-20.5</stateObject>" +
                    "<stateObject name=\"longitude\">30.5</stateObject>" +
                    "<stateObject name=\"elevation\">100.5</stateObject>" +
                "</stateObject>" +
                "<stateObject name=\"heading\">-40.5</stateObject>" +
                "<stateObject name=\"pitch\">50.5</stateObject>" +
                "<stateObject name=\"zoom\">1000.5</stateObject>" +
                "<stateObject name=\"fieldOfView\">10.5</stateObject>" +
            "</restorableState>";
        orbitView.restoreState(stateInXml);

        BasicOrbitView expected = new BasicOrbitView();
        expected.setCenterPosition(Position.fromDegrees(-20.5, 30.5, 100.5));
        expected.setHeading(Angle.fromDegrees(-40.5));
        expected.setPitch(Angle.fromDegrees(50.5));
        expected.setZoom(1000.5);
        expected.setFieldOfView(Angle.fromDegrees(10.5));

        assertEquals(expected, orbitView);
    }
    
    /*************************************************************************************************************/
    /** Helper Methods **/
    /*************************************************************************************************************/

    @SuppressWarnings({"JavaDoc"})
    private static void assignExampleValues(OrbitView orbitView)
    {
        orbitView.setCenterPosition(Position.fromDegrees(-20.5, 30.5, 100.5));
        orbitView.setHeading(Angle.fromDegrees(-40.5));
        orbitView.setPitch(Angle.fromDegrees(50.5));
        orbitView.setZoom(1000.5);
        orbitView.setFieldOfView(Angle.fromDegrees(10.5));

        orbitView.setDetectCollisions(false);
        //orbitView.setNearClipDistance(100.5);
        //orbitView.setFarClipDistance(10000.5);
    }

    private static void assignNullValues(OrbitView orbitView)
    {
        orbitView.setCenterPosition(Position.fromDegrees(0.0, 0.0, 0.0));
        orbitView.setHeading(Angle.fromDegrees(0.0));
        orbitView.setPitch(Angle.fromDegrees(0.0));
        orbitView.setZoom(0.0);
        orbitView.setFieldOfView(Angle.fromDegrees(0.0));

        orbitView.setDetectCollisions(true);
        //orbitView.setNearClipDistance(-1.0);
        //orbitView.setFarClipDistance(-1.0);
    }

    private static void assertEquals(OrbitView expected, OrbitView actual)
    {
        assertNotNull("Expected is null", expected);
        assertNotNull("Actual is null", actual);
        assertEquals("center.latitude", expected.getCenterPosition().getLatitude(), actual.getCenterPosition().getLatitude());
        assertEquals("center.longitude", expected.getCenterPosition().getLongitude(), actual.getCenterPosition().getLongitude());
        assertEquals("center.elevation", expected.getCenterPosition().getElevation(), actual.getCenterPosition().getElevation());
        assertEquals("heading", expected.getHeading(), actual.getHeading());
        assertEquals("pitch", expected.getPitch(), actual.getPitch());
        assertEquals("zoom", expected.getZoom(), actual.getZoom());
        assertEquals("fieldOfView", expected.getFieldOfView(), actual.getFieldOfView());

        assertEquals("detectCollisions", expected.isDetectCollisions(), actual.isDetectCollisions());
        assertEquals("nearClipDistance", expected.getNearClipDistance(), actual.getNearClipDistance());
        assertEquals("farClipDistance", expected.getFarClipDistance(), actual.getFarClipDistance());
    }

    public static void main(String[] args)
    {
        new junit.textui.TestRunner().doRun(new junit.framework.TestSuite(FlatOrbitViewTest.class));
    }
}
