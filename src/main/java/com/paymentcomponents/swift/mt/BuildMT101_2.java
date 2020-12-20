package com.paymentcomponents.swift.mt;

import gr.datamation.swift.common.RepSeq;
import gr.datamation.swift.common.SwiftMessage;
import gr.datamation.swift.common.Tag;
import gr.datamation.swift.processor.SwiftMsgProcessor;
import gr.datamation.swift.validator.SwiftMsgValidator;
import gr.datamation.swift.validator.SwiftValidObj;

import java.util.Collections;
import java.util.Vector;

public class BuildMT101_2 {

    public static void execute() {
        System.out.println("Build MT 101 - Way2");
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

        message.getBlock3().addAll(createTagList("108:MT101 005 OF 020"));

        message.getBlock4().addAll(createTagList(":20:00043\n" +
                        ":28D:1/1\n" +
                        ":50F:/409074-293-45/786\n" +
                        "1/George Philips\n" +
                        "2/High Street 1\n" +
                        "3/GB/London\n" +
                        ":30:011231"));
        message.getBlock4().add(createRepSeq(":21:PQR-27ZZ-01\n" +
                ":32B:USD2564,50\n" +
                ":57D:/C/Clementine Nuggets-1842-Y\n" +
                "MynR49R RailRoad Trust\n" +
                "Cloudsboro ARTUI\n" +
                ":59F:1/Beneficiary Name-1234567891234123\n" +
                "2/QWERT\n" +
                "3/US/Beneficiary Address Line 21\n" +
                "3/Beneficiary Name-1234567891234123\n" +
                ":71A:OUR"));

        message.getBlock5().addAll(createTagList("MAC:00000000", "CHK:4BCF59104AF9"));
        SwiftValidObj swiftValidObj = new SwiftMsgValidator().validateMsg(message);
        if(swiftValidObj.hasErrors()) {
            Utils.printErrors(swiftValidObj);
        } else {
            try {
                System.out.println(new SwiftMsgProcessor().BuildMsgStringFromObject(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static RepSeq createRepSeq(String repSeqString) {
        return new RepSeq(createTagList(repSeqString.split("\n:")));
    }

    public static Vector<Tag> createTagList(String block4Text) {
        return createTagList(block4Text.split("\n:"));
    }

    public static Vector<Tag> createTagList(String... tagValues) {
        Vector<Tag> tagList = new Vector<Tag>();
        for (String tag : tagValues) {
            tagList.add(createTag(tag));
        }
        return tagList;
    }

    public static Tag createTag(String tag) {
        String tagKey = tag.replaceAll("(?s):?(.*):.*", "$1");
        String tagValueString = tag.replaceAll("(?s):?.*:(.*)", "$1");
        String[] lines = tagValueString.split("\n");

        Vector tagValue = new Vector();
        Collections.addAll(tagValue, lines);

        return new Tag(tagKey, tagValue);
    }
}
