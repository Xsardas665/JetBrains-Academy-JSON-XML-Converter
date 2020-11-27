package converter.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlDocumentParser {
    public static final String START = "<";
    public static final String END = ">";
    public static final String SLASH = "/";
    public static final String START_SLASH = START + SLASH;
    public static final String SLASH_END = SLASH + END;

    public XmlElement parse(String input) {
        final List<String> parts = split(input);
        final List<Raw> raws = raw(parts);
        final List<?> foo = convert(raws);
        return null;
    }

    private List<String> split(String input) {
        final List<String> substrings = new ArrayList<>();
        String ch = END;
        int begin = 0;
        int end;
        while ((end = input.indexOf(ch, begin)) != -1) {
            final boolean isStart = START.equals(ch);
            ch = isStart ? END : START;
            final String substring = input.substring(begin, (begin = isStart ? end : end + 1));
            if (!substring.isEmpty()) {
                substrings.add(substring.trim());
            }
        }
        return substrings;
    }

    private List<Raw> raw(List<String> parts) {
        final List<Raw> raws = new ArrayList<>(parts.size());
        for (String part : parts) {
            if (isTag(part)) {
                final String name = getTagName(part);
                if (isClosingTag(part)) {
                    raws.add(Raw.closing(name, part));
                } else if (isEmptyTag(part)) {
                    raws.add(Raw.empty(name, part));
                } else {
                    raws.add(Raw.open(name, part));
                }
            } else {
                raws.add(Raw.value(part));
            }
        }
        return raws;
    }

    private List<?> convert(List<Raw> rawList) {
        for (int i = 0; i < rawList.size(); i++) {
            final Raw raw = rawList.get(i);
            if (raw.type == RawType.EMPTY) {
                if (hasAttributes(raw.value)) {
                    System.out.println(new XmlElement(raw.name, getAttributes(raw)));
                } else {
                    System.out.println(new XmlElement(raw.name));
                }
            } else if (raw.type == RawType.OPEN) {
                final Result result = convert(rawList, i + 1);
                if (hasAttributes(raw.value)) {
                    System.out.println(new XmlElement(raw.name, result.value, getAttributes(raw)));
                } else {
                    System.out.println(new XmlElement(raw.name, result.value));
                }
                i = result.to;
            }
        }
        return null;
    }

    private Result convert(List<Raw> rawList, int from) {
        for (int i = from; i < rawList.size(); i++) {
            final Raw raw = rawList.get(i);
            if (raw.type == RawType.EMPTY) {
                if (hasAttributes(raw.value)) {
                    System.out.println(new XmlElement(raw.name, getAttributes(raw)));
                } else {
                    System.out.println(new XmlElement(raw.name));
                }
            } else if (raw.type == RawType.OPEN) {
                final Result result = convert(rawList, i + 1);
                if (hasAttributes(raw.value)) {
                    System.out.println(new XmlElement(raw.name, result.value, getAttributes(raw)));
                } else {
                    System.out.println(new XmlElement(raw.name, result.value));
                }
                i = result.to;
            }
        }
        return null;
    }

    private List<XmlAttribute> getAttributes(Raw raw) {
        final int beginIndex = START.length() + raw.name.length();
        final String rawValue = raw.value;
        final int rawLength = rawValue.length();
        final int endIndex = rawValue.endsWith(SLASH_END)
                ? rawLength - SLASH_END.length()
                : rawLength - END.length();
        final String value = rawValue.substring(beginIndex, endIndex);
        final Matcher matcher = Pattern.compile("\\w+(\\s*=\\s*\".*\")?").matcher(value);
        final ArrayList<XmlAttribute> attributes = new ArrayList<>();
        while (matcher.find()) {
            final String rawAttribute = matcher.group();
            attributes.add(getAttribute(rawAttribute));
        }
        return attributes;
    }

    private XmlAttribute getAttribute(String attribute) {
        if (attribute.matches("\\w+\\s*=.*")) {
            final int index = attribute.indexOf("=");
            final String name = attribute.substring(0, index).trim();
            final String quotedValue = attribute.substring(index + 1).trim();
            final String value = quotedValue.substring(1, quotedValue.length() - 1);
            return new XmlAttribute(name, value);
        } else {
            return new XmlAttribute(attribute.trim());
        }
    }

    private boolean isTag(String tag) {
        return tag.startsWith(START) && tag.endsWith(END);
    }

    private boolean isEmptyTag(String tag) {
        return tag.startsWith(START) && tag.endsWith(SLASH_END);
    }

    private boolean isClosingTag(String tag) {
        return tag.startsWith(START_SLASH) && tag.endsWith(END);
    }

    private boolean hasAttributes(String tag) {
        return tag.matches("<\\w+\\s+\\w.*/?>");
    }

    private String getTagName(String tag) {
        final Matcher matcher = Pattern.compile("(\\s|" + SLASH_END + "|" + END + ")").matcher(tag);
        if (matcher.find()) {
            final int begin = tag.startsWith(START_SLASH) ? START_SLASH.length() : START.length();
            final int end = matcher.start();
            return tag.substring(begin, end);
        }
        throw new IllegalArgumentException("Illegal tag: " + tag);
    }

    private enum RawType {
        OPEN, CLOSING, EMPTY, VALUE
    }

    private static class Result {
        final int from;
        final int to;
        final XmlValue value;

        Result(int from, int to, XmlValue value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }
    }

    private static class Raw {
        final String name;
        final String value;
        final RawType type;

        Raw(String name, String value, RawType type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        static Raw open(String name, String value) {
            return new Raw(name, value, RawType.OPEN);
        }

        static Raw closing(String name, String value) {
            return new Raw(name, value, RawType.CLOSING);
        }

        static Raw empty(String name, String value) {
            return new Raw(name, value, RawType.EMPTY);
        }

        static Raw value(String value) {
            return new Raw("", value, RawType.VALUE);
        }
    }
}