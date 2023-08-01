package model.rockwell;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.*;
import java.io.File;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import settings.Settings;

public class XmlParser {
	static boolean flag_DataTypesAlreadyReaded = false;

	public XmlParser(Settings properties) {
		super();
	}

	public void parse(ArrayList<DataType> listOfDataTypes, ArrayList<Tag> listOfTags, String path) {
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
	}

	private static ArrayList<Tag> parseTags(Document doc, ArrayList<String> stringList) {
		ArrayList<Tag> tagsList = new ArrayList<Tag>();
		NodeList tags = doc.getElementsByTagName("Tag");
		for (int i = 0; i < tags.getLength(); i++) {
			Tag tmpTag = null;
			Element tag = (Element) tags.item(i);
			if (matchSuffix(tag.getAttribute("Name"), stringList)
					&& tag.getAttribute("TagType").toLowerCase().equals("base")) {
				tmpTag = new Tag();
				tmpTag.setfName(tag.getAttribute("Name"));
				tmpTag.setfTagType(tag.getAttribute("TagType"));
				tmpTag.setDataType(tag.getAttribute("DataType"));
				tmpTag.setfDimensions(tag.getAttribute("Dimensions").trim());
				NodeList comments = tag.getElementsByTagName("Comment");
				ArrayList<Comment> commentsList = new ArrayList<Comment>();
				for (int j = 0; j < comments.getLength(); j++) {
					Element comment = (Element) comments.item(j);
					Comment tmpComment = new Comment();
					tmpComment.setfOperand(comment.getAttribute("Operand"));
					tmpComment.setfDescription(comment.getTextContent().trim());
					commentsList.add(tmpComment);
				}
				tmpTag.setfComments(commentsList);
			}
			if (tmpTag != null) {
				tagsList.add(tmpTag);
			}
		}
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
		ArrayList<DataType> dataTypesList = new ArrayList<DataType>();
		NodeList dataTypes = doc.getElementsByTagName("DataType");
		for (int i = 0; i < dataTypes.getLength(); i++) {
			DataType tmpDataType = null;
			Element dataType = (Element) dataTypes.item(i);
			tmpDataType = new DataType();
			tmpDataType.setfName(dataType.getAttribute("Name"));
			tmpDataType.setfFamily(dataType.getAttribute("Family"));
			tmpDataType.setfClass(dataType.getAttribute("Class"));
			NodeList members = dataType.getElementsByTagName("Member");
			ArrayList<Member> memberList = new ArrayList<Member>();
			for (int j = 0; j < members.getLength(); j++) {
				Element member = (Element) members.item(j);
				Member tmpMember = new Member();
				tmpMember.setfName(member.getAttribute("Name"));
				tmpMember.setfDataType(member.getAttribute("DataType"));
				tmpMember.setfTarget(member.getAttribute("Target"));
				tmpMember.setfDimension(Integer.parseInt(member.getAttribute("Dimension")));
				tmpMember.setfRadix(member.getAttribute("Radix"));
				tmpMember.setfHidden(Boolean.parseBoolean(member.getAttribute("Hidden")));
				tmpMember.setfExternalAccess(member.getAttribute("ExternalAccess"));
				NodeList descriptions = member.getElementsByTagName("Description");
				for (int k = 0; k < descriptions.getLength(); k++) {
					Element description = (Element) descriptions.item(k);
					tmpMember.setfDescription(description.getTextContent().trim());
				}
				memberList.add(tmpMember);
			}
			tmpDataType.setfMembers(memberList);
			dataTypesList.add(tmpDataType);
		}
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
			tmpTag = new Tag();
			tmpTag.setfName(tag.getAttribute("Name"));
			tmpTag.setfTagType(tag.getAttribute("TagType"));
			tmpTag.setDataType(tag.getAttribute("DataType"));
			tmpTag.setfDimensions(tag.getAttribute("Dimensions").trim());
			NodeList comments = tag.getElementsByTagName("Comment");
			ArrayList<Comment> commentsList = new ArrayList<Comment>();
			for (int j = 0; j < comments.getLength(); j++) {
				Element comment = (Element) comments.item(j);
				Comment tmpComment = new Comment();
				tmpComment.setfOperand(comment.getAttribute("Operand"));
				tmpComment.setfDescription(comment.getTextContent().trim());
				commentsList.add(tmpComment);
			}
			tmpTag.setfComments(commentsList);
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
						for (int o = 0; o < nlElem.getLength(); o++) {
							getStructure(s, (Element) nlElem.item(o));
						}
					}
				}
				am.getElements().add(_e);
			}
			s.getDataMembers().add(am);
		}

		NodeList nlsmMembers = structureElement.getElementsByTagName("StructureMember");
		for (int u = 0; u < nlsmMembers.getLength(); u++) {
			@SuppressWarnings("unused")
			Element structureMemberElement = (Element) nlsmMembers.item(u);
			StructureMember sm = new StructureMember();
			// ricorro Structure
			s.getDataMembers().add(sm);
		}
	}

}
