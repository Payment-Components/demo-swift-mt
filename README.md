# Swift MT Message Validator Demo

The project is here to demonstrate how our [SDK](https://www.paymentcomponents.com/messaging-libraries/) for Swift MT Messages Validation works. For our demonstration we are going to use the demo SDK which can parse/validate/generate an MT101 message. 

It's a simple maven project, you can download it and run it, with Java 1.8 or above.

## API Specification

### Installation
Incorporate the SDK into your project by the regular IDE means. This process will vary depending upon your specific IDE and you should consult your documentation on how to deploy a bean. For example in Eclipse all that needs to be done is to import the jar files into a project.
Alternatively, you can import it as a Maven or Gradle dependency

#### Maven
Define repository in the repositories section
```
<repository>
    <id>paymentcomponents</id>
    <url>https://nexus.paymentcomponents.com/repository/public</url>
</repository>

```

Import the SDK
```
<dependency>
  <groupId>gr.datamation</groupId>
  <artifactId>smv</artifactId>
  <version>20.2.0</version>
  <classifier>demo</classifier>
</dependency>
```

#### Gradle 
Define repository in the repositories section
```
repositories {
    maven {
        url "https://nexus.paymentcomponents.com/repository/public"
    }
}
```

Import the SDK
```
implementation 'gr.datamation:smv:20.2.0:demo@jar'
```
In case you purchase the SDK you will be given a protected Maven repository with a user name and a password. You can configure your project to download the SDK from there.

#### Other dependencies
There is a dependency in jdom2 library which is used for XML generation. In case it's a maven projects the following dependency it will added to your project, otherwise you need to include the jar manually to the classpath
```
<dependency>
    <groupId>org.jdom</groupId>
    <artifactId>jdom2</artifactId>
    <version>2.0.6</version>
</dependency>
```

In this project you can see code for all the basic manipulation of an MT message, like :
- [Parse a valid MT](src/main/java/com/paymentcomponents/swift/mt/ParseValidMT01.java)
- [Parse invalid MTs](src/main/java/com/paymentcomponents/swift/mt/ParseInvalidMT01.java) and get the syntax and network validations error
- [Build an MT - Way 1](src/main/java/com/paymentcomponents/swift/mt/BuildMT101_1.java)
- [Build an MT - Way 2](src/main/java/com/paymentcomponents/swift/mt/BuildMT101_2.java)
- [Convert an MT from text to xml](src/main/java/com/paymentcomponents/swift/mt/ConvertMT2XML.java)



### SwiftMsgParser object
This object has two methods encapsulating all the functionality needed to handle a SWIFT message

#### ParseMsgStringToObject method
This is the method used to parse a swift message. It takes as input a string containing a single message as it arrives from the SWIFT network. The method first validates the message for syntax errors, to ensure that the message string was extracted correctly from the SWIFT system. Then parses it producing the SwiftMessage object containing the values of the message tags. The parser uses an efficient and fast algorithm that ensures the correct breakdown of the message to its information in the minimum time.
{1:F01ABCDGRA0AXXX0057000289}{2:O1030919010321DDDDGRA0AXXX00570001710103210920N}{3:{113:ABCD}{111:111}{121:01911b73-1f15-4932-934a-609149301922}}{4:crlf
:20:494930/DEVcrlf
:32A:020527EUR1958,47crlf
:50:BIODATA GJBHcrlf
ZURICHcrlf
:59:S.T JANSSENcrlf
LEDEBOERSTRAAT 29crlf
AMSTERDAMcrlf
-}{5:{MAC:75D138E4}{CHK:DE1B0D71FA96}{TNG:}}{S:{SAC:}{COP}}

Observe there are carriage-return line-feeds (crlf) separating each line. So when using your own examples, make sure you use the appropriate crlf's.
To parse it, do the following:

```
//Put your SWIFT message in a string
String message = "{1:F01ABCDGRA0AXXX0057000289}{2:O1000919010321DDDDGRA0AXXX00570001710103210920N}{3:{113:ABCD}{111:111}{121:01911b73-1f15-4932-934a-609149301922}}{4:\n:20:494930/DEV\n:32A:020527EUR1958,47\n:50:BIODATA GJBH\nZURICH\n:59:S.T JANSSEN\nLEDEBOERSTRAAT 29\nAMSTERDAM\n}{5:{MAC:75D138E4}{CHK:DE1B0D71FA96}{TNG:}}{S:{SAC:}{COP}}";
//Create the parser object as below
SwiftMsgProcessor parser = new SwiftMsgProcessor();
//Create the return object to get back the results
SwiftMessage smObj = parser.ParseMsgStringToObject(Message);
```

You are done! Now the smObj contains the parsed message. To see how to get or manipulate this object read the "SwiftMessage object" section below.

#### ParseMsgStringsToObject method
Since the version 14.1.0 you can use this method to construct a SwiftMessage object from multiple related SWIFT messages. The parser will undertake the merging process. The message types supported are:
MT700 (with the MT701 sequence messages)
MT710 (with the MT711 sequence messages)
MT720 (with the MT721 sequence messages)
The library will process tags 45B, 46B and 47B in the sequence messages and will assign the concatenated results inside the corresponding master message (i.e. MT700 or MT710 or MT720) in tags 45A, 46A and 47A respectively. Pagination (Field 27 in the resulting message) will be handled automatically. The messages passed as arguments should have correct pagination, but they do not necessarily need to be provided in the correct order as the library will sort them prior to proceeding further.

Sample parsing multiple messages
```
SwiftMsgProcessor processor = new SwiftMsgProcessor();

String messageContentMaster = //... mt700
String messageContent1 = //... mt701
String messageContent2 = //... mt701
String messageContent3 = //... mt701

//the result message will be an MT700 message holding the content of all messages passed as the arguments
SwiftMessage result = processor.ParseMsgStringsToObject(Arrays.asList(messageContentMaster, messageContent1, messageContent2, messageContent3));
```

#### BuildMsgStringFromObject method
Use this method to construct a SWIFT message before sending it to the SWIFT network. It takes as input a SwiftMessage object that has been constructed by the programmer (see next section) and performs all the necessary formatting to produce a message with valid syntax. In other words programmer just assigns the right values to the tags of the SwiftMessage object and calls the method to build the SWIFT string ready to be transmitted to the SWIFT network. To enforce or check semantic rules such as tag sequence required fields etc, the SwiftMessageValidator, another PaymentComponents component should be called. To build a valid Swift string so that it can be transmitted to the SWIFT network follow the steps below:

```
//Create the component as below
SwiftMsgProcessor SMP = new SwiftMsgProcessor();

//Create the "SwiftMessage" object to set its Tags.
SwiftMessage smObj = new SwiftMessage();

//Set all the needed tags for the message. Start with block1 and block2
smObj.setArgApplid("F");
smObj.setArgServid("01");
smObj.setArgLTaddrBlk1 ("FFFFGRA0AXXX");
smObj.setArgInoutind("I");
smObj.setArgMsgtype("103");
smObj.setArgLTaddrBlk2("UUUUGRA0AXXX");
smObj.setArgMsgprior("N");

smObj.setServiceTypeIdentifier("111");
smObj.setUniqueEndToEndTrxRef("01911b73-1f15-4932-934a-609149301922");
smObj.setPaymentControlsInformation("/AAA/someScreeningInfo");

//When finished with block1 and block2 continue with block4,
//Create vector to store the line data of tag 20.
java.util.Vector vec20 = new java.util.Vector();
//add one data line
vec20.addElement("12345678");
// Create Tag object to store name and data line for tag 20.
Tag tag20 = new Tag("20",vec20);
//add tag 20 to block4 vector
smObj.getBlock4().addElement(tag20);

//Do the same for 52A
java.util.Vector vec52A = new java.util.Vector();
vec52A.addElement("/12345"); // add first data line
vec52A.addElement("ABCDE"); // add second data line
Tag tag52A = new Tag("52A", vec52A);
smObj.getBlock4().addElement(tag52A); //add tag 52A to block4 vector

//Now build the outgoing message in a string format as SWIFT expects it.
String message = SMP.BuildMsgStringFromObject(smObj);

You are done! Now variable message contains all the Tags of the "SwiftMessage" – including the necessary carriage return linefeeds - and looks like the string below
{1:F01FFFFGRA0AXXX0000000000}{2:I103UUUUGRA0AXXXN}{3:{111:111}{121:01911b73-1f15-4932-934a-609149301922}{434:/AAA/someScreeningInfo}}{4:
:20:12345678
:52A:/12345
ABCDE
-}
```

#### SwiftMessage object
This is the heart of the component. It is the object that the parser populates with the values of the incoming message. This is the object the programmer accesses to get the values. It keeps the message information in vectors of Tag objects (see next section) and provides various methods to access and manipulate the object. There are more than 70 methods available. The programmer will need a small subset of them. Some of those are described below. The rest are straightforward in their usage and can be seen in the JavaDoc.

##### clear()
Clears all the fields of the "SwiftMessage". This includes all block vectors and message property fields.
```
smobj.clear();
```
##### displayMessage()
Displays at the standard output a text representation of the SWIFT message filled Tags
```
smobj.displayMessage();
The above will display as the output the following
Applid : F
Servid : 01
LTaddrBlk1: FFFFGRA0AXXX
Sesno : 0000
Osn : 000000
Inoutind : I
Msgtype : 103
LTaddrBlk2 : UUUUGRA0AXXX
Msgprior: N
113 : ABCD
20 : 494930/DEV
32A : 020527EUR1958,47
21 : 2222222
50 : BIODATA GJBH
50 : ZURICH
59 : S.T JANSSEN
59 : LEDEBOERSTRAAT
59 : AMSTERDAM
```
##### getArgApplid()
Returns "Application Identifier" of the SWIFT message.
```
smobj.getArgApplid();
```
The above returns "F".

##### getArgLTaddrBlk1()
Returns "LT address" of the SWIFT message (Basic Header Block (1))
```smobj.getArgLTaddrBlk1();```
The above returns "FFFFGRA0AXXX".

##### getArgMsgtype()
Returns "Message Type" of the SWIFT message
```smobj.getArgMsgtype();```
The above returns "103".

##### getBlockx()
Returns a vector with all the tags of block x of the message.
```smobj.getBlock1();```
The above returns
```[Applid[0]:F, Servid[0]:01, LTaddrBlk1[0]:FFFFGRA0AXXX, Sesno[0]:0000, Osn[0]:000000]
```
##### getMessageAsVector()
Returns a vector containing all the tags of the message in their original sequence.
```smobj.getMessageAsVector();```
The above returns
```[Applid[0]:F, Servid[0]:01, LTaddrBlk1[0]:FFFFGRA0AXXX, Sesno[0]:0000, Osn[0]:000000,Inoutind[0]:I , Msgtype[0]:103, LTaddrBlk2[0]:UUUUGRA0AXXX, Msgprior[0]:N, 3[0]:ABCD , 20[0]:494930/DEV, 32A[0]:020527EUR1958,47, 21[0]:2222222, 50[0]:BIODATA GJBH 50[1]:ZURICH, 59[0]:S.T JANSSEN 59[1]:LEDEBOERSTRAAT 29 59[2]:AMSTERDAM]
```
##### getNumbertOfTagInstances(String name)
Returns the number of instances for the specific Tag. If tag does not exist then it returns 0.
```smobj.getNumbertOfTagInstances("20");```
The above returns the integer 1.

##### getTag(String name)
```smobj.getTag("20");```
The above returns a Tag object with all the information of tag 20 of the message.
A tag may exist more than once. This method returns the first instance of the tag.

##### getTag(String tagName, int index)
Returns the specified (if exists) instance of the tag with name tagName in the message (i.e. if tag20 exists three times in a message then getTag("20",1) returns the first occurrence of tag20 and getTag("20",3) returns the third. If tag does not exist or index is greater than the maximum number of tag representation then it returns null.
```smobj.getTag("20",1);```
The above returns a Tag object having all the information of tag 20 of the message.

###### isblockEmpty(int index)
Returns true when the specific block vector has no Tags.
```isblockEmpty(4);```
The above returns false.

##### getUniqueEndToEndTrxRef()
Returns the "Unique end-to-end transaction Reference or UETR (121) " of a SWIFT message
```smobj.getUniqueEndToEndTrxRef();```

##### getPaymentControlsInformation()
Returns the "Payment Controls Info (434) " of a SWIFT message
```smobj.getPaymentControlsInformation();```

##### getServiceTypeIdentifier()
Returns the "Service Type Identifier (111) " of a SWIFT message
```smobj.getServiceTypeIdentifier();```

##### setArgLTaddrBlk1(String newArgLTaddrBlk1)
Sets the text part for "LT address" of the SWIFT message (Basic Header Block (1))
```smobj.setArgLTaddrBlk1("FFFFGRA0AXXX");```
The above sets FFFFGRA0AXXX to LTaddrBlk1 property.

##### setArgMsgtype(String newArgMsgtype)
Sets the "Message Type" of a SWIFT message
```smobj.setArgMsgtype("103");```
The above sets 103 to MsgType property.

##### setBlockx(Vector newBlockx)
Sets a vector containing all the tags of a specific block in their original sequence.
```
java.util.Vector tmpVec = new java.util.Vector();
tmpVec.addElement("I");
tmpVec.addElement("103");
tmpVec.addElement("UUUUGRA0AXXX");
tmpVec.addElement("N");
SwiftMessage smobj = new SwiftMessage();
smobj.setBlock2(tmpVec);
```
The above sets tmpVec vector to block2 property.

##### setUniqueEndToEndTrxRef(String newUniqueEndToEndTrxRef)
Sets the "Unique end-to-end transaction Reference or UETR (121) " of a SWIFT message
```smobj.setUniqueEndToEndTrxRef("01911b73-1f15-4932-934a-609149301922");```
The above sets the UETR to the 121 field of MT103.

##### setServiceTypeIdentifier(String newServiceTypeIdentifier)
Sets the "Service Type Identifier (111)" of a SWIFT message
```smobj.setServiceTypeIdentifier("111");```
The above sets the Service Type Identifier to the 111 field of Block 3.


##### setPaymentControlsInformation(String newPaymentControlsInformation)
Sets the "Payment Controls Information (434) " of a SWIFT message
```smobj.setPaymentControlsInformation("/AAA/someScreeningInfo");```
The above sets the Payment Controls Info to the 434 field of MT103.

##### toString()
Returns the String representation of this SWIFT Message
```smobj.toString();```
The above returns the string
```Applid[0]:F Servid[0]:01 LTaddrBlk1[0]:FFFFGRA0AXXX Sesno[0]:0000 Osn[0]:000000 Inoutind[0]:I Msgtype[0]:103 LTaddrBlk2[0]:UUUUGRA0AXXX Msgprior[0]:N 113[0]:ABCD 20[0]:494930/DEV 32A[0]:020527EUR1958,47 21[0]:2222222 50[0]:BIODATA GJBH 50[1]:ZURICH 59[0]:S.T JANSSEN 59[1]:LEDEBOERSTRAAT 29 59[2]:AMSTERDAM
```

##### autoReply(final String confirmationId, final String statusCode, final String reasonCode, final String forwardTo, final String settlementCode, final String clearingSystem)
Since release 20.2.0, and in the paid version only, you have the option to create an MT199, with acctepance/rejection/in process status codes, for an MT103, according to the Univeral Confirmations rule book. See the [gist here](https://gist.github.com/pc14-alexandrakis/4ec4ac8fbb8cffcbe9a7ea5605a4747d)

#### Tag object
This object is used to handle the message tags. Its properties are name and data. Name is a string containing the name of the tag (i.e. 52A). Data is a vector of strings containing the lines of tag information. It is accompanied by a number of utility methods to set and get the values or do other manipulation. Only the most important of these methods are described below. The rest are straight forward in usage and can be seen in the JavaDoc.

##### addDataLine(String line)
Adds a string containing the information of a Tag line to Data vector.
```
tag59.addDataLine("S.T JANSSEN");
tag59.addDataLine("LEDEBOERSTRAAT 29");
tag59.addDataLine("AMSTERDAM");
```

##### clearAll()
Clears the Tag properties name and data.
```tag59.clearAll();```
###### getData()
Returns a vector of strings containing the tag information. Each line of information is a vector element.
```Vector tempVec = tag59.getData();```
Now tempVec vector contains the value:
```[S.T JANSSEN, LEDEBOERSTRAAT 29, AMSTERDAM]```
##### getDataLineAt(int index)
Returns a string containing the information of the specified line of the tag. e.g. Assuming that in the original tag looks like the following
```
:59:S.T JANSSEN
LEDEBOERSTRAAT 29
AMSTERDAM
```

```
String data59l0 = tag59.getDataLineAt(0);
String data59l2 = tag59.getDataLineAt(2);
```
The above will set "S.T JANSSEN" to data59l0 and "AMSTERDAM" to data59l2 variable.

##### getName()
Returns a string containing the name of the tag.
```tag59.getName();```
The above will return "59".

##### getNumberOfDataLines()
Returns the number of lines that a tag has. Assuming that in the original tag looks like the following
```
:59:S.T JANSSEN
LEDEBOERSTRAAT 29
AMSTERDAM
```
```tag59.getNumberOfDataLines();```
The above returns the integer 3.

##### insertDataLineAt(String line, int index )
Inserts the specified line in Data vector at the specified index. Each line with an index greater or equal to the specified index is shifted upward.
Throws InvalidActionAttemptedException when the index is invalid. Assuming that in the original tag looks like the following
```
:59:S.T JANSSEN
LEDEBOERSTRAAT 29
AMSTERDAM
```
```tag59.InsertDataLineAt("HOLLAND",2);```
Now tag 59 looks like the following
```
:59:S.T JANSSEN
LEDEBOERSTRAAT 29
HOLLAND
AMSTERDAM
```

##### isEmpty()
Returns true when Tag infromation is empty.
```tag59.isEmpty();```

##### setData(Vector Data)
Sets the Data property (Vector) value.

```
Vector tmpVec = new Vector();
tmpVec.addElement("S.T JANSSEN");
tmpVec.addElement("LEDEBOERSTRAAT 29");
tmpVec.addElement("AMSTERDAM");
Tag tag59 = new Tag();
tag59.setName("59");
tag59.setData(tmpVec);
```

##### setDataLineAt(String line, int index )
Inserts the specified line in Data vector at the specified index overwriting the old line. Throws InvalidActionAttemptedException when the index is invalid.
Assuming that in the original tag looks like the following
```
:59:S.T JANSSEN
LEDEBOERSTRAAT 29
AMSTERDAM
```
```tag59.setDataLineAt("HOLLAND",1);```
Now tag 59 looks like the following
```
:59:S.T JANSSEN
HOLLAND
AMSTERDAM
```

##### setName(String newName)
Sets the Tags Name property (String) value.
```
Tag tag59 = new Tag();
tag59.setName("59");
```

##### setTag(String name, Vector data)
Sets Tags properties name and data.
```
Vector tmpVec = new Vector();
tmpVec.addElement("S.T JANSSEN");
tmpVec.addElement("LEDEBOERSTRAAT 29");
tmpVec.addElement("AMSTERDAM");
Tag tag59 = new Tag();
tag59.setTag("59",tmpVec);
```

##### toString()
Returns the String representation of this Tag.
```tag59.toString();```
The above returns
```59[0]:S.T JANSSEN 59[1]:LEDEBOERSTRAAT 29 59[2]:AMSTERDAM```

##### Tag description
**In the paid version**, you can now get each tag's description by using the `getDescription` method of the `Tag` object. So, if you have the tag object and you know the MT message it belongs to, you can use the following code
```
Tag tag20 = smObj.getTag("20");
//Get specific tag from the SwiftMessage object
System.out.println("Tag " + tag20.getName() + " " +
" " + tag20.getValueAsString());
System.out.println("Description: " +
tag20.getDescription(smObj.getArgMsgtype()));
```
and it will return the description of the tag. `MT_MESSAGE_NUMBER` is the mt message e.g. 103, 202, 700 etc. We have created a [gist here](https://gist.github.com/pc14-alexandrakis/067c319e37deec5bb357d526a953ebf4)

#### The use of Repetitive Sequences in SWIFT messages
Repetitive Sequences are used in certain SWIFT messages to enable groups of tags to be repeated more than once. The component represent these repetitive sequences by means of the `RepSeq` object. This means that a message can contain a combination of simple Tags and Repetitive Sequences, each of which is defined as optional or mandatory.
The RepSeq object includes simple Tag objects (i.e. tag 21 will be included in this RepSeq object as a simple Tag object), or even other (nested) RepSeq objects since there are SWIFT messages that are more complicated and include repetitive sequences inside repetitive sequences.
Check [here](src/main/java/com/paymentcomponents/swift/mt/BuildMT101_1.java) how the Reppetitive Sequnece B, is being added to the Swift Message Object.
##### Note
If we want to build a "Block" with a repetitive sequence named Rep1 (BlockRepSeq Rep1) which has another repetitive sequence named Rep2 inside it (BlockRepSeq Rep2), then: We create the second (inner) repetitive sequence (BlockRepSeq Rep2), we fill it with values, then we put it inside the first BlockRepSeq calling the addSubSequence method of the BlockRepSeq class, like this:
```Rep1.addSubSequence(Rep2);```

#### SwiftMsgValidator object
SwiftMsgValidator has just one method encapsulating the entire functionality needed to validate and build a SWIFT MT message.
##### Validating Objects - validateMsg method
This method takes as input a SwiftMessage object that has been manually constructed by a developer [(as here)](src/main/java/com/paymentcomponents/swift/mt/BuildMT101_1.java), performs SWIFT validations and returns a list of error messages (if the message it correct, then an empty list is returned; null is never returned). The `validateMsg` will also return a SwiftMessage object that might be sligthly different from the given one. For example the SK reorder the tags if they are in a wrong position in the message, so it's recommented to use the returned message object after the validation.  

#### Splitting Oversized Messages
Since version 14.1.0, SwiftMsgValidator is able to split oversized messages if they belong in one of the supported categories (MT700, MT710, MT720). The validator will handle splitting automatically depending on the category of the input message.
In case where the message type falls into one of the supported categories, additional sequence messages will be produced based on the input message type, and based on the following rules (applied in this order):
If one or more of fields 45A, 46A, 47A of the input message have more lines than the allowed limit, according to the SWIFT standard, each field will be broken into multiple chunks, each being encapsulated inside a sequence message.
If the message as a whole has more characters than the allowed limit per message, according to the  SWIFT standard, each of the fields 45A, 46A, 47A will begin being transferred into new sequence messages (priority is given to the tags that have the most lines) up until the point where the message is not oversized anymore.
In the case where the message will be split, pagination in field 27 will be handled automatically, and field 20 of the sequence messages will hold the value of field 20 of the original input message.
Maximum amount of sequence messages that the original input message should be split into is 8 (to result in 9 messages along with the input one); this will allow to keep the syntax of the field 27 intact. If the original message is required to be split into a number of sequence messages greater than 8, the library will still proceed with the splitting, but the validation that follows will report errors in all messages regarding field 27, where the allowed format is 1!n/1!n.
After splitting, all messages will be validated, and errors will be reported.

#### Validation Results
##### getSwiftMessage() and getSwiftMessages()
Since version 14.1.0, the SwiftValidObj object is able to return multiple message objects as a result of the validation process. The legacy getSwiftMessage method will always return the first message of the resulting array of message objects. The new method `getSwiftMessages` will return an array of SwiftMessages that are the result of the validation process. The validation error list inside the SwiftValidObj will hold errors that concern the entire array of messages.

##### getMessageIndex()
Since version 14.1.0, the ValidationError objects have a new method, getMessageIndex which returns an integer indicating the index in the input array of the message this error corresponds to.

#### XMLWriter object
It is used to serialize the SwiftMessage object to a generic XML document. It does it with the use of buildXML method. It also uses Java Document Object Model (JDOM) that is delivered with the deployment jar. These open source classes can also be downloaded from www.jdom.org.
Note: This article talks about gr.datamation.swift.swift2xml.XMLWriter. Avoid confusing with javax.sql.rowset.spi.XmlWriter.

##### buildXML method
It is a method within XMLWriter object, and is the only one used to build a generic XML document. It has three inputs:
1. The SMP object after it has been created by the SMP parser
2. String containing the path where to write the XML Document (if the String is null, the current path will be used)
3. String containing the XML filename to be created.

The method first validates the file name for syntax errors, to ensure that the XML will have alphanumeric characters.
Example
Check [here](src/main/java/com/paymentcomponents/swift/mt/ConvertMT2XML.java) how to create XMLs from an MT


### More features are included in the paid version like

[MT199 according to Universal Coonfirmations rules ](https://gist.github.com/pc14-alexandrakis/4ec4ac8fbb8cffcbe9a7ea5605a4747d)

[Tag Descriptions](https://gist.github.com/pc14-alexandrakis/067c319e37deec5bb357d526a953ebf4)

[Trade Finance Messages](https://gist.github.com/Gizpc14/2d3bd08520823399a722290c7650bba2)


