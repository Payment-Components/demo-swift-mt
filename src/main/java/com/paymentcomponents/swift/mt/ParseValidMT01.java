package com.paymentcomponents.swift.mt;

import gr.datamation.swift.common.InvalidMessageFormatException;
import gr.datamation.swift.common.SwiftMessage;
import gr.datamation.swift.common.Tag;
import gr.datamation.swift.processor.SwiftMsgProcessor;
import gr.datamation.swift.validator.SwiftMsgValidator;
import gr.datamation.swift.validator.SwiftValidObj;

public class ParseValidMT01 {
    private static final String mt101String =
            "{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
                    ":20:00043\n" +
                    ":28D:1/1\n" +
                    ":50F:/409074-293-45/786\n" +
                    "1/George Philips\n" +
                    "2/High Street 1\n" +
                    "3/GB/London\n" +
                    ":30:011231\n" +
                    ":21:PQR-27ZZ-01\n" +
                    ":32B:USD2564,50\n" +
                    ":57D:/C/Clementine Nuggets-1842-Y\n" +
                    "MynR49R RailRoad Trust\n" +
                    "Cloudsboro ARTUI\n" +
                    ":59F:1/Beneficiary Name-1234567891234123\n" +
                    "2/QWERT\n" +
                    "3/US/Beneficiary Address Line 21\n" +
                    "3/Beneficiary Name-1234567891234123\n" +
                    ":71A:OUR\n" +
                    "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}";

    public static void execute() {
        //You can instatiate the SwiftMsgProcessor with the EOL of your choice.
        //\n for linux based systems or \r\b for Windows
        //If the default constructor is used then the \n will be used by default
        SwiftMsgProcessor parser = new SwiftMsgProcessor("\n");
        try {
            System.out.println("Parsing a valid MT101 message");
            SwiftMessage smObj = parser.ParseMsgStringToObject(mt101String);
            System.out.println("Sender " + smObj.getArgLTaddrBlk1());
            System.out.println("Receiver " + smObj.getArgLTaddrBlk2());
            Tag tag20 = smObj.getTag("20");
            Tag tag50a = smObj.getTag("50F");
            //Get specific tag from the SwiftMessage object
            System.out.println("Tag " + tag20.getName() + " " +
                    " " + tag20.getValueAsString());
            System.out.println("Tag " + tag50a.getName() + " Number of Lines "
                    + tag50a.getNumberOfDataLines());
            tag50a.getData().stream().forEach(line ->
                    System.out.println(tag50a.getName() + " line " + line));
            //In the full version of the product you will have access to the tag's
            // description as given by swift by calling the following method
            //System.out.println("Description: " +
            //        tag20.getDescription(smObj.getArgMsgtype()));
            //Output  "Description: Sender's Reference"

            //Validate the message
            SwiftValidObj swiftValidObj = new SwiftMsgValidator().validateMsg(smObj);
            //Check if it has any errors
            if (!swiftValidObj.hasErrors()) {
                System.out.println("Message is valid");
            } else {
                Utils.printErrors(swiftValidObj);
            }
        } catch (InvalidMessageFormatException e) {
            System.err.println("Message cannot be parsed");
        }
    }
}