package com.paymentcomponents.swift.mt;

import gr.datamation.swift.common.InvalidMessageFormatException;
import gr.datamation.swift.common.SwiftMessage;
import gr.datamation.swift.processor.SwiftMsgProcessor;
import gr.datamation.swift.validator.SwiftMsgValidator;
import gr.datamation.swift.validator.SwiftValidObj;

public class ParseInvalidMT01 {

    public static void execute() {
        System.out.println("Parsing invalid MT101 message");
        invalidCurrencyCode();
        nonExpectedCharacter();
        invalidFormat();
        mandatoryFieldMissing();
        networkValidationError();
    }

    private static void validate(String mt101String){
        SwiftMsgProcessor parser = new SwiftMsgProcessor("\n");
        try {
            SwiftMessage smObj = parser.ParseMsgStringToObject(mt101String);
            //Validate the message
            SwiftValidObj swiftValidObj = new SwiftMsgValidator().validateMsg(smObj);
            //Check if it has any errors
            if (swiftValidObj.hasErrors()) {
                Utils.printErrors(swiftValidObj);
            }
        } catch (InvalidMessageFormatException e) {
            System.err.println("Message cannot be parsed");
        }
    }

    private static void invalidCurrencyCode(){
        validate("{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
                ":20:00043\n" +
                ":28D:1/1\n" +
                ":50F:/409074-293-45/786\n" +
                "1/George Philips\n" +
                "2/High Street 1\n" +
                "3/GB/London\n" +
                ":30:011231\n" +
                ":21:PQR-27ZZ-01\n" +
                ":32B:AAA2564,50\n" +
                ":57D:/C/Clementine Nuggets-1842-Y\n" +
                "MynR49R RailRoad Trust\n" +
                "Cloudsboro ARTUI\n" +
                ":59F:1/Beneficiary Name-1234567891234123\n" +
                "2/QWERT\n" +
                "3/US/Beneficiary Address Line 21\n" +
                "3/Beneficiary Name-1234567891234123\n" +
                ":71A:OUR\n" +
                "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}");

    }

    private static void nonExpectedCharacter(){
        validate("{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
                ":20:00043@\n" +
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
                "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}");

    }

    private static void invalidFormat(){
        validate("{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
                ":20:0004322222222222222\n" +
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
                "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}");

    }

    private static void mandatoryFieldMissing(){
        validate("{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
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
                "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}");

    }

    private static void networkValidationError(){
        validate("{1:F01COPZBEB0AXXX0377002843}{2:O1011519110804LRLRXXXX4A1100009044661108041720N}{3:{108:MT101 006 OF 020}{433:/AOK/NO HIT DETECTED     }}{4:\n" +
                ":20:1234567890123456\n" +
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
                ":33B:EUR2564,50\n" +
                ":71A:OUR\n" +
                "-}{5:{MAC:00000000}{CHK:19DA346889CC}{TNG:}}{S:{SAC:}{COP:P}}");

    }

}
