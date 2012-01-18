/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics;

/**
 * SIDC constants for graphics in the "Tactical Graphics" scheme (MIL-STD-2525C Appendix B). The constants in this
 * interface are "masked" SIDCs. All fields except Scheme, Category, Function ID, and Order of Battle fields are filled
 * with hyphens. (The other fields do not identity a type of graphic, they modify the graphic.)
 *
 * @author pabercrombie
 * @version $Id$
 */
public interface TacGrpSidc
{
    ///////////////////////////////
    // Tasks
    ///////////////////////////////

    /** Block */
    final String TSK_BLK = "G-T-B---------X";
    /** Breach */
    final String TSK_BRH = "G-T-H---------X";
    /** Bypass */
    final String TSK_BYS = "G-T-Y---------X";
    /** Canalize */
    final String TSK_CNZ = "G-T-C---------X";
    /** Clear */
    final String TSK_CLR = "G-T-X---------X";
    /** Contain */
    final String TSK_CNT = "G-T-J---------X";
    /** Counterattack (CATK) */
    final String TSK_CATK = "G-T-K---------X";
    /** Counterattack By Fire */
    final String TSK_CATK_CATKF = "G-T-KF--------X";
    /** Delay */
    final String TSK_DLY = "G-T-L---------X";
    /** Destroy */
    final String TSK_DSTY = "G-T-D---------X";
    /** Disrupt */
    final String TSK_DRT = "G-T-T---------X";
    /** Fix */
    final String TSK_FIX = "G-T-F---------X";
    /** Follow And Assume */
    final String TSK_FLWASS = "G-T-A---------X";
    /** Follow And Support */
    final String TSK_FLWASS_FLWSUP = "G-T-AS--------X";
    /** Interdict */
    final String TSK_ITDT = "G-T-I---------X";
    /** Isolate */
    final String TSK_ISL = "G-T-E---------X";
    /** Neutralize */
    final String TSK_NEUT = "G-T-N---------X";
    /** Occupy */
    final String TSK_OCC = "G-T-O---------X";
    /** Penetrate */
    final String TSK_PNE = "G-T-P---------X";
    /** Relief In Place (RIP) */
    final String TSK_RIP = "G-T-R---------X";
    /** Retain */
    final String TSK_RTN = "G-T-Q---------X";
    /** Retirement */
    final String TSK_RTM = "G-T-M---------X";
    /** Secure */
    final String TSK_SCE = "G-T-S---------X";
    /** Screen */
    final String TSK_SEC_SCN = "G-T-US--------X";
    /** Guard */
    final String TSK_SEC_GUD = "G-T-UG--------X";
    /** Cover */
    final String TSK_SEC_COV = "G-T-UC--------X";
    /** Seize */
    final String TSK_SZE = "G-T-Z---------X";
    /** Withdraw */
    final String TSK_WDR = "G-T-W---------X";
    /** Withdraw Under Pressure */
    final String TSK_WDR_WDRUP = "G-T-WP--------X";

    ///////////////////////////////////////////
    // Command, Control, and General Manuever
    ///////////////////////////////////////////

    /** Datum */
    final String C2GM_GNL_PNT_USW_UH2_DTM = "G-G-GPUUD-----X";
    /** Brief Contact */
    final String C2GM_GNL_PNT_USW_UH2_BCON = "G-G-GPUUB-----X";
    /** Lost Contact */
    final String C2GM_GNL_PNT_USW_UH2_LCON = "G-G-GPUUL-----X";
    /** Sinker */
    final String C2GM_GNL_PNT_USW_UH2_SNK = "G-G-GPUUS-----X";
    /** Sonobuoy */
    final String C2GM_GNL_PNT_USW_SNBY = "G-G-GPUY------X";
    /** Pattern Center */
    final String C2GM_GNL_PNT_USW_SNBY_PTNCTR = "G-G-GPUYP-----X";
    /** Directional Frequency Analyzing And Recording (DIFAR) */
    final String C2GM_GNL_PNT_USW_SNBY_DIFAR = "G-G-GPUYD-----X";
    /** Low Frequency Analyzing And Recording (LOFAR) */
    final String C2GM_GNL_PNT_USW_SNBY_LOFAR = "G-G-GPUYL-----X";
    /** Command Active Sonobuoy System (CASS) */
    final String C2GM_GNL_PNT_USW_SNBY_CASS = "G-G-GPUYC-----X";
    /** Directional Command Active Sonobuoy System (DICASS) */
    final String C2GM_GNL_PNT_USW_SNBY_DICASS = "G-G-GPUYS-----X";
    /** Bathythermograph Transmitting (BT) */
    final String C2GM_GNL_PNT_USW_SNBY_BT = "G-G-GPUYB-----X";
    /** ANM */
    final String C2GM_GNL_PNT_USW_SNBY_ANM = "G-G-GPUYA-----X";
    /** Vertical Line Array Difar (VLAD) */
    final String C2GM_GNL_PNT_USW_SNBY_VLAD = "G-G-GPUYV-----X";
    /** ATAC */
    final String C2GM_GNL_PNT_USW_SNBY_ATAC = "G-G-GPUYT-----X";
    /** Range Only (RO) */
    final String C2GM_GNL_PNT_USW_SNBY_RO = "G-G-GPUYR-----X";
    /** Kingpin */
    final String C2GM_GNL_PNT_USW_SNBY_KGP = "G-G-GPUYK-----X";
    /** Sonobuoy-Expired */
    final String C2GM_GNL_PNT_USW_SNBY_EXP = "G-G-GPUYX-----X";
    /** Search */
    final String C2GM_GNL_PNT_USW_SRH = "G-G-GPUS------X";
    /** Search Area */
    final String C2GM_GNL_PNT_USW_SRH_ARA = "G-G-GPUSA-----X";
    /** DIP Position */
    final String C2GM_GNL_PNT_USW_SRH_DIPPSN = "G-G-GPUSD-----X";
    /** Search Center */
    final String C2GM_GNL_PNT_USW_SRH_CTR = "G-G-GPUSC-----X";
    /** Reference Point */
    final String C2GM_GNL_PNT_REFPNT = "G-G-GPR-------X";
    /** Navigational Reference Point */
    final String C2GM_GNL_PNT_REFPNT_NAVREF = "G-G-GPRN------X";
    /** Special Point */
    final String C2GM_GNL_PNT_REFPNT_SPLPNT = "G-G-GPRS------X";
    /** DLRP */
    final String C2GM_GNL_PNT_REFPNT_DLRP = "G-G-GPRD------X";
    /** Point Of Intended Movement (PIM) */
    final String C2GM_GNL_PNT_REFPNT_PIM = "G-G-GPRP------X";
    /** Marshall Point */
    final String C2GM_GNL_PNT_REFPNT_MRSH = "G-G-GPRM------X";
    /** Waypoint */
    final String C2GM_GNL_PNT_REFPNT_WAP = "G-G-GPRW------X";
    /** Corridor Tab */
    final String C2GM_GNL_PNT_REFPNT_CRDRTB = "G-G-GPRC------X";
    /** Point Of Interest */
    final String C2GM_GNL_PNT_REFPNT_PNTINR = "G-G-GPRI------X";
    /** Weapon */
    final String C2GM_GNL_PNT_WPN = "G-G-GPW-------X";
    /** Aim Point */
    final String C2GM_GNL_PNT_WPN_AIMPNT = "G-G-GPWA------X";
    /** Drop Point */
    final String C2GM_GNL_PNT_WPN_DRPPNT = "G-G-GPWD------X";
    /** Entry Point */
    final String C2GM_GNL_PNT_WPN_ENTPNT = "G-G-GPWE------X";
    /** Ground Zero */
    final String C2GM_GNL_PNT_WPN_GRDZRO = "G-G-GPWG------X";
    /** MSL Detect Point */
    final String C2GM_GNL_PNT_WPN_MSLPNT = "G-G-GPWM------X";
    /** Impact Point */
    final String C2GM_GNL_PNT_WPN_IMTPNT = "G-G-GPWI------X";
    /** Predicted Impact Point */
    final String C2GM_GNL_PNT_WPN_PIPNT = "G-G-GPWP------X";
    /** Formation */
    final String C2GM_GNL_PNT_FRMN = "G-G-GPF-------X";
    /** Harbor (General) */
    final String C2GM_GNL_PNT_HBR = "G-G-GPH-------X";
    /** Point Q */
    final String C2GM_GNL_PNT_HBR_PNTQ = "G-G-GPHQ------X";
    /** Point A */
    final String C2GM_GNL_PNT_HBR_PNTA = "G-G-GPHA------X";
    /** Point Y */
    final String C2GM_GNL_PNT_HBR_PNTY = "G-G-GPHY------X";
    /** Point X */
    final String C2GM_GNL_PNT_HBR_PNTX = "G-G-GPHX------X";
    /** Route */
    final String C2GM_GNL_PNT_RTE = "G-G-GPO-------X";
    /** Rendezvous */
    final String C2GM_GNL_PNT_RTE_RDV = "G-G-GPOZ------X";
    /** Diversions */
    final String C2GM_GNL_PNT_RTE_DVSN = "G-G-GPOD------X";
    /** Waypoint */
    final String C2GM_GNL_PNT_RTE_WAP = "G-G-GPOW------X";
    /** PIM */
    final String C2GM_GNL_PNT_RTE_PIM = "G-G-GPOP------X";
    /** Point R */
    final String C2GM_GNL_PNT_RTE_PNTR = "G-G-GPOR------X";
    /** Air Control */
    final String C2GM_GNL_PNT_ACTL = "G-G-GPA-------X";
    /** Combat Air Patrol (CAP) */
    final String C2GM_GNL_PNT_ACTL_CAP = "G-G-GPAP------X";
    /** Airborne Early Warning (AEW) */
    final String C2GM_GNL_PNT_ACTL_ABNEW = "G-G-GPAW------X";
    /** Tanking */
    final String C2GM_GNL_PNT_ACTL_TAK = "G-G-GPAK------X";
    /** Antisubmarine Warfare, Fixed Wing */
    final String C2GM_GNL_PNT_ACTL_ASBWF = "G-G-GPAA------X";
    /** Antisubmarine Warfare, Rotary Wing */
    final String C2GM_GNL_PNT_ACTL_ASBWR = "G-G-GPAH------X";
    /** Sucap - Fixed Wing */
    final String C2GM_GNL_PNT_ACTL_SUWF = "G-G-GPAB------X";
    /** Sucap - Rotary Wing */
    final String C2GM_GNL_PNT_ACTL_SUWR = "G-G-GPAC------X";
    /** IW - Fixed Wing */
    final String C2GM_GNL_PNT_ACTL_MIWF = "G-G-GPAD------X";
    /** MIW - Rotary Wing */
    final String C2GM_GNL_PNT_ACTL_MIWR = "G-G-GPAE------X";
    /** Strike Ip */
    final String C2GM_GNL_PNT_ACTL_SKEIP = "G-G-GPAS------X";
    /** Tacan */
    final String C2GM_GNL_PNT_ACTL_TCN = "G-G-GPAT------X";
    /** Tomcat */
    final String C2GM_GNL_PNT_ACTL_TMC = "G-G-GPAO------X";
    /** Rescue */
    final String C2GM_GNL_PNT_ACTL_RSC = "G-G-GPAR------X";
    /** Replenish */
    final String C2GM_GNL_PNT_ACTL_RPH = "G-G-GPAL------X";
    /** Unmanned Aerial System (UAS/UA) */
    final String C2GM_GNL_PNT_ACTL_UA = "G-G-GPAF------X";
    /** VTUA */
    final String C2GM_GNL_PNT_ACTL_VTUA = "G-G-GPAG------X";
    /** Orbit */
    final String C2GM_GNL_PNT_ACTL_ORB = "G-G-GPAI------X";
    /** Orbit - Figure Eight */
    final String C2GM_GNL_PNT_ACTL_ORBF8 = "G-G-GPAJ------X";
    /** Orbit - Race Track */
    final String C2GM_GNL_PNT_ACTL_ORBRT = "G-G-GPAM------X";
    /** Orbit - Random, Closed */
    final String C2GM_GNL_PNT_ACTL_ORBRD = "G-G-GPAN------X";
    /** Action Points (General) */
    final String C2GM_GNL_PNT_ACTPNT = "G-G-GPP-------X";
    /** Check Point */
    final String C2GM_GNL_PNT_ACTPNT_CHKPNT = "G-G-GPPK------X";
    /** Contact Point */
    final String C2GM_GNL_PNT_ACTPNT_CONPNT = "G-G-GPPC------X";
    /** Coordination Point */
    final String C2GM_GNL_PNT_ACTPNT_CRDPNT = "G-G-GPPO------X";
    /** Decision Point */
    final String C2GM_GNL_PNT_ACTPNT_DCNPNT = "G-G-GPPD------X";
    /** Linkup Point */
    final String C2GM_GNL_PNT_ACTPNT_LNKUPT = "G-G-GPPL------X";
    /** Passage Point */
    final String C2GM_GNL_PNT_ACTPNT_PSSPNT = "G-G-GPPP------X";
    /** Rally Point */
    final String C2GM_GNL_PNT_ACTPNT_RAYPNT = "G-G-GPPR------X";
    /** Release Point */
    final String C2GM_GNL_PNT_ACTPNT_RELPNT = "G-G-GPPE------X";
    /** Start Point */
    final String C2GM_GNL_PNT_ACTPNT_STRPNT = "G-G-GPPS------X";
    /** Amnesty Point */
    final String C2GM_GNL_PNT_ACTPNT_AMNPNT = "G-G-GPPA------X";
    /** Waypoint */
    final String C2GM_GNL_PNT_ACTPNT_WAP = "G-G-GPPW------X";
    /** EA Surface Control Station */
    final String C2GM_GNL_PNT_SCTL = "G-G-GPC-------X";
    /** Unmanned Surface Vehicle (USV) Control Station */
    final String C2GM_GNL_PNT_SCTL_USV = "G-G-GPCU------X";
    /** Remote Multimission Vehicle (RMV) Usv Control Station */
    final String C2GM_GNL_PNT_SCTL_USV_RMV = "G-G-GPCUR-----X";
    /** USV - Antisubmarine Warfare Control Station */
    final String C2GM_GNL_PNT_SCTL_USV_ASW = "G-G-GPCUA-----X";
    /** USV - Surface Warfare Control Station */
    final String C2GM_GNL_PNT_SCTL_USV_SUW = "G-G-GPCUS-----X";
    /** USV - Mine Warfare Control Station */
    final String C2GM_GNL_PNT_SCTL_USV_MIW = "G-G-GPCUM-----X";
    /** ASW Control Station */
    final String C2GM_GNL_PNT_SCTL_ASW = "G-G-GPCA------X";
    /** SUW Control Station */
    final String C2GM_GNL_PNT_SCTL_SUW = "G-G-GPCS------X";
    /** MIW Control Station */
    final String C2GM_GNL_PNT_SCTL_MIW = "G-G-GPCM------X";
    /** Picket Control Station */
    final String C2GM_GNL_PNT_SCTL_PKT = "G-G-GPCP------X";
    /** Rendezvous Control Point */
    final String C2GM_GNL_PNT_SCTL_RDV = "G-G-GPCR------X";
    /** Rescue Control Point */
    final String C2GM_GNL_PNT_SCTL_RSC = "G-G-GPCC------X";
    /** Replenishment Control Point */
    final String C2GM_GNL_PNT_SCTL_REP = "G-G-GPCE------X";
    /** Noncombatant Control Station */
    final String C2GM_GNL_PNT_SCTL_NCBTT = "G-G-GPCN------X";
    /** Subsurface Control Station */
    final String C2GM_GNL_PNT_UCTL = "G-G-GPB-------X";
    /** Unmanned Underwater Vehicle (UUV) Control Station */
    final String C2GM_GNL_PNT_UCTL_UUV = "G-G-GPBU------X";
    /** UUV - Antisubmarine Warfare Control Station */
    final String C2GM_GNL_PNT_UCTL_UUV_ASW = "G-G-GPBUA-----X";
    /** UUV - Surface Warfare Control Station */
    final String C2GM_GNL_PNT_UCTL_UUV_SUW = "G-G-GPBUS-----X";
    /** UUV - Mine Warfare Control Station */
    final String C2GM_GNL_PNT_UCTL_UUV_MIW = "G-G-GPBUM-----X";
    /** Submarine Control Station */
    final String C2GM_GNL_PNT_UCTL_SBSTN = "G-G-GPBS------X";
    /** ASW Submarine Control Station */
    final String C2GM_GNL_PNT_UCTL_SBSTN_ASW = "G-G-GPBSA-----X";
    /** Boundaries */
    final String C2GM_GNL_LNE_BNDS = "G-G-GLB-------X";
    /** Forward, GLC--- ,Line Of Contact */
    final String C2GM_GNL_LNE_FLOT = "G-G-GLF-------X";
    /** Phase Line */
    final String C2GM_GNL_LNE_PHELNE = "G-G-GLP-------X";
    /** Light Line */
    final String C2GM_GNL_LNE_LITLNE = "G-G-GLL-------X";
    /** Areas */
    final String C2GM_GNL_ARS = "G-G-GA--------X";
    /** General Area */
    final String C2GM_GNL_ARS_GENARA = "G-G-GAG-------X";
    /** Assembly Area */
    final String C2GM_GNL_ARS_ABYARA = "G-G-GAA-------X";
    /** Engagement Area */
    final String C2GM_GNL_ARS_EMTARA = "G-G-GAE-------X";
    /** Fortified Area */
    final String C2GM_GNL_ARS_FTFDAR = "G-G-GAF-------X";
    /** Drop Zone */
    final String C2GM_GNL_ARS_DRPZ = "G-G-GAD-------X";
    /** Extraction Zone (EZ) */
    final String C2GM_GNL_ARS_EZ = "G-G-GAX-------X";
    /** Landing Zone (LZ) */
    final String C2GM_GNL_ARS_LZ = "G-G-GAL-------X";
    /** Pickup Zone (PZ) */
    final String C2GM_GNL_ARS_PZ = "G-G-GAP-------X";
    /** Search Area/Reconnaissance Area */
    final String C2GM_GNL_ARS_SRHARA = "G-G-GAS-------X";
    /** Limited Access Area */
    final String C2GM_GNL_ARS_LAARA = "G-G-GAY-------X";
    /** Airfield Zone */
    final String C2GM_GNL_ARS_AIRFZ = "G-G-GAZ-------X";
    /** Air Control Point (ACP) */
    final String C2GM_AVN_PNT_ACP = "G-G-APP-------X";
    /** Communications Checkpoint (CCP) */
    final String C2GM_AVN_PNT_COMMCP = "G-G-APC-------X";
    /** Pull-Up Point (PUP) */
    final String C2GM_AVN_PNT_PUP = "G-G-APU-------X";
    /** Downed Aircrew Pickup Point */
    final String C2GM_AVN_PNT_DAPP = "G-G-APD-------X";
    /** Air Corridor */
    final String C2GM_AVN_LNE_ACDR = "G-G-ALC-------X";
    /** Minimum Risk Route (MRR) */
    final String C2GM_AVN_LNE_MRR = "G-G-ALM-------X";
    /** Standard-Use Army Aircraft Flight Route (SAAFR) */
    final String C2GM_AVN_LNE_SAAFR = "G-G-ALS-------X";
    /** Unmanned Aircraft (UA) Route */
    final String C2GM_AVN_LNE_UAR = "G-G-ALU-------X";
    /** Low Level Transit Route (LLTR) */
    final String C2GM_AVN_LNE_LLTR = "G-G-ALL-------X";
    /** Restricted Operations Zone (ROZ) */
    final String C2GM_AVN_ARS_ROZ = "G-G-AAR-------X";
    /** Short-Range Air Defense Engagement Zone (SHORADEZ) */
    final String C2GM_AVN_ARS_SHRDEZ = "G-G-AAF-------X";
    /** High Density Airspace Control Zone (HIDACZ) */
    final String C2GM_AVN_ARS_HIDACZ = "G-G-AAH-------X";
    /** Missile Engagement Zone (MEZ) */
    final String C2GM_AVN_ARS_MEZ = "G-G-AAM-------X";
    /** Low Altitude Mez */
    final String C2GM_AVN_ARS_MEZ_LAMEZ = "G-G-AAML------X";
    /** High Altitude Mez */
    final String C2GM_AVN_ARS_MEZ_HAMEZ = "G-G-AAMH------X";
    /** Weapons Free Zone */
    final String C2GM_AVN_ARS_WFZ = "G-G-AAW-------X";
    /** Dummy (Deception/Decoy) */
    final String C2GM_DCPN_DMY = "G-G-PD--------X";
    /** Axis  Of Advance For Feint */
    final String C2GM_DCPN_AAFF = "G-G-PA--------X";
    /** Direction Of Attack For Feint */
    final String C2GM_DCPN_DAFF = "G-G-PF--------X";
    /** Decoy Mined Area */
    final String C2GM_DCPN_DMA = "G-G-PM--------X";
    /** "Decoy Mined Area,  Fenced" */
    final String C2GM_DCPN_DMAF = "G-G-PY--------X";
    /** Dummy Minefield (Static) */
    final String C2GM_DCPN_DMYMS = "G-G-PN--------X";
    /** Dummy Minefield (Dynamic) */
    final String C2GM_DCPN_DMYMD = "G-G-PC--------X";
    /** Target Reference Point (TRP) */
    final String C2GM_DEF_PNT_TGTREF = "G-G-DPT-------X";
    /** Observation Post/Outpost */
    final String C2GM_DEF_PNT_OBSPST = "G-G-DPO-------X";
    /** Combat  Outpost */
    final String C2GM_DEF_PNT_OBSPST_CBTPST = "G-G-DPOC------X";
    /** Observation Post Occupied By Dismounted Scouts Or Reconnaissance */
    final String C2GM_DEF_PNT_OBSPST_RECON = "G-G-DPOR------X";
    /** Forward Observer Position */
    final String C2GM_DEF_PNT_OBSPST_FWDOP = "G-G-DPOF------X";
    /** Sensor Outpost/Listening Post (OP/Lp) */
    final String C2GM_DEF_PNT_OBSPST_SOP = "G-G-DPOS------X";
    /** Cbrn Observation Post (Dismounted) */
    final String C2GM_DEF_PNT_OBSPST_CBRNOP = "G-G-DPON------X";
    /** Forward Edge Of Battle Area (FEBA) */
    final String C2GM_DEF_LNE_FEBA = "G-G-DLF-------X";
    /** Principal Direction Of Fire (PDF) */
    final String C2GM_DEF_LNE_PDF = "G-G-DLP-------X";
    /** Battle Position */
    final String C2GM_DEF_ARS_BTLPSN = "G-G-DAB-------X";
    /** Prepared But Not Occupied */
    final String C2GM_DEF_ARS_BTLPSN_PBNO = "G-G-DABP------X";
    /** Engagement Area */
    final String C2GM_DEF_ARS_EMTARA = "G-G-DAE-------X";
    /** Point Of Departure */
    final String C2GM_OFF_PNT_PNTD = "G-G-OPP-------X";
    /** Axis Of Advance */
    final String C2GM_OFF_LNE_AXSADV = "G-G-OLA-------X";
    /** Aviation */
    final String C2GM_OFF_LNE_AXSADV_AVN = "G-G-OLAV------X";
    /** Airborne */
    final String C2GM_OFF_LNE_AXSADV_ABN = "G-G-OLAA------X";
    /** "Attack, Rotary Wing" */
    final String C2GM_OFF_LNE_AXSADV_ATK = "G-G-OLAR------X";
    /** Ground */
    final String C2GM_OFF_LNE_AXSADV_GRD = "G-G-OLAG------X";
    /** Main Attack */
    final String C2GM_OFF_LNE_AXSADV_GRD_MANATK = "G-G-OLAGM-----X";
    /** Supporting Attack */
    final String C2GM_OFF_LNE_AXSADV_GRD_SUPATK = "G-G-OLAGS-----X";
    /** Aviation */
    final String C2GM_OFF_LNE_DIRATK_AVN = "G-G-OLKA------X";
    /** Main Ground Attack */
    final String C2GM_OFF_LNE_DIRATK_GRD_MANATK = "G-G-OLKGM-----X";
    /** Supporting Ground Attack */
    final String C2GM_OFF_LNE_DIRATK_GRD_SUPATK = "G-G-OLKGS-----X";
    /** Final Coordination Line */
    final String C2GM_OFF_LNE_FCL = "G-G-OLF-------X";
    /** Infiltration Lane */
    final String C2GM_OFF_LNE_INFNLE = "G-G-OLI-------X";
    /** Limit Of Advance */
    final String C2GM_OFF_LNE_LMTADV = "G-G-OLL-------X";
    /** Line Of Departure */
    final String C2GM_OFF_LNE_LD = "G-G-OLT-------X";
    /** Line Of Departure/Line Of Contact (LD/LC) */
    final String C2GM_OFF_LNE_LDLC = "G-G-OLC-------X";
    /** Probable Line Of Deployment (PLD) */
    final String C2GM_OFF_LNE_PLD = "G-G-OLP-------X";
    /** Assault Position */
    final String C2GM_OFF_ARS_ASTPSN = "G-G-OAA-------X";
    /** Attack Position */
    final String C2GM_OFF_ARS_ATKPSN = "G-G-OAK-------X";
    /** Attack By Fire Position */
    final String C2GM_OFF_ARS_AFP = "G-G-OAF-------X";
    /** Support By Fire Position */
    final String C2GM_OFF_ARS_SFP = "G-G-OAS-------X";
    /** Objective */
    final String C2GM_OFF_ARS_OBJ = "G-G-OAO-------X";
    /** Penetration Box */
    final String C2GM_OFF_ARS_PBX = "G-G-OAP-------X";
    /** Ambush */
    final String C2GM_SPL_LNE_AMB = "G-G-SLA-------X";
    /** Holding Line */
    final String C2GM_SPL_LNE_HGL = "G-G-SLH-------X";
    /** Release Line */
    final String C2GM_SPL_LNE_REL = "G-G-SLR-------X";
    /** Bridgehead */
    final String C2GM_SPL_LNE_BRGH = "G-G-SLB-------X";
    /** Area */
    final String C2GM_SPL_ARA = "G-G-SA--------X";
    /** Area Of Operations (AO) */
    final String C2GM_SPL_ARA_AOO = "G-G-SAO-------X";
    /** Airhead */
    final String C2GM_SPL_ARA_AHD = "G-G-SAA-------X";
    /** Encirclement */
    final String C2GM_SPL_ARA_ENCMT = "G-G-SAE-------X";
    /** Named */
    final String C2GM_SPL_ARA_NAI = "G-G-SAN-------X";
    /** Targeted Area Of Interest (TAI) */
    final String C2GM_SPL_ARA_TAI = "G-G-SAT-------X";

    ///////////////////////////////////////////
    // Mobility/Survivability
    ///////////////////////////////////////////

    /** Belt */
    final String MOBSU_OBST_GNL_BLT = "G-M-OGB-------X";
    /** Line */
    final String MOBSU_OBST_GNL_LNE = "G-M-OGL-------X";
    /** Zone */
    final String MOBSU_OBST_GNL_Z = "G-M-OGZ-------X";
    /** Obstacle Free Area */
    final String MOBSU_OBST_GNL_OFA = "G-M-OGF-------X";
    /** Obstacle Restricted Area */
    final String MOBSU_OBST_GNL_ORA = "G-M-OGR-------X";
    /** Abatis */
    final String MOBSU_OBST_ABS = "G-M-OS--------X";
    /** "Antitank Ditch, Under Construction" */
    final String MOBSU_OBST_ATO_ATD_ATDUC = "G-M-OADU------X";
    /** "Antitank Ditch, Complete" */
    final String MOBSU_OBST_ATO_ATD_ATDC = "G-M-OADC------X";
    /** Antitank Ditch Reinforced With Antitank Mines */
    final String MOBSU_OBST_ATO_ATDATM = "G-M-OAR-------X";
    /** Fixed And Prefabricated */
    final String MOBSU_OBST_ATO_TDTSM_FIXPFD = "G-M-OAOF------X";
    /** Moveable */
    final String MOBSU_OBST_ATO_TDTSM_MVB = "G-M-OAOM------X";
    /** Moveable And Prefabricated */
    final String MOBSU_OBST_ATO_TDTSM_MVBPFD = "G-M-OAOP------X";
    /** Antitank Wall */
    final String MOBSU_OBST_ATO_ATW = "G-M-OAW-------X";
    /** Booby Trap */
    final String MOBSU_OBST_BBY = "G-M-OB--------X";
    /** Unspecified Mine */
    final String MOBSU_OBST_MNE_USPMNE = "G-M-OMU-------X";
    /** Antitank Mine (AT) */
    final String MOBSU_OBST_MNE_ATMNE = "G-M-OMT-------X";
    /** Antitank Mine With Antihandling Device */
    final String MOBSU_OBST_MNE_ATMAHD = "G-M-OMD-------X";
    /** Antitank Mine (Directional) */
    final String MOBSU_OBST_MNE_ATMDIR = "G-M-OME-------X";
    /** Antipersonnel (AP) Mines */
    final String MOBSU_OBST_MNE_APMNE = "G-M-OMP-------X";
    /** Wide Area Mines */
    final String MOBSU_OBST_MNE_WAMNE = "G-M-OMW-------X";
    /** Mine Cluster */
    final String MOBSU_OBST_MNE_MCLST = "G-M-OMC-------X";
    /** Static Depiction */
    final String MOBSU_OBST_MNEFLD_STC = "G-M-OFS-------X";
    /** Dynamic Depiction */
    final String MOBSU_OBST_MNEFLD_DYN = "G-M-OFD-------X";
    /** Gap */
    final String MOBSU_OBST_MNEFLD_GAP = "G-M-OFG-------X";
    /** Mined Area */
    final String MOBSU_OBST_MNEFLD_MNDARA = "G-M-OFA-------X";
    /** Block */
    final String MOBSU_OBST_OBSEFT_BLK = "G-M-OEB-------X";
    /** Fix */
    final String MOBSU_OBST_OBSEFT_FIX = "G-M-OEF-------X";
    /** Turn */
    final String MOBSU_OBST_OBSEFT_TUR = "G-M-OET-------X";
    /** Disrupt */
    final String MOBSU_OBST_OBSEFT_DRT = "G-M-OED-------X";
    /** Unexploded Ordnance Area (UXO) */
    final String MOBSU_OBST_UXO = "G-M-OU--------X";
    /** Planned */
    final String MOBSU_OBST_RCBB_PLND = "G-M-ORP-------X";
    /** "Explosives, State Of Readiness 1 (Safe)" */
    final String MOBSU_OBST_RCBB_SAFE = "G-M-ORS-------X";
    /** "Explosives, State Of Readiness 2 (Armed-But Passable)" */
    final String MOBSU_OBST_RCBB_ABP = "G-M-ORA-------X";
    /** Roadblock Complete (Executed) */
    final String MOBSU_OBST_RCBB_EXCD = "G-M-ORC-------X";
    /** Trip Wire */
    final String MOBSU_OBST_TRIPWR = "G-M-OT--------X";
    /** Wire Obstacle */
    final String MOBSU_OBST_WREOBS = "G-M-OW--------X";
    /** Unspecified */
    final String MOBSU_OBST_WREOBS_USP = "G-M-OWU-------X";
    /** Single Fence */
    final String MOBSU_OBST_WREOBS_SNGFNC = "G-M-OWS-------X";
    /** Double Fence */
    final String MOBSU_OBST_WREOBS_DBLFNC = "G-M-OWD-------X";
    /** Double Apron Fence */
    final String MOBSU_OBST_WREOBS_DAFNC = "G-M-OWA-------X";
    /** Low Wire Fence */
    final String MOBSU_OBST_WREOBS_LWFNC = "G-M-OWL-------X";
    /** High Wire Fence */
    final String MOBSU_OBST_WREOBS_HWFNC = "G-M-OWH-------X";
    /** Single Concertina */
    final String MOBSU_OBST_WREOBS_CCTA_SNG = "G-M-OWCS------X";
    /** Double Strand Concertina */
    final String MOBSU_OBST_WREOBS_CCTA_DBLSTD = "G-M-OWCD------X";
    /** Triple Strand Concertina */
    final String MOBSU_OBST_WREOBS_CCTA_TRISTD = "G-M-OWCT------X";
    /** Low Tower */
    final String MOBSU_OBST_AVN_TWR_LOW = "G-M-OHTL------X";
    /** High Tower */
    final String MOBSU_OBST_AVN_TWR_HIGH = "G-M-OHTH------X";
    /** Overhead Wire/Power Line */
    final String MOBSU_OBST_AVN_OHWIRE = "G-M-OHO-------X";
    /** Bypass Easy */
    final String MOBSU_OBSTBP_DFTY_ESY = "G-M-BDE-------X";
    /** Bypass Difficult */
    final String MOBSU_OBSTBP_DFTY_DFT = "G-M-BDD-------X";
    /** Bypass Impossible */
    final String MOBSU_OBSTBP_DFTY_IMP = "G-M-BDI-------X";
    /** Crossing Site/Water Crossing */
    final String MOBSU_OBSTBP_CSGSTE = "G-M-BC--------X";
    /** Assault Crossing Area */
    final String MOBSU_OBSTBP_CSGSTE_ASTCA = "G-M-BCA-------X";
    /** Bridge or Gap */
    final String MOBSU_OBSTBP_CSGSTE_BRG = "G-M-BCB-------X";
    /** Ferry */
    final String MOBSU_OBSTBP_CSGSTE_FRY = "G-M-BCF-------X";
    /** Ford Easy */
    final String MOBSU_OBSTBP_CSGSTE_FRDESY = "G-M-BCE-------X";
    /** Ford Difficult */
    final String MOBSU_OBSTBP_CSGSTE_FRDDFT = "G-M-BCD-------X";
    /** Lane */
    final String MOBSU_OBSTBP_CSGSTE_LANE = "G-M-BCL-------X";
    /** Raft Site */
    final String MOBSU_OBSTBP_CSGSTE_RFT = "G-M-BCR-------X";
    /** Engineer Regulating Point */
    final String MOBSU_OBSTBP_CSGSTE_ERP = "G-M-BCP-------X";
    /** "Earthwork, Small Trench Or Fortification" */
    final String MOBSU_SU_ESTOF = "G-M-SE--------X";
    /** Fort */
    final String MOBSU_SU_FRT = "G-M-SF--------X";
    /** Fortified Line */
    final String MOBSU_SU_FTFDLN = "G-M-SL--------X";
    /** "Foxhole, Emplacement Or Weapon Site" */
    final String MOBSU_SU_FEWS = "G-M-SW--------X";
    /** Strong Point */
    final String MOBSU_SU_STRGPT = "G-M-SP--------X";
    /** Surface Shelter */
    final String MOBSU_SU_SUFSHL = "G-M-SS--------X";
    /** Underground Shelter */
    final String MOBSU_SU_UGDSHL = "G-M-SU--------X";
    /** Minimum Safe Distance Zones */
    final String MOBSU_CBRN_MSDZ = "G-M-NM--------X";
    /** Nuclear Detonations Ground Zero */
    final String MOBSU_CBRN_NDGZ = "G-M-NZ--------X";
    /** Fallout Producing */
    final String MOBSU_CBRN_FAOTP = "G-M-NF--------X";
    /** Radioactive Area */
    final String MOBSU_CBRN_RADA = "G-M-NR--------X";
    /** Biologically Contaminated Area */
    final String MOBSU_CBRN_BIOCA = "G-M-NB--------X";
    /** Chemically Contaminated Area */
    final String MOBSU_CBRN_CMLCA = "G-M-NC--------X";
    /** Biological Release Event */
    final String MOBSU_CBRN_REEVNT_BIO = "G-M-NEB-------X";
    /** Chemical Release Event */
    final String MOBSU_CBRN_REEVNT_CML = "G-M-NEC-------X";
    /** Decon Site/Point (Unspecified) */
    final String MOBSU_CBRN_DECONP_USP = "G-M-NDP-------X";
    /** Alternate Decon Site/Point (Unspecified) */
    final String MOBSU_CBRN_DECONP_ALTUSP = "G-M-NDA-------X";
    /** Decon Site/Point (Troops) */
    final String MOBSU_CBRN_DECONP_TRP = "G-M-NDT-------X";
    /** Decon , */
    final String MOBSU_CBRN_DECONP_EQT = "G-M-NDE-------X";
    /** Decon Site/Point (Equipment And Troops) */
    final String MOBSU_CBRN_DECONP_EQTTR = "G-M-NDB-------X";
    /** Decon Site/Point (Operational Decontamination) */
    final String MOBSU_CBRN_DECONP_OPDECN = "G-M-NDO-------X";
    /** Decon Site/Point (Thorough Decontamination) */
    final String MOBSU_CBRN_DECONP_TRGH = "G-M-NDD-------X";
    /** Dose Rate Contour Lines */
    final String MOBSU_CBRN_DRCL = "G-M-NL--------X";

    /////////////////////////////////////////////////
    // Fire Support
    /////////////////////////////////////////////////

    /** Point/Single Target */
    final String FSUPP_PNT_TGT_PTGT = "G-F-PTS-------X";
    /** Nuclear Target */
    final String FSUPP_PNT_TGT_NUCTGT = "G-F-PTN-------X";
    /** Fire Support Station */
    final String FSUPP_PNT_C2PNT_FSS = "G-F-PCF-------X";
    /** Survey Control Point */
    final String FSUPP_PNT_C2PNT_SCP = "G-F-PCS-------X";
    /** Firing Point */
    final String FSUPP_PNT_C2PNT_FP = "G-F-PCB-------X";
    /** Reload Point */
    final String FSUPP_PNT_C2PNT_RP = "G-F-PCR-------X";
    /** Hide Point */
    final String FSUPP_PNT_C2PNT_HP = "G-F-PCH-------X";
    /** Launch Point */
    final String FSUPP_PNT_C2PNT_LP = "G-F-PCL-------X";
    /** Linear Target */
    final String FSUPP_LNE_LNRTGT = "G-F-LT--------X";
    /** Linear Smoke Target */
    final String FSUPP_LNE_LNRTGT_LSTGT = "G-F-LTS-------X";
    /** Final Protective Fire (FPF) */
    final String FSUPP_LNE_LNRTGT_FPF = "G-F-LTF-------X";
    /** Fire Support Coordination Line (FSCL) */
    final String FSUPP_LNE_C2LNE_FSCL = "G-F-LCF-------X";
    /** Coordinated Fire Line (CFL) */
    final String FSUPP_LNE_C2LNE_CFL = "G-F-LCC-------X";
    /** No-Fire Line (NFL) */
    final String FSUPP_LNE_C2LNE_NFL = "G-F-LCN-------X";
    /** Restrictive */
    final String FSUPP_LNE_C2LNE_RFL = "G-F-LCR-------X";
    /** Munition Flight Path (MFP) */
    final String FSUPP_LNE_C2LNE_MFP = "G-F-LCM-------X";
    /** Area Target */
    final String FSUPP_ARS_ARATGT = "G-F-AT--------X";
    /** Rectangular Target */
    final String FSUPP_ARS_ARATGT_RTGTGT = "G-F-ATR-------X";
    /** Circular Target */
    final String FSUPP_ARS_ARATGT_CIRTGT = "G-F-ATC-------X";
    /** Series Or Group Of Targets */
    final String FSUPP_ARS_ARATGT_SGTGT = "G-F-ATG-------X";
    /** Smoke */
    final String FSUPP_ARS_ARATGT_SMK = "G-F-ATS-------X";
    /** Bomb Area */
    final String FSUPP_ARS_ARATGT_BMARA = "G-F-ATB-------X";
    /** "Fire Support Area (FSA), Irregula" */
    final String FSUPP_ARS_C2ARS_FSA_IRR = "G-F-ACSI------X";
    /** "Fire Support Area (FSA), Rectangular" */
    final String FSUPP_ARS_C2ARS_FSA_RTG = "G-F-ACSR------X";
    /** "Fire Support Area (FSA), Circular" */
    final String FSUPP_ARS_C2ARS_FSA_CIRCLR = "G-F-ACSC------X";
    /** "Airspace Coordination Area (ACA), Irregular" */
    final String FSUPP_ARS_C2ARS_ACA_IRR = "G-F-ACAI------X";
    /** "Airspace Coordination Area (ACA), Rectangular" */
    final String FSUPP_ARS_C2ARS_ACA_RTG = "G-F-ACAR------X";
    /** "Airspace Coordination Area (ACA), Circular" */
    final String FSUPP_ARS_C2ARS_ACA_CIRCLR = "G-F-ACAC------X";
    /** "Free Fire Area (FFA), Irregular" */
    final String FSUPP_ARS_C2ARS_FFA_IRR = "G-F-ACFI------X";
    /** "Free Fire Area (FFA), Rectangular" */
    final String FSUPP_ARS_C2ARS_FFA_RTG = "G-F-ACFR------X";
    /** "Free Fire Area (FFA), Circular" */
    final String FSUPP_ARS_C2ARS_FFA_CIRCLR = "G-F-ACFC------X";
    /** "No Fire Area (NFA), Irregular" */
    final String FSUPP_ARS_C2ARS_NFA_IRR = "G-F-ACNI------X";
    /** "No Fire Area (NFA), Rectangular" */
    final String FSUPP_ARS_C2ARS_NFA_RTG = "G-F-ACNR------X";
    /** "No , Circular" */
    final String FSUPP_ARS_C2ARS_NFA_CIRCLR = "G-F-ACNC------X";
    /** "Restrictive Fire Area (RFA), Irregular" */
    final String FSUPP_ARS_C2ARS_RFA_IRR = "G-F-ACRI------X";
    /** "Restrictive Fire Area (RFA), Rectangular" */
    final String FSUPP_ARS_C2ARS_RFA_RTG = "G-F-ACRR------X";
    /** "Restrictive Fire Area (RFA), Circular" */
    final String FSUPP_ARS_C2ARS_RFA_CIRCLR = "G-F-ACRC------X";
    /** "Position Area For Artillery (PAA), Rectangular" */
    final String FSUPP_ARS_C2ARS_PAA_RTG = "G-F-ACPR------X";
    /** "Position Area For Artillery (PAA), Circular" */
    final String FSUPP_ARS_C2ARS_PAA_CIRCLR = "G-F-ACPC------X";
    /** "Sensor Zone, Irregular" */
    final String FSUPP_ARS_C2ARS_SNSZ_IRR = "G-F-ACEI------X";
    /** "Sensor Zone, Rectangular" */
    final String FSUPP_ARS_C2ARS_SNSZ_RTG = "G-F-ACER------X";
    /** "Sensor Zone ,  Circular" */
    final String FSUPP_ARS_C2ARS_SNSZ_CIRCLR = "G-F-ACEC------X";
    /** "Dead Space Area (DA),  Irregular" */
    final String FSUPP_ARS_C2ARS_DA_IRR = "G-F-ACDI------X";
    /** "Dead Space Area (DA),  Rectangular" */
    final String FSUPP_ARS_C2ARS_DA_RTG = "G-F-ACDR------X";
    /** "Dead Space Area (DA),  Circular" */
    final String FSUPP_ARS_C2ARS_DA_CIRCLR = "G-F-ACDC------X";
    /** "Zone Of Responsibility (ZOR), Irregular" */
    final String FSUPP_ARS_C2ARS_ZOR_IRR = "G-F-ACZI------X";
    /** "Zone Of Responsibility (ZOR), Rectangular" */
    final String FSUPP_ARS_C2ARS_ZOR_RTG = "G-F-ACZR------X";
    /** "Zone Of Responsibility (ZOR), Circular" */
    final String FSUPP_ARS_C2ARS_ZOR_CIRCLR = "G-F-ACZC------X";
    /** "Target Build Up Area (TBA), Irregular" */
    final String FSUPP_ARS_C2ARS_TBA_IRR = "G-F-ACBI------X";
    /** "Target Build Up Area (TBA),Rectangular" */
    final String FSUPP_ARS_C2ARS_TBA_RTG = "G-F-ACBR------X";
    /** "Target Build Up Area (TBA), Circular" */
    final String FSUPP_ARS_C2ARS_TBA_CIRCLR = "G-F-ACBC------X";
    /** "Target , Irregular" */
    final String FSUPP_ARS_C2ARS_TVAR_IRR = "G-F-ACVI------X";
    /** "Target Value Area (TVAR), Rectangular" */
    final String FSUPP_ARS_C2ARS_TVAR_RTG = "G-F-ACVR------X";
    /** "Target Value Area (TVAR), Circular" */
    final String FSUPP_ARS_C2ARS_TVAR_CIRCLR = "G-F-ACVC------X";
    /** Terminally Guided Munition Footprint (TGMF) */
    final String FSUPP_ARS_C2ARS_TGMF = "G-F-ACT-------X";
    /** "Artillery Target Intelligence (ATI) Zone, Irregular" */
    final String FSUPP_ARS_TGTAQZ_ATIZ_IRR = "G-F-AZII------X";
    /** "Artillery Target Intelligence (ATI) Zone, Rectangular" */
    final String FSUPP_ARS_TGTAQZ_ATIZ_RTG = "G-F-AZIR------X";
    /** "Call For Fire Zone (CFFZ), Irregular" */
    final String FSUPP_ARS_TGTAQZ_CFFZ_IRR = "G-F-AZXI------X";
    /** "Call For Fire Zone (CFFZ), Rectangular" */
    final String FSUPP_ARS_TGTAQZ_CFFZ_RTG = "G-F-AZXR------X";
    /** "Censor Zone,  Irregular" */
    final String FSUPP_ARS_TGTAQZ_CNS_IRR = "G-F-AZCI------X";
    /** "Censor Zone, Rectangular" */
    final String FSUPP_ARS_TGTAQZ_CNS_RTG = "G-F-AZCR------X";
    /** "Critical Friendly Zone (CFZ), Irregular" */
    final String FSUPP_ARS_TGTAQZ_CFZ_IRR = "G-F-AZFI------X";
    /** "Critical Friendly Zone (CFZ), Rectangular" */
    final String FSUPP_ARS_TGTAQZ_CFZ_RTG = "G-F-AZFR------X";
    /** "Weapon/Sensor Range Fan, Circular" */
    final String FSUPP_ARS_WPNRF_CIRCLR = "G-F-AXC-------X";
    /** "Weapon/Sensor Range Fan, Sector" */
    final String FSUPP_ARS_WPNRF_SCR = "G-F-AXS-------X";
    /** "Blue Kill Box,  Circular" */
    final String FSUPP_ARS_KLBOX_BLUE_CIRCLR = "G-F-AKBC------X";
    /** "Blue Kill Box, Irregular" */
    final String FSUPP_ARS_KLBOX_BLUE_IRR = "G-F-AKBI------X";
    /** "Blue , Rectangular" */
    final String FSUPP_ARS_KLBOX_BLUE_RTG = "G-F-AKBR------X";
    /** "Purple Kill Box, Circular" */
    final String FSUPP_ARS_KLBOX_PURPLE_CIRCLR = "G-F-AKPC------X";
    /** "Purple Kill Box, Irregular" */
    final String FSUPP_ARS_KLBOX_PURPLE_IRR = "G-F-AKPI------X";
    /** "Purple Kill Box, Rectangular" */
    final String FSUPP_ARS_KLBOX_PURPLE_RTG = "G-F-AKPR------X";

    ////////////////////////////////////////////////
    // Combat Service Support
    ////////////////////////////////////////////////

    /** Ambulance Exchange Point */
    final String CSS_PNT_AEP = "G-S-PX--------X";
    /** Cannibalization Point */
    final String CSS_PNT_CBNP = "G-S-PC--------X";
    /** Casualty Collection Point */
    final String CSS_PNT_CCP = "G-S-PY--------X";
    /** Civilian Collection Point */
    final String CSS_PNT_CVP = "G-S-PT--------X";
    /** Detainee Collection Point */
    final String CSS_PNT_DCP = "G-S-PD--------X";
    /** Enemy Prisoner Of War (EPW) Collection Point */
    final String CSS_PNT_EPWCP = "G-S-PE--------X";
    /** Logistics Release Point (LRP) */
    final String CSS_PNT_LRP = "G-S-PL--------X";
    /** Maintenance Collection Point */
    final String CSS_PNT_MCP = "G-S-PM--------X";
    /** "Rearm, Refuel And Resupply Point" */
    final String CSS_PNT_RRRP = "G-S-PR--------X";
    /** Refuel On The Move (ROM) Point */
    final String CSS_PNT_ROM = "G-S-PU--------X";
    /** Traffic Control Post (TCP) */
    final String CSS_PNT_TCP = "G-S-PO--------X";
    /** Trailer Transfer Point */
    final String CSS_PNT_TTP = "G-S-PI--------X";
    /** Unit Maintenance Collection Point */
    final String CSS_PNT_UMC = "G-S-PN--------X";
    /** Supply Points */
    final String CSS_PNT_SPT = "G-S-PS--------X";
    /** General */
    final String CSS_PNT_SPT_GNL = "G-S-PSZ-------X";
    /** Class I */
    final String CSS_PNT_SPT_CLS1 = "G-S-PSA-------X";
    /** Class Ii */
    final String CSS_PNT_SPT_CLS2 = "G-S-PSB-------X";
    /** Class Iii */
    final String CSS_PNT_SPT_CLS3 = "G-S-PSC-------X";
    /** Class Iv */
    final String CSS_PNT_SPT_CLS4 = "G-S-PSD-------X";
    /** Class V */
    final String CSS_PNT_SPT_CLS5 = "G-S-PSE-------X";
    /** Class Vi */
    final String CSS_PNT_SPT_CLS6 = "G-S-PSF-------X";
    /** Class Vii */
    final String CSS_PNT_SPT_CLS7 = "G-S-PSG-------X";
    /** Class Viii */
    final String CSS_PNT_SPT_CLS8 = "G-S-PSH-------X";
    /** Class Ix */
    final String CSS_PNT_SPT_CLS9 = "G-S-PSI-------X";
    /** Class X */
    final String CSS_PNT_SPT_CLS10 = "G-S-PSJ-------X";
    /** Ammunition Points */
    final String CSS_PNT_AP = "G-S-PA--------X";
    /** Ammunition Supply Point (ASP) */
    final String CSS_PNT_AP_ASP = "G-S-PAS-------X";
    /** Ammunition Transfer Point (ATP) */
    final String CSS_PNT_AP_ATP = "G-S-PAT-------X";
    /** Moving Convoy */
    final String CSS_LNE_CNY_MCNY = "G-S-LCM-------X";
    /** Halted Convoy */
    final String CSS_LNE_CNY_HCNY = "G-S-LCH-------X";
    /** Main Supply Route */
    final String CSS_LNE_SLPRUT_MSRUT = "G-S-LRM-------X";
    /** Alternate Supply Route */
    final String CSS_LNE_SLPRUT_ASRUT = "G-S-LRA-------X";
    /** One-Way Traffic */
    final String CSS_LNE_SLPRUT_1WTRFF = "G-S-LRO-------X";
    /** Alternating Traffic */
    final String CSS_LNE_SLPRUT_ATRFF = "G-S-LRT-------X";
    /** Two-Way Traffic */
    final String CSS_LNE_SLPRUT_2WTRFF = "G-S-LRW-------X";
    /** Detainee Holding Area */
    final String CSS_ARA_DHA = "G-S-AD--------X";
    /** Enemy Prisoner Of War (EPW) Holding Area */
    final String CSS_ARA_EPWHA = "G-S-AE--------X";
    /** Forward Arming And Refueling Area (FARP) */
    final String CSS_ARA_FARP = "G-S-AR--------X";
    /** Refugee Holding Area */
    final String CSS_ARA_RHA = "G-S-AH--------X";
    /** Brigade (BSA) */
    final String CSS_ARA_SUPARS_BSA = "G-S-ASB-------X";
    /** Division (DSA) */
    final String CSS_ARA_SUPARS_DSA = "G-S-ASD-------X";
    /** Regimental (RSA) */
    final String CSS_ARA_SUPARS_RSA = "G-S-ASR-------X";

    //////////////////////////////////////////////
    // Other
    //////////////////////////////////////////////

    /** Ditched Aircraft */
    final String OTH_ER_DTHAC = "G-O-ED------X";
    /** Person In Water */
    final String OTH_ER_PIW = "G-O-EP--------X";
    /** Distressed Vessel */
    final String OTH_ER_DSTVES = "G-O-EV--------X";
    /** Sea Mine-Like */
    final String OTH_HAZ_SML = "G-O-HM--------X";
    /** Navigational */
    final String OTH_HAZ_NVGL = "G-O-HN--------X";
    /** Iceberg */
    final String OTH_HAZ_IB = "G-O-HI--------X";
    /** Oil Rig */
    final String OTH_HAZ_OLRG = "G-O-HO--------X";
    /** Bottom Return/Non-Milco */
    final String OTH_SSUBSR_BTMRTN = "G-O-SB--------X";
    /** Installation/Manmade */
    final String OTH_SSUBSR_BTMRTN_INS = "G-O-SBM-------X";
    /** "Seabed Rock/Stone,  Obstacle,Other" */
    final String OTH_SSUBSR_BTMRTN_SBRSOO = "G-O-SBN-------X";
    /** "Wreck,  Non Dangerous" */
    final String OTH_SSUBSR_BTMRTN_WRKND = "G-O-SBW-------X";
    /** "Wreck,  Dangerous" */
    final String OTH_SSUBSR_BTMRTN_WRKD = "G-O-SBX-------X";
    /** Marine Life */
    final String OTH_SSUBSR_MARLFE = "G-O-SM--------X";
    /** "Sea Anomaly (Wake, Current, Knuckle)" */
    final String OTH_SSUBSR_SA = "G-O-SS--------X";
    /** Bearing Line */
    final String OTH_BERLNE = "G-O-B---------X";
    /** Electronic Bearing Line */
    final String OTH_BERLNE_ELC = "G-O-BE--------X";
    /** Acoustic Bearing Line */
    final String OTH_BERLNE_ACU = "G-O-BA--------X";
    /** Torpedo, Bearing Line */
    final String OTH_BERLNE_TPD = "G-O-BT--------X";
    /** Electro-Optical Intercept */
    final String OTH_BERLNE_EOPI = "G-O-BO--------X";
    /** Acoustic Fix */
    final String OTH_FIX_ACU = "G-O-FA--------X";
    /** Electro-Magnetic Fix */
    final String OTH_FIX_EM = "G-O-FE--------X";
    /** Electro-Optical Fix */
    final String OTH_FIX_EOP = "G-O-FO--------X";
}
