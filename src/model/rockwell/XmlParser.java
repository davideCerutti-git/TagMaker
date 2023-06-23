package model.rockwell;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import settings.Settings;

public class XmlParser {

	// private final static String path =
	// "C:\\Users\\d.cerutti.KEY\\Desktop\\prove\\rockwell\\asdasdas.L5X.xml";
	static boolean flag_DataTypesAlreadyReaded = false;
	private static Settings properties;

	public XmlParser(Settings properties2) {
		super();
		this.properties = properties2;
	}

	public void parse(ArrayList<DataType> listOfDataTypes, ArrayList<Tag> listOfTags, String path) {
		ModelRockwell.logRock.info("start method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			ArrayList<String> suffixeslist = ModelRockwell.getListSuffixes();
			listOfTags.addAll(parseTags(doc, suffixeslist));
			listOfDataTypes.addAll(parseDataTypes(doc));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		ModelRockwell.logRock.info("end method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	private static ArrayList<Tag> parseTags(Document doc, ArrayList<String> stringList) {
		ModelRockwell.logRock.info("start method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<Tag> tagsList = new ArrayList<Tag>();
		NodeList tags = doc.getElementsByTagName("Tag");
		for (int i = 0; i < tags.getLength(); i++) {
			Tag tmpTag = null;
			Element tag = (Element) tags.item(i);
			if (matchSuffix(tag.getAttribute("Name"), stringList)&&tag.getAttribute("TagType").toLowerCase().equals("base")) {
				ModelRockwell.logRock.info("Name: " + tag.getAttribute("Name"));
				tmpTag = new Tag();
				tmpTag.setfName(tag.getAttribute("Name"));
				tmpTag.setfTagType(tag.getAttribute("TagType"));
				tmpTag.setDataType(tag.getAttribute("DataType"));
				tmpTag.setfDimensions(tag.getAttribute("Dimensions").trim());
				// System.out.println("Radix: " + tag.getAttribute("Radix"));
				// System.out.println("Constant: " + tag.getAttribute("Constant"));
				// System.out.println("ExternalAccess: " + tag.getAttribute("ExternalAccess"));
				// System.out.println("Dimensions: " + tag.getAttribute("Dimensions").trim());
				NodeList comments = tag.getElementsByTagName("Comment");
				ArrayList<Comment> commentsList = new ArrayList<Comment>();
				for (int j = 0; j < comments.getLength(); j++) {
					Element comment = (Element) comments.item(j);
					// System.out.print("\tComment ");
					Comment tmpComment = new Comment();
					// System.out.println("Operand: " + comment.getAttribute("Operand"));
					tmpComment.setfOperand(comment.getAttribute("Operand"));
					// System.out.println("\tString: " + comment.getTextContent().trim());
					tmpComment.setfDescription(comment.getTextContent().trim());
					commentsList.add(tmpComment);
				}
				tmpTag.setfComments(commentsList);
			}

			if (tmpTag != null) {
				tagsList.add(tmpTag);
			}
		}
		ModelRockwell.logRock.info("end method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
		return tagsList;
	}

	private static boolean matchSuffix(String attribute, ArrayList<String> stringList) {
		for (String s : stringList) {
			if (attribute.endsWith(s))
				return true;
		}
		return false;
	}

	private static ArrayList<DataType> parseDataTypes(Document doc) {
		ModelRockwell.logRock.info("start method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<DataType> dataTypesList = new ArrayList<DataType>();
		NodeList dataTypes = doc.getElementsByTagName("DataType");
		// System.out.println("Found DataTypes\n");
		for (int i = 0; i < dataTypes.getLength(); i++) {
			DataType tmpDataType = null;
			Element dataType = (Element) dataTypes.item(i);
			// System.out.println("Found DataType");
			tmpDataType = new DataType();
			// System.out.println("Name: " + dataType.getAttribute("Name"));
			tmpDataType.setfName(dataType.getAttribute("Name"));
			// System.out.println("Family: " + dataType.getAttribute("Family"));
			tmpDataType.setfFamily(dataType.getAttribute("Family"));
			// System.out.println("Class: " + dataType.getAttribute("Class"));
			tmpDataType.setfClass(dataType.getAttribute("Class"));
			NodeList members = dataType.getElementsByTagName("Member");
			ArrayList<Member> memberList = new ArrayList<Member>();
			for (int j = 0; j < members.getLength(); j++) {
				Element member = (Element) members.item(j);
				// System.out.println("\tFound Member " + j);
				Member tmpMember = new Member();
				// System.out.println("\tName: " + member.getAttribute("Name"));
				tmpMember.setfName(member.getAttribute("Name"));
				// System.out.println("\tDataType: " + member.getAttribute("DataType"));
				tmpMember.setfDataType(member.getAttribute("DataType"));
				// System.out.println("\tDataType: " + member.getAttribute("Target"));
				tmpMember.setfTarget(member.getAttribute("Target"));
				// System.out.println("\tDimension: " + member.getAttribute("Dimension"));
				tmpMember.setfDimension(Integer.parseInt(member.getAttribute("Dimension")));
				// System.out.println("\tRadix: " + member.getAttribute("Radix"));
				tmpMember.setfRadix(member.getAttribute("Radix"));
				// System.out.println("\tHidden: " + member.getAttribute("Hidden"));
				tmpMember.setfHidden(Boolean.parseBoolean(member.getAttribute("Hidden")));
				// System.out.println("\tExternalAccess: " +
				// member.getAttribute("ExternalAccess"));
				tmpMember.setfExternalAccess(member.getAttribute("ExternalAccess"));
				// System.out.println("\tTest: " + member.getAttribute("Test"));
				NodeList descriptions = member.getElementsByTagName("Description");
				for (int k = 0; k < descriptions.getLength(); k++) {
					Element description = (Element) descriptions.item(k);
					// System.out.println("\t\tFound Description: " +
					// description.getTextContent().trim());
					tmpMember.setfDescription(description.getTextContent().trim());
				}
				memberList.add(tmpMember);
			}
			tmpDataType.setfMembers(memberList);
			dataTypesList.add(tmpDataType);
		}
		ModelRockwell.logRock.info("end method: "+Thread.currentThread().getStackTrace()[1].getMethodName());
		return dataTypesList;
	}

	public void parseRaw(ArrayList<Tag> listOfTags, String path) {
		try {
			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			listOfTags.addAll(parseTags_DOM_Raw(doc));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	private static ArrayList<Tag> parseTags_DOM_Raw(Document doc) {
		ArrayList<Tag> tagsList = new ArrayList<Tag>();
		NodeList tags = doc.getElementsByTagName("Tag");
		for (int i = 0; i < tags.getLength(); i++) {
			Tag tmpTag = null;
			Element tag = (Element) tags.item(i);
			// if (matchSuffix(tag.getAttribute("Name"), stringList)) {
			// System.out.println("Found Tag");
			tmpTag = new Tag();
			// System.out.println("Name: " + tag.getAttribute("Name"));
			tmpTag.setfName(tag.getAttribute("Name"));
			// System.out.println("TagType: " + tag.getAttribute("TagType"));
			tmpTag.setfTagType(tag.getAttribute("TagType"));
			// System.out.println("DataType: " + tag.getAttribute("DataType"));
			tmpTag.setDataType(tag.getAttribute("DataType"));
			tmpTag.setfDimensions(tag.getAttribute("Dimensions").trim());
			// System.out.println("Radix: " + tag.getAttribute("Radix"));
			// System.out.println("Constant: " + tag.getAttribute("Constant"));
			// System.out.println("ExternalAccess: " + tag.getAttribute("ExternalAccess"));
			// System.out.println("Dimensions: " + tag.getAttribute("Dimensions").trim());
			NodeList comments = tag.getElementsByTagName("Comment");
			ArrayList<Comment> commentsList = new ArrayList<Comment>();
			for (int j = 0; j < comments.getLength(); j++) {
				Element comment = (Element) comments.item(j);
				// System.out.print("\tComment ");
				Comment tmpComment = new Comment();
				// System.out.println("Operand: " + comment.getAttribute("Operand"));
				tmpComment.setfOperand(comment.getAttribute("Operand"));
				// System.out.println("\tString: " + comment.getTextContent().trim());
				tmpComment.setfDescription(comment.getTextContent().trim());
				commentsList.add(tmpComment);
			}
			tmpTag.setfComments(commentsList);
			// }

			NodeList data = tag.getElementsByTagName("Data");
			for (int k = 0; k < data.getLength(); k++) {
				Element dataElement = (Element) data.item(k);
				if (dataElement.getAttribute("Format").equals("Decorated")) {
					Data d = new Data();
					Structure s = new Structure();
					s.setDataMembers(new ArrayList<DataMember>());
					NodeList structureElements = dataElement.getElementsByTagName("Structure");
					Element structureElement = (Element) structureElements.item(0);
					
					getStructure(s, structureElement);

					d.setStructure(s);
					ArrayList<Data> arrayList = new ArrayList<Data>();
					arrayList.add(d);
					tmpTag.setfData(arrayList);
				}
			}

			if (tmpTag != null) {
				tagsList.add(tmpTag);
			}
		}
		// ModelRockwell.logRock.debug(tagsList);
		return tagsList;
	}

	/**
	 * @param s
	 * @param structureElement
	 */
	private static void getStructure(Structure s, Element structureElement) {
		NodeList nldvmMembers = structureElement.getElementsByTagName("DataValueMember");
		for (int g = 0; g < nldvmMembers.getLength(); g++) {
			Element element = (Element) nldvmMembers.item(g);
			DataValueMember dvm = new DataValueMember();
			dvm.setName(element.getAttribute("Name"));
			System.out.println(dvm.getName());
			s.getDataMembers().add(dvm);
		}

		NodeList nlamMembers = structureElement.getElementsByTagName("ArrayMember");
		for (int t = 0; t < nlamMembers.getLength(); t++) {
			Element arrayMemberElement = (Element) nldvmMembers.item(t);
			ArrayMember am = new ArrayMember();
			am.setName(arrayMemberElement.getAttribute("Name"));
			am.setDimensions(Integer.parseInt(arrayMemberElement.getAttribute("Dimensions")));
			NodeList nlamElements = arrayMemberElement.getElementsByTagName("Element");
			for (int y = 0; y < nlamElements.getLength(); y++) {
				Element_ _e = new Element_();
				Element el = (Element) nlamElements.item(y);
				_e.setIndex(el.getAttribute("Index"));
				NodeList nlElem = el.getElementsByTagName("Structure");
				if (nlElem != null) {
					if (nlElem.getLength() > 0) {
						// ricorro Structure
						for(int o=0;o<nlElem.getLength();o++) {
							getStructure(s, (Element)nlElem.item(o));
						}
					}
				}
				am.getElements().add(_e);
			}
			System.out.println(am.getName());
			s.getDataMembers().add(am);
		}
		
		NodeList nlsmMembers = structureElement.getElementsByTagName("StructureMember");
		for (int u = 0; u < nlsmMembers.getLength(); u++) {
			Element structureMemberElement = (Element) nlsmMembers.item(u);
			StructureMember sm=new StructureMember();
			//ricorro Structure
			s.getDataMembers().add(sm);
		}
	}

//	private static void parseRSLogix5000Content(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT && !flag_DataTypesAlreadyReaded) {
//		String tagName = reader.getLocalName();
//		if (tagName.equals("Controller")) {
//			System.out.println("Found Controller");
//			parseController(reader);
//		} else {
//			throw new XMLStreamException("Expected <Controller> element, found: " + tagName, reader.getLocation());
//		}
//	}
//}
//private static void parseController(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT) {
//		String tagName = reader.getLocalName();
//		// scorro il file xml perchè solitamente ci sono altri elementi in
//		// questo punto (Security, SafetyInfo, ecc..)
//		while (!tagName.equals("DataTypes") && !flag_DataTypesAlreadyReaded) {
//			reader.nextTag();
//			tagName = reader.getLocalName();
//		}
//		if (tagName.equals("DataTypes")) {
//			System.out.println("Found DataTypes");
//			parseDataTypes(reader);
//			flag_DataTypesAlreadyReaded = true;
//			return;
//		} else {
//			throw new XMLStreamException("Expected <DataTypes> element, found: " + tagName, reader.getLocation());
//		}
//	}
//}
//private static void parseDataTypes(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT) {
//		String tagName = reader.getLocalName();
//		if (tagName.equals("DataType")) {
//			System.out.print("Found DataType: ");
//			System.out.println(reader.getAttributeValue(null, "Name"));
//			parseDataType(reader);
//		} else {
//			throw new XMLStreamException("Expected <DataType> element, found: " + tagName, reader.getLocation());
//		}
//	}
//}
//
//private static void parseDataType(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT) {
//		String tagName = reader.getLocalName();
//		if (tagName.equals("Members")) {
//			System.out.println("Found Members");
//			parseMembers(reader);
//		} else if (tagName.equals("Description")) {
//			System.out.println("Found DataType Description ");
//			parseDescription(reader);
//		} else {
//			throw new XMLStreamException("Expected <Description> element, found: " + tagName, reader.getLocation());
//		}
//	}
//
//}
//
//private static void parseMembers(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT) {
//		String tagName = reader.getLocalName();
//		if (tagName.equals("Member")) {
//			System.out.print("Found Member: ");
//			System.out.println(reader.getAttributeValue(null, "Name"));
//			parseMember(reader);
//		} else {
//			throw new XMLStreamException("Expected <Members> element, found: " + tagName, reader.getLocation());
//		}
//	}
//}
//
//private static void parseMember(XMLStreamReader reader) throws XMLStreamException {
//	while (reader.nextTag() != XMLStreamConstants.END_ELEMENT) {
//		String tagName = reader.getLocalName();
//		if (tagName.equals("Description")) {
//			System.out.print("Found Description: ");
//			parseDescription(reader);
//		} else {
//			throw new XMLStreamException("Expected <Description> element, found: " + tagName, reader.getLocation());
//		}
//	}
//}
//private static void parseDescription(XMLStreamReader reader) throws XMLStreamException {
//	reader.next();
//	if (reader.getEventType() == XMLStreamConstants.CHARACTERS
//			|| reader.getEventType() == XMLStreamConstants.CDATA) {
//		reader.next();
//		System.out.println(reader.getText());
//	}
//	reader.nextTag();
//}

//private static void readDataTypes() throws FileNotFoundException, XMLStreamException {
//	
//	XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
//	XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
//
//	while (reader.hasNext()) {
//
//		XMLEvent nextEvent = reader.nextEvent();
//
//		if (nextEvent.isStartElement()) {
//
//			StartElement startElement = nextEvent.asStartElement();
//			if (startElement.getName().getLocalPart().equals("DataType")) {
//				// ...
//
//				System.out.println("new DataType");
//				String name = startElement.getAttributeByName(new QName("Name")).getValue();
//				System.out.println(name);
//				String family = startElement.getAttributeByName(new QName("Family")).getValue();
//				System.out.println(family);
//				String classe = startElement.getAttributeByName(new QName("Class")).getValue();
//				System.out.println(classe);
//
//				while (reader.hasNext()) {
//					nextEvent = reader.nextEvent();
//					if (nextEvent.isStartElement()) {
//						startElement = nextEvent.asStartElement();
//						if (startElement.getName().getLocalPart().equals("Members")) {
//
//							System.out.println("Members found");
//							while (reader.hasNext()) {
//								nextEvent = reader.nextEvent();
//								if (nextEvent.isStartElement()) {
//									startElement = nextEvent.asStartElement();
//									if (startElement.getName().getLocalPart().equals("Member")) {
//
//										System.out.println("\nnew Member found");
//										name = startElement.getAttributeByName(new QName("Name")).getValue();
//										System.out.println(name);
//										String dataType = startElement.getAttributeByName(new QName("DataType"))
//												.getValue();
//										System.out.println(dataType);
//										String dimension = startElement.getAttributeByName(new QName("Dimension"))
//												.getValue();
//										System.out.println(dimension);
//										String radix = startElement.getAttributeByName(new QName("Radix"))
//												.getValue();
//										System.out.println(radix);
//										String hidden = startElement.getAttributeByName(new QName("Hidden"))
//												.getValue();
//										System.out.println(hidden);
//										String externalAccess = startElement
//												.getAttributeByName(new QName("ExternalAccess")).getValue();
//										System.out.println(externalAccess);
//									}
//								}
//								if (nextEvent.isEndElement()
//										&& nextEvent.asEndElement().getName().getLocalPart().equals("Member")) {
//									System.out.println("end Member");
//								}
//							}
//						}
//					}
//					if (nextEvent.isEndElement()
//							&& nextEvent.asEndElement().getName().getLocalPart().equals("Members")) {
//						System.out.println("end Members");
//					}
//				}
//
//			}
//
//		}
//		if (nextEvent.isEndElement() && nextEvent.asEndElement().getName().getLocalPart().equals("DataType")) {
//			System.out.println("end DataType");
//		}
//	}
//}

//static void readTags_STAX() {
//	XMLStreamReader reader = null;
//	try {
//		XMLInputFactory factory = XMLInputFactory.newFactory();
//		reader = factory.createXMLStreamReader(new FileInputStream(path));
//		reader.nextTag(); // Position on root element
//		String tagName = reader.getLocalName();
//		if (!tagName.equals("RSLogix5000Content"))
//			throw new XMLStreamException("Expected <RSLogix5000Content> element, found: " + tagName,
//					reader.getLocation());
//		parseRSLogix5000Content(reader);
//	} catch (XMLStreamException e) {
//		e.printStackTrace();
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
//	} finally {
//		try {
//			reader.close();
//		} catch (XMLStreamException e) {
//			e.printStackTrace();
//		}
//	}
//}
}
