package converter.xml;

import converter.json.JsonElement;

public class XmlConverter {
    public JsonElement convertToJson(XmlElement element) {
//        final String elementName = element.name;
//        final String elementValue = element.value;
//        final List<XmlAttribute> elementAttributes = element.attributes;
//        if (elementAttributes == null) {
//            return new JsonElement(elementName, elementValue);
//        } else {
//            final List<JsonEntity> entities = new ArrayList<>(elementAttributes.size() + 1);
//            for (XmlAttribute attribute : elementAttributes) {
//                entities.add(new JsonEntity("@" + attribute.name, attribute.value));
//            }
//            entities.add(new JsonEntity("#" + elementName, elementValue));
//            return new JsonElement(elementName, new JsonElement(entities));
//        }
        return null;
    }
}