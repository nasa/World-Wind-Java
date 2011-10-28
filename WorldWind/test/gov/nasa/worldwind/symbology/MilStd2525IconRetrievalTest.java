/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525IconRetriever;
import junit.framework.*;
import junit.textui.TestRunner;

import java.awt.image.*;

public class MilStd2525IconRetrievalTest
{
    // TODO: test all possible values for Standard Identity and Status
    // TODO: test unframed icons

    // This path should correspond to the location of the appropriate symbology source icons on your system
    private final static String PATH
        = "file:///C:/WorldWind/release/trunk/WorldWind/src/gov/nasa/worldwind/symbology/milstd2525/icons";
    private final static String URL
        = "http://worldwindserver.net/milstd2525/";

    public static class RetrievalTests extends TestCase
    {
        //////////////////////////////////////////////////////////
        // Test retrieval of a MilStd2525 icon from both a remote
        // server and the local file system.
        //////////////////////////////////////////////////////////

        @org.junit.Test
        public void testServerRetrieval()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVListImpl params = new AVListImpl();
            BufferedImage img = symGen.createIcon("SUAPC----------", params);
            assertNotNull(img);
        }

        @org.junit.Test
        public void testFileRetrieval()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(PATH);
            BufferedImage img = symGen.createIcon("SUAPC----------");
            assertNotNull(img);
        }
    }

    public static class ParsingTests extends TestCase
    {
        //////////////////////////////////////////////////////////
        // Test parsing of the Symbol Code.
        // MilStd2525 SymCodes should be exactly 15 characters.
        //////////////////////////////////////////////////////////

        @org.junit.Test
        public void testParseCodeTooShort()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("SUAPC");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testParseCodeTooLong()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("SUAPCTEST");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testParseNullCode()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon(null);
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }
    }

    public static class CodeTests extends TestCase
    {
        //////////////////////////////////////////////////////////
        // Test validity of Symbol Code.
        // Codes containing invalid letters should retrieve a null image.
        // TODO: is this correct?
        //////////////////////////////////////////////////////////

        @org.junit.Test
        public void testInvalidCodingScheme()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon(".UAPC----------");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testInvalidStandardIdentity()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("S.APC----------");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testInvalidBattleDimension()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("SU.PC----------");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testInvalidStatus()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("SUA.C----------");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        /*
        @org.junit.Test
        public void testInvalidFunctionID()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            BufferedImage img = symGen.createIcon("SUAPZ----------");
            assertNull(img);
        }

        @org.junit.Test
        public void testInvalidModifierCode()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            BufferedImage img = symGen.createIcon("SUAPCZZ--------");
            assertNull(img);
        }

        @org.junit.Test
        public void testInvalidModifierCode()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            BufferedImage img = symGen.createIcon("SUAPC-------ZZ-");
            assertNull(img);
        }
        */

        @org.junit.Test
        public void testInvalidOrderOfBattle()
        {
            try
            {
                MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
                BufferedImage img = symGen.createIcon("SUAPC---------.");
                fail("Should raise an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }
    }

    public static class FunctionIDTests extends TestCase
    {
        //////////////////////////////////////////////////////////
        // Test for the presence and retrieval of a every possible base icon by
        // iterating through all combinations of Standard Identity and FunctionID.
        // Battle Dimensions AIR and GROUND only are tested here.
        //////////////////////////////////////////////////////////

        @org.junit.Test
        public void testAirFunctionIDRetrieval()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVList params = new AVListImpl();
            BufferedImage img = null;

            for (String s : AirFunctionIDs)
            {
                img = symGen.createIcon("SUAP" + s + "-----", params);
                assertNotNull("Icon " + "0.suap" + s.toLowerCase() + "-----.png not found.", img);

                img = symGen.createIcon("SFAP" + s + "-----", params);
                assertNotNull("Icon " + "1.sfap" + s.toLowerCase() + "-----.png not found.", img);

                img = symGen.createIcon("SNAP" + s + "-----", params);
                assertNotNull("Icon " + "2.snap" + s.toLowerCase() + "-----.png not found.", img);

                img = symGen.createIcon("SHAP" + s + "-----", params);
                assertNotNull("Icon " + "3.shap" + s.toLowerCase() + "-----.png not found.", img);
            }
        }

        @org.junit.Test
        public void testGroundFunctionIDRetrieval()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVList params = new AVListImpl();
            BufferedImage img = null;

            for (String s : GroundFunctionIDs)
            {
                String padding = "-----";
                if (s.substring(0, 1).equalsIgnoreCase("I"))    // handle special case of installations
                    padding = "H----";

                img = symGen.createIcon("SUGP" + s + padding, params);
                assertNotNull("Icon " + "0.sugp" + s.toLowerCase() + padding + ".png not found.", img);

                img = symGen.createIcon("SFGP" + s + padding, params);
                assertNotNull("Icon " + "1.sfgp" + s.toLowerCase() + padding + ".png not found.", img);

                img = symGen.createIcon("SNGP" + s + padding, params);
                assertNotNull("Icon " + "2.sngp" + s.toLowerCase() + padding + ".png not found.", img);

                img = symGen.createIcon("SHGP" + s + padding, params);
                assertNotNull("Icon " + "3.shgp" + s.toLowerCase() + padding + ".png not found.", img);
            }
        }

        @org.junit.Test
        public void testIconOverlayRetrieval()
        {
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVList params = new AVListImpl();
            BufferedImage img = null;

            for (String s : IconOverlays)
            {
                img = symGen.retrieveImageFromURL(s, img);
                assertNotNull("Icon overlay " + s + " not found.", img);
            }
        }
    }

    public static void main(String[] args)
    {
        TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(RetrievalTests.class);
        testSuite.addTestSuite(ParsingTests.class);
        testSuite.addTestSuite(CodeTests.class);
        testSuite.addTestSuite(FunctionIDTests.class);
        new TestRunner().doRun(testSuite);
    }

    private final static String[] IconOverlays = {"arch_overlay.png",
        "circle_overlay.png",
        "clover_overlay.png",
        "clovertop_overlay.png",
        "diamond_overlay.png",
        "rectangle_overlay.png",
        "tent_overlay.png",
        "installation_clover_overlay.png",
        "installation_diamond_overlay.png",
        "installation_rectangle_overlay.png"};

    private final static String[] AirFunctionIDs = {"------",
        "C-----",
        "M-----",
        "MF----",
        "MFB---",
        "MFF---", "MFFI--",
        "MFT---",
        "MFA---",
        "MFL---",
        "MFK---", "MFKB--", "MFKD--",
        "MFC---", "MFCL--", "MFCM--", "MFCH--",
        "MFJ---",
        "MFO---",
        "MFR---", "MFRW--", "MFRZ--", "MFRX--",
        "MFP---", "MFPN--", "MFPM--",
        "MFU---", "MFUL--", "MFUM--", "MFUH--",
        "MFY---",
        "MFH---",
        "MFD---",
        "MFQ---",
        "MFQA--",
        "MFQB--",
        "MFQC--",
        "MFQD--",
        "MFQF--",
        "MFQH--",
        "MFQJ--",
        "MFQK--",
        "MFQL--",
        "MFQM--",
        "MFQI--",
        "MFQN--",
        "MFQP--",
        "MFQR--",
        "MFQRW-", "MFQRZ-", "MFQRX-",
        "MFQS--",
        "MFQT--",
        "MFQU--",
        "MFQY--",
        "MFQO--",
        "MFS---",
        "MFM---",
        "MH----",
        "MHA---",
        "MHS---",
        "MHU---", "MHUL--", "MHUM--", "MHUH--",
        "MHI---",
        "MHH---",
        "MHR---",
        "MHQ---",
        "MHC---",
        "MHCL--",
        "MHCM--",
        "MHCH--",
        "MHT---",
        "MHO---",
        "MHM---",
        "MHD---",
        "MHK---",
        "MHJ---",
        "ML----",
        "MV----",
        "ME----",
        "W-----",
        "WM----",
        "WMS---",
        "WMSS--",
        "WMSA--",
        "WMSU--",
        "WMSB--",
        "WMA---",
        "WMAS--",
        "WMAA--",
        "WMAP--",
        "WMU---",
        "WMCM--",
        "WMB---",
        "WB----",
        "WD----",
        "C-----",
        "CF----",
        "CH----",
        "CL----"};

    private final static String[] GroundFunctionIDs = {"------",
        "U-----",
        "UC----",
        "UCD---",
        "UCDS--",
        "UCDSC-",
        "UCDSS-",
        "UCDSV-",
        "UCDM--",
        "UCDML-",
        "UCDMLA",
        "UCDMM-",
        "UCDMH-",
        "UCDH--",

        "UCDHH-",
        "UCDHP-",
        "UCDG--",
        "UCDC--",
        "UCDT--",
        "UCDO--",
        "UCA---",
        "UCAT--",
        "UCATA-",
        "UCATW-",
        "UCATWR",
        "UCATL-",
        "UCATM-",
        "UCATH-",
        "UCATR-",
        "UCAW--",
        "UCAWS-",
        "UCAWA-",
        "UCAWW-",
        "UCAWWR",
        "UCAWL-",
        "UCAWM-",
        "UCAWH-",
        "UCAWR-",
        "UCAA--",

        "UCAAD-",
        "UCAAL-",
        "UCAAM-",
        "UCAAS-",
        "UCAAU-",
        "UCAAC-",
        "UCAAA-",
        "UCAAAT",
        "UCAAAW",
        "UCAAAS",
        "UCAAO-",
        "UCAAOS",
        "UCV---",
        "UCVF--",
        "UCVFU-",
        "UCVFA-",
        "UCVFR-",
        "UCVR--",
        "UCVRA-",
        "UCVRS-",
        "UCVRW-",
        "UCVRU-",
        "UCVRUL",
        "UCVRUM",
        "UCVRUH",

        "UCVRUC",
        "UCVRUE",
        "UCVRM-",
        "UCVS--",
        "UCVC--",
        "UCVV--",
        "UCVU--",
        "UCVUF-",
        "UCVUR-",
        "UCI---",
        "UCIL--",
        "UCIM--",
        "UCIO--",
        "UCIA--",
        "UCIS--",
        "UCIZ--",
        "UCIN--",
        "UCII--",
        "UCIC--",
        "UCE---",
        "UCEC--",
        "UCECS-",
        "UCECA-",
        "UCECC-",

        "UCECL-",
        "UCECM-",
        "UCECH-",
        "UCECT-",
        "UCECW-",
        "UCECO-",
        "UCECR-",
        "UCEN--",
        "UCENN-",
        "UCF---",
        "UCFH--",
        "UCFHE-",
        "UCFHS-",
        "UCFHA-",
        "UCFHC-",
        "UCFHO-",
        "UCFHL-",
        "UCFHM-",
        "UCFHH-",
        "UCFHX-",
        "UCFR--",
        "UCFRS-",
        "UCFRSS",
        "UCFRSR",
        "UCFRST",

        "UCFRM-",
        "UCFRMS",
        "UCFRMR",
        "UCFRMT",
        "UCFT--",
        "UCFTR-",
        "UCFTS-",
        "UCFTF-",
        "UCFTC-",
        "UCFTCD",
        "UCFTCM",
        "UCFTA-",
        "UCFM--",
        "UCFMS-",
        "UCFMW-",
        "UCFMT-",
        "UCFMTA",
        "UCFMTS",
        "UCFMTC",
        "UCFMTO",
        "UCFML-",
        "UCFS--",
        "UCFSS-",
        "UCFSA-",
        "UCFSL-",

        "UCFSO-",
        "UCFO--",
        "UCFOS-",
        "UCFOA-",
        "UCFOL-",
        "UCFOO-",
        "UCR---",
        "UCRH--",
        "UCRV--",
        "UCRVA-",
        "UCRVM-",
        "UCRVG-",
        "UCRVO-",
        "UCRC--",
        "UCRS--",
        "UCRA--",
        "UCRO--",
        "UCRL--",
        "UCRR--",
        "UCRRD-",
        "UCRRF-",
        "UCRRL-",
        "UCRX--",
        "UCM---",

        "UCMT--",
        "UCMS--",
        "UCS---",
        "UCSW--",
        "UCSG--",
        "UCSGD-",
        "UCSGM-",
        "UCSGA-",
        "UCSM--",
        "UCSR--",
        "UCSA--",
        "UU----",
        "UUA---",
        "UUAC--",
        "UUACC-",
        "UUACCK",
        "UUACCM",
        "UUACS-",
        "UUACSM",
        "UUACSA",
        "UUACR-",
        "UUACRW",
        "UUACRS",
        "UUAN--",

        "UUAB--",
        "UUABR-",
        "UUAD--",
        "UUM---",
        "UUMA--",
        "UUMS--",
        "UUMSE-",
        "UUMSEA",
        "UUMSED",
        "UUMSEI",
        "UUMSEJ",
        "UUMSET",
        "UUMSEC",
        "UUMC--",
        "UUMR--",
        "UUMRG-",
        "UUMRS-",
        "UUMRSS",
        "UUMRX-",
        "UUMMO-",
        "UUMO--",
        "UUMT--",
        "UUMQ--",
        "UUMJ--",
        "UUL---",

        "UULS--",
        "UULM--",
        "UULC--",
        "UULF--",
        "UULD--",
        "UUS---",
        "UUSA--",
        "UUSC--",
        "UUSCL-",
        "UUSO--",
        "UUSF--",
        "UUSM--",
        "UUSMS-",
        "UUSML-",
        "UUSMN-",
        "UUSR--",
        "UUSRS-",
        "UUSRT-",
        "UUSRW-",
        "UUSS--",
        "UUSW--",
        "UUSX--",
        "UUI---",
        "UUP---",
        "UUE---",

        "US----",
        "USA---",
        "USAT--",
        "USAC--",
        "USAJ--",
        "USAJT-",
        "USAJC-",
        "USAO--",
        "USAOT-",
        "USAOC-",
        "USAF--",
        "USAFT-",
        "USAFC-",
        "USAS--",
        "USAST-",
        "USASC-",
        "USAM--",
        "USAMT-",
        "USAMC-",
        "USAR--",
        "USART-",
        "USARC-",
        "USAP--",
        "USAPT-",
        "USAPC-",

        "USAPB-",
        "USAPBT",
        "USAPBC",
        "USAPM-",
        "USAPMT",
        "USAPMC",
        "USAX--",
        "USAXT-",
        "USAXC-",
        "USAL--",
        "USALT-",
        "USALC-",
        "USAW--",
        "USAWT-",
        "USAWC-",
        "USAQ--",
        "USAQT-",
        "USAQC-",
        "USM---",
        "USMT--",
        "USMC--",
        "USMM--",
        "USMMT-",
        "USMMC-",
        "USMV--",

        "USMVT-",
        "USMVC-",
        "USMD--",
        "USMDT-",
        "USMDC-",
        "USMP--",
        "USMPT-",
        "USMPC-",
        "USS---",
        "USST--",
        "USSC--",
        "USS1--",
        "USS1T-",
        "USS1C-",
        "USS2--",
        "USS2T-",
        "USS2C-",
        "USS3--",
        "USS3T-",
        "USS3C-",
        "USS3A-",
        "USS3AT",
        "USS3AC",
        "USS4--",
        "USS4T-",

        "USS4C-",
        "USS5--",
        "USS5T-",
        "USS5C-",
        "USS6--",
        "USS6T-",
        "USS6C-",
        "USS7--",
        "USS7T-",
        "USS7C-",
        "USS8--",
        "USS8T-",
        "USS8C-",
        "USS9--",
        "USS9T-",
        "USS9C-",
        "USSX--",
        "USSXT-",
        "USSXC-",
        "USSL--",
        "USSLT-",
        "USSLC-",
        "USSW--",
        "USSWT-",
        "USSWC-",

        "USSWP-",
        "USSWPT",
        "USSWPC",
        "UST---",
        "USTT--",
        "USTC--",
        "USTM--",
        "USTMT-",
        "USTMC-",
        "USTR--",
        "USTRT-",
        "USTRC-",
        "USTS--",
        "USTST-",
        "USTSC-",
        "USTA--",
        "USTAT-",
        "USTAC-",
        "USTI--",
        "USTIT-",
        "USTIC-",
        "USX---",
        "USXT--",
        "USXC--",
        "USXH--",

        "USXHT-",
        "USXHC-",
        "USXR--",
        "USXRT-",
        "USXRC-",
        "USXO--",
        "USXOT-",
        "USXOC-",
        "USXOM-",
        "USXOMT",
        "USXOMC",
        "USXE--",
        "USXET-",
        "USXEC-",
        "UH----",
        "E-----",
        //"EW----",         // icon not used
        "EWM---",
        "EWMA--",
        "EWMAS-",
        "EWMASR",
        "EWMAI-",
        "EWMAIR",
        "EWMAIE",

        "EWMAL-",
        "EWMALR",
        "EWMALE",
        "EWMAT-",
        "EWMATR",
        "EWMATE",
        "EWMS--",
        "EWMSS-",
        "EWMSI-",
        "EWMSL-",
        "EWMT--",
        "EWMTL-",
        "EWMTM-",
        "EWMTH-",
        "EWS---",
        "EWSL--",
        "EWSM--",
        "EWSH--",
        "EWX---",
        "EWXL--",
        "EWXM--",
        "EWXH--",
        "EWT---",
        "EWTL--",
        "EWTM--",

        "EWTH--",
        "EWR---",
        "EWRR--",
        "EWRL--",
        "EWRH--",
        "EWZ---",
        "EWZL--",
        "EWZM--",
        "EWZH--",
        "EWO---",
        "EWOL--",
        "EWOM--",
        "EWOH--",
        "EWH---",
        "EWHL--",
        "EWHLS-",
        "EWHM--",
        "EWHMS-",
        "EWHH--",
        "EWHHS-",
        "EWG---",
        "EWGL--",
        "EWGM--",
        "EWGH--",
        "EWGR--",

        "EWD---",
        "EWDL--",
        "EWDLS-",
        "EWDM--",
        "EWDMS-",
        "EWDH--",
        "EWDHS-",
        "EWA---",
        "EWAL--",
        "EWAM--",
        "EWAH--",
        "EV----",
        "EVA---",
        "EVAT--",
        "EVATL-",
        "EVATLR",
        "EVATM-",
        "EVATMR",
        "EVATH-",
        "EVATHR",
        "EVAA--",
        "EVAAR-",
        "EVAI--",
        "EVAC--",
        "EVAS--",

        "EVAL--",
        "EVU---",
        "EVUB--",
        "EVUS--",
        "EVUSL-",
        "EVUSM-",
        "EVUSH-",
        "EVUL--",
        "EVUX--",
        "EVUR--",
        "EVUT--",
        "EVUTL-",
        "EVUTH-",
        "EVUA--",
        "EVUAA-",
        "EVE---",
        "EVEB--",
        "EVEE--",
        "EVEC--",
        "EVEM--",
        "EVEMV-",
        "EVEML-",
        "EVEA--",
        "EVEAA-",
        "EVEAT-",

        "EVED--",
        "EVEDA-",
        "EVES--",
        "EVER--",
        "EVEH--",
        "EVEF--",
        "EVT---",
        "EVC---",
        "EVCA--",
        "EVCAL-",
        "EVCAM-",
        "EVCAH-",
        "EVCO--",
        "EVCOL-",
        "EVCOM-",
        "EVCOH-",
        "EVCM--",
        "EVCML-",
        "EVCMM-",
        "EVCMH-",
        "EVCU--",
        "EVCUL-",
        "EVCUM-",
        "EVCUH-",
        "EVCJ--",

        "EVCJL-",
        "EVCJM-",
        "EVCJH-",
        "EVCT--",
        "EVCTL-",
        "EVCTM-",
        "EVCTH-",
        "EVCF--",
        "EVCFL-",
        "EVCFM-",
        "EVCFH-",
        "EVM---",
        "EVS---",
        "EVST--",
        "EVSR--",
        "EVSC--",
        "EVSP--",
        "EVSW--",
        "ES----",
        "ESR---",
        "ESE---",
        //"EX----",         // icon not used
        "EXI---",
        "EXL---",
        "EXN---",

        "EXF---",
        "EXM---",
        "EXMC--",
        "EXML--",
        "I-----",
        "IR----",
        "IRM---",
        "IRP---",
        "IRN---",
        "IRNB--",
        "IRNC--",
        "IRNN--",
        "IP----",
        "IPD---",
        "IE----",
        "IU----",
        "IUR---",
        "IUT---",
        "IUE---",
        "IUEN--",
        "IUED--",
        "IUEF--",
        "IUP---",
        //"IM----",         // icon not used
        "IMF---",

        "IMFA--",
        "IMFP--",
        "IMFPW-",
        "IMFS--",
        "IMA---",
        "IME---",
        "IMG---",
        "IMV---",
        "IMN---",
        "IMNB--",
        "IMC---",
        "IMS---",
        "IMM---",
        "IG----",
        "IB----",
        "IBA---",
        "IBN---",
        "IT----",
        "IX----",
        "IXH---"};
}

