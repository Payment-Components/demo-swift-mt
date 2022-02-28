package com.paymentcomponents.mt.demo;

import gr.datamation.mt.common.RepSeq;
import gr.datamation.mt.common.SwiftMessage;
import gr.datamation.mt.common.Tag;
import gr.datamation.mt.processor.SwiftMsgProcessor;
import gr.datamation.mt.validator.SwiftMsgValidator;
import gr.datamation.mt.validator.SwiftValidObj;

import java.util.Vector;

public class BuildMT101_1 {

    public static void execute() {
        System.out.println("Build MT 101 Way 1");
        SwiftMessage message = new SwiftMessage();

        // Set Tags for block1
        message.setArgApplid("F");
        message.setArgServid("01");
        message.setArgLTaddrBlk1("COPZBEB0AXXX");

        // Set Tags for block2
        message.setArgInoutind("I");
        message.setArgMsgtype("101");
        message.setArgLTaddrBlk2("LRLRXXXX4A11");
        message.setArgMsgprior("N");

        // Set Tags for block3

        message.getBlock3().add(new Tag("108", new Vector<String>() {{
            add("MT103 005 OF 020");
        }}));


        // Set Tags for block4
        message.getBlock4().add(new Tag("20", new Vector<String>(){{add("5387354");}}));
        message.getBlock4().add(new Tag("28D", new Vector<String>(){{add("1/1");}}));
        message.getBlock4().add(new Tag("50F", new Vector<String>(){{
            add("/409074-293-45/786");
            add("1/George Philips");
            add("2/High Street 1");
            add("3/GB/London");
        }}));
        message.getBlock4().add(new Tag("30", new Vector<String>(){{add("011231");}}));

        RepSeq repSeqB = new RepSeq();
        repSeqB.addTag(new Tag("21", new Vector<String>(){{add("PQR-27ZZ-01");}}));
        repSeqB.addTag(new Tag("32B", new Vector<String>(){{add("USD2564,50");}}));
        repSeqB.addTag(new Tag("57D", new Vector<String>(){{
            add("/C/Clementine Nuggets-1842-Y");
            add("MynR49R RailRoad Trust");
            add("Cloudsboro ARTUI");
        }}));
        repSeqB.addTag(new Tag("59F", new Vector<String>(){{
            add("1/Beneficiary Name-1234567891234123");
            add("2/QWERT");
            add("3/US/Beneficiary Address Line 21");
            add("3/Beneficiary Name-1234567891234123");
        }}));
        repSeqB.addTag(new Tag("71A", new Vector<String>(){{add("OUR");}}));
        message.getBlock4().add(repSeqB);

        // Set Tags for block5
        message.getBlock5().add(new Tag("MAC", new Vector<String>() {{
            add("00000000");
        }}));

        message.getBlock5().add(new Tag("CHK", new Vector<String>() {{
            add("4BCF59104AF9");
        }}));

        SwiftValidObj swiftValidObj = new SwiftMsgValidator().validateMsg(message);
        if (swiftValidObj.hasErrors()){
            Utils.printErrors(swiftValidObj);
        } else {
            try {
                System.out.println(new SwiftMsgProcessor().BuildMsgStringFromObject(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
