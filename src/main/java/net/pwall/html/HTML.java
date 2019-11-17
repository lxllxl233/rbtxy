/*
 * @(#) HTML.java
 */

package net.pwall.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import net.pwall.util.AbstractCharMapper;
import net.pwall.util.CharMapper;
import net.pwall.util.CharMapperEntry;
import net.pwall.util.CharUnmapper;
import net.pwall.util.Strings;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A set of static methods to assist in parsing and writing HTML.
 *
 * @author Peter Wall
 */
public class HTML {

    private static final char backslash = '\\';
    private static String newlineString = null;

    // these are the character entity references from 0xA0 to 0xFF
    public static final String[] baseEntities = {
      "&nbsp;", "&iexcl;", "&cent;", "&pound;", "&curren;", "&yen;", "&brvbar;", "&sect;",
      "&uml;", "&copy;", "&ordf;", "&laquo;", "&not;", "&shy;", "&reg;", "&macr;",
      "&deg;", "&plusmn;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;", "&middot;",
      "&cedil;", "&sup1;", "&ordm;", "&raquo;", "&frac14;", "&frac12;", "&frac34;", "&iquest;",
      "&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;", "&AElig;", "&Ccedil;",
      "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;", "&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;",
      "&ETH;", "&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "&times;",
      "&Oslash;", "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;", "&szlig;",
      "&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;", "&aring;", "&aelig;", "&ccedil;",
      "&egrave;", "&eacute;", "&ecirc;", "&euml;", "&igrave;", "&iacute;", "&icirc;", "&iuml;",
      "&eth;", "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;", "&ouml;", "&divide;",
      "&oslash;", "&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;", "&thorn;", "&yuml;"
    };

    // these entities are non-contiguous, so they are looked up by a binary search
    public static final CharMapperEntry[] mappedEntities = {
        new CharMapperEntry(0x0152, "&OElig;"),
        new CharMapperEntry(0x0153, "&oelig;"),
        new CharMapperEntry(0x0160, "&Scaron;"),
        new CharMapperEntry(0x0161, "&scaron;"),
        new CharMapperEntry(0x0178, "&Yuml;"),
        new CharMapperEntry(0x0192, "&fnof;"),
        new CharMapperEntry(0x02C6, "&circ;"),
        new CharMapperEntry(0x02DC, "&tilde;"),
        new CharMapperEntry(0x0391, "&Alpha;"),
        new CharMapperEntry(0x0392, "&Beta;"),
        new CharMapperEntry(0x0393, "&Gamma;"),
        new CharMapperEntry(0x0394, "&Delta;"),
        new CharMapperEntry(0x0395, "&Epsilon;"),
        new CharMapperEntry(0x0396, "&Zeta;"),
        new CharMapperEntry(0x0397, "&Eta;"),
        new CharMapperEntry(0x0398, "&Theta;"),
        new CharMapperEntry(0x0399, "&Iota;"),
        new CharMapperEntry(0x039A, "&Kappa;"),
        new CharMapperEntry(0x039B, "&Lambda;"),
        new CharMapperEntry(0x039C, "&Mu;"),
        new CharMapperEntry(0x039D, "&Nu;"),
        new CharMapperEntry(0x039E, "&Xi;"),
        new CharMapperEntry(0x039F, "&Omicron;"),
        new CharMapperEntry(0x03A0, "&Pi;"),
        new CharMapperEntry(0x03A1, "&Rho;"),
        new CharMapperEntry(0x03A3, "&Sigma;"),
        new CharMapperEntry(0x03A4, "&Tau;"),
        new CharMapperEntry(0x03A5, "&Upsilon;"),
        new CharMapperEntry(0x03A6, "&Phi;"),
        new CharMapperEntry(0x03A7, "&Chi;"),
        new CharMapperEntry(0x03A8, "&Psi;"),
        new CharMapperEntry(0x03A9, "&Omega;"),
        new CharMapperEntry(0x03B1, "&alpha;"),
        new CharMapperEntry(0x03B2, "&beta;"),
        new CharMapperEntry(0x03B3, "&gamma;"),
        new CharMapperEntry(0x03B4, "&delta;"),
        new CharMapperEntry(0x03B5, "&epsilon;"),
        new CharMapperEntry(0x03B6, "&zeta;"),
        new CharMapperEntry(0x03B7, "&eta;"),
        new CharMapperEntry(0x03B8, "&theta;"),
        new CharMapperEntry(0x03B9, "&iota;"),
        new CharMapperEntry(0x03BA, "&kappa;"),
        new CharMapperEntry(0x03BB, "&lambda;"),
        new CharMapperEntry(0x03BC, "&mu;"),
        new CharMapperEntry(0x03BD, "&nu;"),
        new CharMapperEntry(0x03BE, "&xi;"),
        new CharMapperEntry(0x03BF, "&omicron;"),
        new CharMapperEntry(0x03C0, "&pi;"),
        new CharMapperEntry(0x03C1, "&rho;"),
        new CharMapperEntry(0x03C2, "&sigmaf;"),
        new CharMapperEntry(0x03C3, "&sigma;"),
        new CharMapperEntry(0x03C4, "&tau;"),
        new CharMapperEntry(0x03C5, "&upsilon;"),
        new CharMapperEntry(0x03C6, "&phi;"),
        new CharMapperEntry(0x03C7, "&chi;"),
        new CharMapperEntry(0x03C8, "&psi;"),
        new CharMapperEntry(0x03C9, "&omega;"),
        new CharMapperEntry(0x03D1, "&thetasym;"),
        new CharMapperEntry(0x03D2, "&upsih;"),
        new CharMapperEntry(0x03D6, "&piv;"),
        new CharMapperEntry(0x2002, "&ensp;"),
        new CharMapperEntry(0x2003, "&emsp;"),
        new CharMapperEntry(0x2009, "&thinsp;"),
        new CharMapperEntry(0x200C, "&zwnj;"),
        new CharMapperEntry(0x200D, "&zwj;"),
        new CharMapperEntry(0x200E, "&lrm;"),
        new CharMapperEntry(0x200F, "&rlm;"),
        new CharMapperEntry(0x2013, "&ndash;"),
        new CharMapperEntry(0x2014, "&mdash;"),
        new CharMapperEntry(0x2018, "&lsquo;"),
        new CharMapperEntry(0x2019, "&rsquo;"),
        new CharMapperEntry(0x201A, "&sbquo;"),
        new CharMapperEntry(0x201C, "&ldquo;"),
        new CharMapperEntry(0x201D, "&rdquo;"),
        new CharMapperEntry(0x201E, "&bdquo;"),
        new CharMapperEntry(0x2020, "&dagger;"),
        new CharMapperEntry(0x2021, "&Dagger;"),
        new CharMapperEntry(0x2022, "&bull;"),
        new CharMapperEntry(0x2026, "&hellip;"),
        new CharMapperEntry(0x2030, "&permil;"),
        new CharMapperEntry(0x2032, "&prime;"),
        new CharMapperEntry(0x2033, "&Prime;"),
        new CharMapperEntry(0x2039, "&lsaquo;"),
        new CharMapperEntry(0x203A, "&rsaquo;"),
        new CharMapperEntry(0x203E, "&oline;"),
        new CharMapperEntry(0x2044, "&frasl;"),
        new CharMapperEntry(0x20AC, "&euro;"),
        new CharMapperEntry(0x2111, "&image;"),
        new CharMapperEntry(0x2118, "&weierp;"),
        new CharMapperEntry(0x211C, "&real;"),
        new CharMapperEntry(0x2122, "&trade;"),
        new CharMapperEntry(0x2135, "&alefsym;"),
        new CharMapperEntry(0x2190, "&larr;"),
        new CharMapperEntry(0x2191, "&uarr;"),
        new CharMapperEntry(0x2192, "&rarr;"),
        new CharMapperEntry(0x2193, "&darr;"),
        new CharMapperEntry(0x2194, "&harr;"),
        new CharMapperEntry(0x21B5, "&crarr;"),
        new CharMapperEntry(0x21D0, "&lArr;"),
        new CharMapperEntry(0x21D1, "&uArr;"),
        new CharMapperEntry(0x21D2, "&rArr;"),
        new CharMapperEntry(0x21D3, "&dArr;"),
        new CharMapperEntry(0x21D4, "&hArr;"),
        new CharMapperEntry(0x2200, "&forall;"),
        new CharMapperEntry(0x2202, "&part;"),
        new CharMapperEntry(0x2203, "&exist;"),
        new CharMapperEntry(0x2205, "&empty;"),
        new CharMapperEntry(0x2207, "&nabla;"),
        new CharMapperEntry(0x2208, "&isin;"),
        new CharMapperEntry(0x2209, "&notin;"),
        new CharMapperEntry(0x220B, "&ni;"),
        new CharMapperEntry(0x220F, "&prod;"),
        new CharMapperEntry(0x2211, "&sum;"),
        new CharMapperEntry(0x2212, "&minus;"),
        new CharMapperEntry(0x2217, "&lowast;"),
        new CharMapperEntry(0x221A, "&radic;"),
        new CharMapperEntry(0x221D, "&prop;"),
        new CharMapperEntry(0x221E, "&infin;"),
        new CharMapperEntry(0x2220, "&ang;"),
        new CharMapperEntry(0x2227, "&and;"),
        new CharMapperEntry(0x2228, "&or;"),
        new CharMapperEntry(0x2229, "&cap;"),
        new CharMapperEntry(0x222A, "&cup;"),
        new CharMapperEntry(0x222B, "&int;"),
        new CharMapperEntry(0x2234, "&there4;"),
        new CharMapperEntry(0x223C, "&sim;"),
        new CharMapperEntry(0x2245, "&cong;"),
        new CharMapperEntry(0x2248, "&asymp;"),
        new CharMapperEntry(0x2260, "&ne;"),
        new CharMapperEntry(0x2261, "&equiv;"),
        new CharMapperEntry(0x2264, "&le;"),
        new CharMapperEntry(0x2265, "&ge;"),
        new CharMapperEntry(0x2282, "&sub;"),
        new CharMapperEntry(0x2283, "&sup;"),
        new CharMapperEntry(0x2284, "&nsub;"),
        new CharMapperEntry(0x2286, "&sube;"),
        new CharMapperEntry(0x2287, "&supe;"),
        new CharMapperEntry(0x2295, "&oplus;"),
        new CharMapperEntry(0x2297, "&otimes;"),
        new CharMapperEntry(0x22A5, "&perp;"),
        new CharMapperEntry(0x22C5, "&sdot;"),
        new CharMapperEntry(0x2308, "&lceil;"),
        new CharMapperEntry(0x2309, "&rceil;"),
        new CharMapperEntry(0x230A, "&lfloor;"),
        new CharMapperEntry(0x230B, "&rfloor;"),
        new CharMapperEntry(0x2329, "&lang;"),
        new CharMapperEntry(0x232A, "&rang;"),
        new CharMapperEntry(0x25CA, "&loz;"),
        new CharMapperEntry(0x2660, "&spades;"),
        new CharMapperEntry(0x2663, "&clubs;"),
        new CharMapperEntry(0x2665, "&hearts;"),
        new CharMapperEntry(0x2666, "&diams;")
    };

    public static final CharMapperEntry[] reverseMapping;

    static {
        reverseMapping = new CharMapperEntry[mappedEntities.length + baseEntities.length + 4];
        int count = 0;
        addReverseMapping(new CharMapperEntry('<', "&lt;"), count++);
        addReverseMapping(new CharMapperEntry('>', "&gt;"), count++);
        addReverseMapping(new CharMapperEntry('&', "&amp;"), count++);
        addReverseMapping(new CharMapperEntry('"', "&quot;"), count++);
        for (int i = 0; i < baseEntities.length; i++)
            addReverseMapping(new CharMapperEntry(i + 0xA0, baseEntities[i]), count++);
        for (int i = 0; i < mappedEntities.length; i++)
            addReverseMapping(mappedEntities[i], count++);
//        for (int i = 0; i < reverseMapping.length; i++)
//            System.err.println("Mapping " + reverseMapping[i].getCodePoint() + " to " +
//                    reverseMapping[i].getString());
    }

    private static void addReverseMapping(CharMapperEntry entry, int count) {
        int lo = 0;
        int hi = count;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (entry.getString().compareTo(reverseMapping[mid].getString()) < 0)
                hi = mid;
            else
                lo = mid + 1;
        }
        if (lo < count)
            System.arraycopy(reverseMapping, lo, reverseMapping, lo + 1, count - lo);
        reverseMapping[lo] = entry;
    }

    // note - these two element lists, and the attribute lists in the map below, must be in
    // alphabetic order since they are searched using a binary search
    public static final String[] elementsWithoutChildren = { "area", "base", "basefont", "br",
            "col", "frame", "hr", "img", "input", "isindex", "link", "meta", "param" };

    public static final String[] elementsWithoutText = { "head", "html", "ol", "select",
            "table", "tbody", "tfoot", "thead", "tr", "ul" };

    public static final Map<String, String[]> booleanAttrs = new HashMap<>();

    static {
        booleanAttrs.put("area", new String[] { "nohref" });
        booleanAttrs.put("button", new String[] { "disabled" });
        booleanAttrs.put("frame", new String[] { "noresize" });
        booleanAttrs.put("img", new String[] { "ismap" });
        booleanAttrs.put("input", new String[] { "checked", "disabled", "ismap", "readonly" });
        booleanAttrs.put("object", new String[] { "declare" });
        booleanAttrs.put("ol", new String[] { "compact" });
        booleanAttrs.put("optgroup", new String[] { "disabled" });
        booleanAttrs.put("option", new String[] { "disabled", "selected" });
        booleanAttrs.put("script", new String[] { "defer" });
        booleanAttrs.put("select", new String[] { "disabled", "multiple" });
        booleanAttrs.put("td", new String[] { "nowrap" });
        booleanAttrs.put("textarea", new String[] { "disabled", "readonly" });
        booleanAttrs.put("th", new String[] { "nowrap" });
        booleanAttrs.put("ul", new String[] { "compact" });
        // any others?
    }

    public static final Strings.SpaceTest spaceTest = new Strings.SpaceTest() {
        @Override public boolean isSpace(int ch) {
            return isWhiteSpace(ch);
        }
    };

    public static final CharMapper charMapper = new CharMapper() {
        @Override
        public String map(int codePoint) {
            if (codePoint == '"')
                return "&quot;";
            return dataCharMapper.map(codePoint);
        }
    };

    public static final CharMapper dataCharMapper = new AbstractCharMapper() {
        @Override
        public String map(int codePoint) {
            if (codePoint == '<')
                return "&lt;";
            if (codePoint == '>')
                return "&gt;";
            if (codePoint == '&')
                return "&amp;";
            String result = arrayMapping(baseEntities, codePoint, 0xA0);
            if (result != null)
                return result;
            result = lookupMapping(mappedEntities, codePoint);
            if (result != null)
                return result;
            if (codePoint < ' ' && !isWhiteSpace(codePoint) || codePoint >= 0x7F)
                return decimalMapping(codePoint, "&#", ";");
            return null;
        }
    };

    private static final CharUnmapper unmapper = new CharUnmapper() {
        @Override
        public boolean isEscape(CharSequence s, int offset) {
            return s.charAt(offset) == '&';
        }
        @Override
        public int unmap(StringBuilder sb, CharSequence s, int offset) {
            int start = offset + 1;
            if (start < s.length() && s.charAt(start) == '#') {
                int i = ++start;
                do {
                    if (i >= s.length())
                        throw new IllegalArgumentException("Unclosed character reference");
                } while (s.charAt(i++) != ';');
                int codePoint;
                try {
                    char ch = s.charAt(start);
                    if (ch == 'x' || ch == 'X')
                        codePoint = Strings.convertHexToInt(s, start + 1, i - 1);
                    else
                        codePoint = Strings.convertToInt(s, start, i - 1);
                }
                catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Illegal digit in character reference");
                }
                if (Character.isSupplementaryCodePoint(codePoint)) {
                    sb.append(Character.highSurrogate(codePoint));
                    sb.append(Character.lowSurrogate(codePoint));
                }
                else if (Character.isBmpCodePoint(codePoint) &&
                        !Character.isSurrogate((char)codePoint))
                    sb.append((char)codePoint);
                else
                    throw new IllegalArgumentException("Illegal character reference");
                return i - offset;
            }
            else {
                int lo = 0, hi = reverseMapping.length;
                while (lo < hi) {
                    int mid = (lo + hi) >>> 1;
                    CharMapperEntry entry = reverseMapping[mid];
                    String mapping = entry.getString();
                    int compare = compareEntry(s, offset, mapping);
                    if (compare == 0) {
                        sb.append((char)entry.getCodePoint()); // guaranteed to be in BMP
                        return mapping.length();
                    }
                    if (compare > 0)
                        hi = mid;
                    else
                        lo = mid + 1;
                }
                throw new IllegalArgumentException("Illegal entity reference");
            }
        }
    };

    private static int compareEntry(CharSequence source, int start, CharSequence target) {
        int n = target.length();
        int m = source.length();
        for (int i = 0, j = start; i < n; i++, j++) {
            if (j >= m)
                return 1;
            int result = target.charAt(i) - source.charAt(j);
            if (result != 0)
                return result;
        }
        return 0;
    }

    /**
     * Convert a string to the "escaped" form for HTML, with special characters converted to
     * entity references.  If no conversion is required the original string is returned
     * unmodified.  This method does not encode the single quote / apostrophe character (0x27);
     * therefore it is not suitable for encoding attributes delimited by that character.
     *
     * @param  s    the input string (as a {@link CharSequence})
     * @return      the converted string (as a {@link CharSequence})
     */
    public static CharSequence escape(CharSequence s) {
        return Strings.escape(s, charMapper);
    }

    /**
     * Convert a string to the "escaped" form for HTML, with special characters converted to
     * entity references.  If no conversion is required the original string is returned
     * unmodified.  This method does not encode the single quote / apostrophe character (0x27);
     * therefore it is not suitable for encoding attributes delimited by that character.
     *
     * @param s     the input string
     * @return      the converted string
     */
    public static String escape(String s) {
        return Strings.escape(s, charMapper);
    }

    /**
     * Convert a string to the "escaped" form for HTML, with special characters converted to
     * entity references.  If no conversion is required the original string is returned
     * unmodified.  This method differs from the {@link #escape(CharSequence)} method in that
     * it does not convert the double quote character - it is not necessary to escape this
     * in character data (text nodes etc.).
     *
     * @param s     the input string (as a {@link CharSequence})
     * @return      the converted string (as a {@link CharSequence})
     */
    public static CharSequence escapeData(CharSequence s) {
        return Strings.escape(s, dataCharMapper);
    }

    /**
     * Convert a string to the "escaped" form for HTML, with special characters converted to
     * character and entity references.  If no conversion is required the original string is
     * returned unmodified.  This method differs from the {@link #escape(String)} method in that
     * it does not convert the double quote character - it is not necessary to escape this
     * in character data (text nodes etc.).
     *
     * @param s     the input string
     * @return      the converted string
     */
    public static String escapeData(String s) {
        return Strings.escape(s, dataCharMapper);
    }

    public static void appendEscaped(Appendable a, CharSequence cs) throws IOException {
        Strings.appendEscaped(a, cs, charMapper);
    }

    public static void appendEscapedData(Appendable a, CharSequence cs) throws IOException {
        Strings.appendEscaped(a, cs, dataCharMapper);
    }

    public static String unescape(String s) {
        return Strings.unescape(s, unmapper);
    }

    /**
     * Check a character (Unicode code point) for white space according to the HTML 4.01
     * specification.
     *
     * @param  cp   the code point
     * @return      {@code true} if the code point is white space
     */
    public static boolean isWhiteSpace(int cp) {
        return cp == ' ' || cp == '\t' || cp == '\n' || cp == '\r' || cp == '\f' ||
                cp == 0x200B;
    }

    /**
     * Tests whether a {@link CharSequence} (a {@link String}, {@link StringBuilder} etc.) is
     * comprised entirely of white space characters, using the definition of white space in the
     * HTML 4.01 specification.  Because all the characters considered white space are in the
     * BMP, this function does not need to consider surrogate character mapping.
     *
     * @param  s    the {@link CharSequence}
     * @return      {@code true} if the contents are all space characters
     */
    public static boolean isAllWhiteSpace(CharSequence s) {
        for (int i = 0, n = s.length(); i < n; i++)
            if (!isWhiteSpace(s.charAt(i)))
                return false;
        return true;
    }

    public static String trim(String s) {
        return Strings.trim(s, spaceTest);
    }

    /**
     * Convert a string to the quoted form for JavaScript, using the nominated quote character.
     * This method is included in a class of HTML functions because of the commonly encountered
     * need to output JavaScript in HTML event handlers.
     *
     * @param   str     the string
     * @param   quote   the quote character (must be single or double quote)
     * @return          the quoted string
     * @throws          IllegalArgumentException if the quote character is not valid
     */
    public static String quoteJavaScript(String str, char quote) {
        if (quote != '"' && quote != '\'')
            throw new IllegalArgumentException("quoteJavaScript must use ' or \"");
        StringBuilder sb = new StringBuilder();
        sb.append(quote);
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == 0x08)
                sb.append(backslash).append('b');
            else if (ch == 0x09)
                sb.append(backslash).append('t');
            else if (ch == 0x0A)
                sb.append(backslash).append('n');
            else if (ch == 0x0B)
                sb.append(backslash).append('v');
            else if (ch == 0x0C)
                sb.append(backslash).append('f');
            else if (ch == 0x0D)
                sb.append(backslash).append('r');
            else if (ch == quote)
                sb.append(backslash).append(quote);
            else if (ch == backslash)
                sb.append(backslash).append(backslash);
            else if (ch >= ' ' && ch < 0x7F)
                sb.append(ch);
            else {
                sb.append(backslash).append('u');
                try {
                    Strings.appendHex(sb, ch);
                }
                catch (IOException e) {
                    // can't happen - StringBuilder does not throw IOException
                }
            }
        }
        sb.append(quote);
        return sb.toString();
    }

    /**
     * Convert a string to the quoted form for JavaScript, allowing the method to choose the
     * quote character.  The method will generally choose single quotes, unless the string
     * contains one or more single quotes and does not contain double quotes.
     *
     * @param   str     the string
     * @return          the quoted string
     */
    public static String quoteJavaScript(String str) {
        return quoteJavaScript(str,
                str.indexOf('\'') >= 0 && str.indexOf('"') < 0 ? '"' : '\'');
    }

    /**
     * Output a node (and its children) to a given {@link Writer}.  An optional
     * {@link Properties} object specifies the encoding to be used for the output, along with
     * other formatting options.
     *
     * @param out         the {@link Writer}
     * @param node        the input node
     * @param properties  the {@link Properties} object (may be {@code null})
     * @throws IOException on any I/O errors
     * @throws IllegalArgumentException on errors in the input
     */
    public static void output(Writer out, Node node, Properties properties) throws IOException {
        // NOTE - properties may contain:
        //  method (html, xml, text)
        //  encoding
        //  doctype-public
        //  doctype-system
        //  omit-xml-declaration (only applies to XML)
        //  indent
        //  standalone (only applies to XML)
        //  version
        //  media-type (e.g. text/html)
        boolean indent = "yes".equals(getProperty(properties, "indent"));
        if (newlineString == null)
            newlineString = System.getProperty("line.separator");
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            Element element = (Element)node;
            String tagName = element.getTagName();
            outputElementStart(out, element);
            NodeList children = element.getChildNodes();
            int numChildren = children.getLength();
            if (Arrays.binarySearch(elementsWithoutChildren,
                    tagName.toLowerCase(Locale.ENGLISH)) >= 0) {
                if (numChildren > 0)
                    throw new IllegalArgumentException("<" + tagName + "> node has children");
            }
            else {
                if (tagName.equalsIgnoreCase("script") || tagName.equalsIgnoreCase("style")) {
                    for (int i = 0; i < numChildren; ++i)
                        outputText(out, children.item(i));
                }
                else if (Arrays.binarySearch(elementsWithoutText,
                        tagName.toLowerCase(Locale.ENGLISH)) >= 0) {
                    out.write(newlineString);
                    for (int i = 0; i < numChildren; ++i) {
                        Node child = children.item(i);
                        if (child instanceof Element) {
                            output(out, child, properties);
                            out.write(newlineString);
                        }
                    }
                }
                else if (tagName.equalsIgnoreCase("head")) {
                    out.write(newlineString);
                    out.write("<META http-equiv=\"Content-Type\" content=\"");
                    String mediaType = getProperty(properties, "media-type");
                    out.write(mediaType != null ? mediaType : "text/html");
                    out.write("; charset=");
                    String encoding = getProperty(properties, "encoding");
                    out.write(encoding != null ? encoding : "UTF-8");
                    out.write("\">");
                    out.write(newlineString);
                    for (int i = 0; i < numChildren; ++i) {
                        Node child = children.item(i);
                        if (child instanceof Element) {
                            if (!isContentType(child)) {
                                output(out, child, properties);
                                out.write(newlineString);
                            }
                        }
                    }
                }
                else if (tagName.equalsIgnoreCase("pre")) {
                    for (int i = 0; i < numChildren; ++i)
                        output(out, children.item(i), properties);
                }
                else {
                    StringBuilder text = new StringBuilder();
                    for (int i = 0; i < numChildren; ++i) {
                        Node child = children.item(i);
                        if (child instanceof Text && !(child instanceof CDATASection)) {
                            text.append(((Text)child).getData());
                        }
                        else {
                            if (text.length() > 0) {
                                compact(text);
                                out.append(escapeData(text));
                                text.setLength(0);
                            }
                            output(out, child, properties);
                        }
                    }
                    if (text.length() > 0) {
                        compact(text);
                        out.append(escapeData(text));
                    }
                }
                out.write("</");
                out.write(tagName);
                out.write('>');
            }
//            if (indent)
//                out.write(newlineString);
            break;
        case Node.TEXT_NODE:
            out.append(escapeData(((Text)node).getData()));
            break;
        case Node.CDATA_SECTION_NODE:
            out.write("<![CDATA[");
            out.write(((CDATASection)node).getData());
            out.write("]]>");
            if (indent)
                out.write(newlineString);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            // ignore in HTML
            break;
        case Node.COMMENT_NODE:
            out.write("<!-- ");
            out.write(((Comment)node).getData());
            out.write(" -->");
            if (indent)
                out.write(newlineString);
            break;
        case Node.DOCUMENT_NODE:
            Document document = (Document)node;
            DocumentType documentType = getDocumentType(document);
            if (documentType != null) {
                outputDocumentType(out, documentType);
            }
            else {
                String publicId = getProperty(properties, "doctype-public");
                String systemId = getProperty(properties, "doctype-system");
                if (publicId != null || systemId != null)
                    outputDocumentType(out, document.getDocumentElement().getTagName(),
                            publicId, systemId, null);
            }
            output(out, document.getDocumentElement(), properties);
            break;
        case Node.DOCUMENT_FRAGMENT_NODE: // is this correct?
        case Node.ENTITY_REFERENCE_NODE: // is this correct?
            NodeList roots = node.getChildNodes();
            for (int i = 0, n = roots.getLength(); i < n; ++i)
                output(out, roots.item(i), properties);
            break;
        case Node.DOCUMENT_TYPE_NODE:
            outputDocumentType(out, (DocumentType)node);
            break;
        }
    }

    private static String getProperty(Properties properties, String key) {
        return properties == null ? null : properties.getProperty(key);
    }

    /**
     * Output a node (and its children) to a given {@link Writer}.
     *
     * @param out         the {@link Writer}
     * @param node        the input node
     * @throws IOException on any I/O errors
     * @throws IllegalArgumentException on errors in the input
     */
    public static void output(Writer out, Node node) throws IOException {
        output(out, node, null);
    }

    /**
     * Output a node (and its children) to a given {@link OutputStream}.  An optional
     * {@link Properties} object specifies the encoding to be used for the output, along with
     * other formatting options.
     *
     * @param out         the {@link OutputStream}
     * @param node        the input node
     * @param properties  the {@link Properties} object (may be {@code null})
     * @throws IOException on any I/O errors
     * @throws IllegalArgumentException on errors in the input
     */
    public static void output(OutputStream out, Node node, Properties properties)
            throws IOException {
        String encoding = getProperty(properties, "encoding");
        try (OutputStreamWriter osw = encoding != null ?
                new OutputStreamWriter(out, encoding) : new OutputStreamWriter(out)) {
            output(osw, node, properties);
            osw.flush();
        }
    }

    /**
     * Output a node (and its children) to a given {@link OutputStream}.
     *
     * @param out         the {@link OutputStream}
     * @param node        the input node
     * @throws IOException on any I/O errors
     * @throws IllegalArgumentException on errors in the input
     */
    public static void output(OutputStream out, Node node) throws IOException {
        output(out, node, null);
    }

    public static void outputText(Writer out, Node node) throws IOException {
        if (node instanceof Text)
            out.write(((Text)node).getData());
        else {
            NodeList children = node.getChildNodes();
            for (int i = 0, n = children.getLength(); i < n; ++i)
                outputText(out, children.item(i));
        }
    }

    private static void outputDocumentType(Writer out, DocumentType dt) throws IOException {
        if (dt != null)
            outputDocumentType(out, dt.getName(), dt.getPublicId(), dt.getSystemId(),
                    dt.getInternalSubset());
    }

    private static void outputDocumentType(Writer out, String tagName, String publicId,
            String systemId, String intSubset) throws IOException {
        if (tagName != null) {
            out.write("<!DOCTYPE ");
            out.write(tagName);
            if (publicId != null) {
                out.write(" PUBLIC \"");
                out.write(publicId);
                out.write('"');
                if (systemId != null) {
                    out.write(" \"");
                    out.write(systemId);
                    out.write('"');
                }
            }
            else {
                if (systemId != null) {
                    out.write(" SYSTEM \"");
                    out.write(systemId);
                    out.write('"');
                }
            }
            if (intSubset != null) {
                out.write(" [");
                out.write(intSubset);
                out.write(']');
            }
            out.write('>');
            out.write(newlineString);
        }
    }

    private static void outputElementStart(Writer out, Element element) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        String tagName = element.getTagName();
        sb.append(tagName);
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Attr attr = (Attr)attrs.item(i);
            String name = attr.getName();
            String value = attr.getValue();
            String[] attrNames = booleanAttrs.get(tagName.toLowerCase(Locale.ENGLISH));
            if (attrNames != null &&
                    Arrays.binarySearch(attrNames, name.toLowerCase(Locale.ENGLISH)) >= 0) {
                if (value != null && value.length() > 0)
                    sb.append(' ').append(name);
            }
            else
                appendAttributeValue(sb, name, value);
        }
        if (sb.charAt(sb.length() - 1) == '/') // in case last attribute without quotes ends
            sb.append(' ');                    // with '/' - avoid '/>' at end of element
        out.write(sb.toString());
    }

    private static void appendAttributeValue(StringBuilder sb, String name, String value) {
        sb.append(' ').append(name).append('=');
        if (value != null && value.length() > 0) {
            for (int i = 0, n = value.length(); i < n; ++i) {
                char ch = value.charAt(i);
                if (isWhiteSpace(ch) || ch == '"' || ch == '\'' || ch == '=' || ch == '<' ||
                        ch == '>' || ch == '`') {
                    // check for '&'? (deciding whether to enclose in quotes)
                    sb.append('"').append(escape(value)).append('"');
                    return;
                }
            }
            sb.append(escape(value));
        }
        else
            sb.append("\"\"");
    }

    private static DocumentType getDocumentType(Document document) {
        NodeList children = document.getChildNodes();
        for (int i = 0, n = children.getLength(); i < n; ++i) {
            Node child = children.item(i);
            if (child instanceof DocumentType)
                return (DocumentType)child;
        }
        return null;
    }

    private static boolean isContentType(Node node) {
        if (node instanceof Element) {
            Element element = (Element)node;
            if ("meta".equalsIgnoreCase(element.getTagName())) {
                if ("Content-Type".equalsIgnoreCase(element.getAttribute("http-equiv")) ||
                        "Content-Type".equalsIgnoreCase(element.getAttribute("name")))
                    return true;
            }
        }
        return false;
    }

    private static void compact(StringBuilder sb) {
        for (int i = 0; i < sb.length(); ++i) {
            char ch = sb.charAt(i);
            if (isWhiteSpace(ch)) {
                boolean newline = ch == '\r' || ch == '\n';
                int j = i;
                while (++j < sb.length()) {
                    ch = sb.charAt(j);
                    if (!isWhiteSpace(ch))
                        break;
                    if (ch == '\r' || ch == '\n')
                        newline = true;
                }
                if (j - i > 1) {
                    sb.delete(i, j);
                    if (newline) {
                        sb.insert(i, newlineString);
                        i += newlineString.length();
                    }
                    else {
                        sb.insert(i, ' ');
                        i++;
                    }
                }
            }
        }
    }

}
