/*
 * @(#) HTMLFormatter.java
 *
 * HTML Formatter Class
 * Copyright (c) 2012, 2013, 2014, 2015 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * HTML Formatter.  Takes a stream of SAX events and outputs the corresponding external
 * representation of the HTML.
 *
 * @author Peter Wall
 */
public class HTMLFormatter extends DefaultHandler2 implements AutoCloseable {

    /**
     * Enumeration to control whitespace handling in the formatted output:
     * <dl>
     *   <dt>NONE</dt>
     *   <dd>All non-essential whitespace will be dropped</dd>
     *   <dt>ALL</dt>
     *   <dd>All whitespace will be output</dd>
     *   <dt>INDENT</dt>
     *   <dd>The formatter will, where possible, format the output in a conventional indented
     *   form</dd>
     * </dl>
     */
    public enum Whitespace {
        NONE, ALL, INDENT
    }

    public static final String[] elementsWithoutChildren = { "area", "base", "basefont", "br",
        "col", "frame", "hr", "img", "input", "isindex", "link", "meta", "param" };

    public static final String[] elementsWithoutText = { "html", "head", "ol", "select",
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
    }

    private static String eol = System.getProperty("line.separator");

    private OutputStream out;
    private Whitespace whitespace;
    private String encoding;
    private int indent;
    private int preCount;
    private boolean literal;
    private StringBuilder data;
    private List<String> elements;
    private boolean elementPending;
    private Writer writer;
    @SuppressWarnings("unused")
    private Locator locator;
    private boolean documentStarted;
    private boolean documentEnded;
    private boolean dtdStarted;
    private boolean dtdInternalSubset;
    private boolean dtdEnded;

    /**
     * Construct a {@code HTMLFormatter} using the given {@link OutputStream}, with the given
     * whitespace option.
     *
     * @param out         the {@link OutputStream}
     * @param whitespace  the whitespace option
     */
    public HTMLFormatter(OutputStream out, Whitespace whitespace) {
        this.out = out;
        this.whitespace = whitespace;
        encoding = "UTF-8";
        indent = 2;
        preCount = 0;
        literal = false;
        data = new StringBuilder();
        elements = new ArrayList<>();
        elementPending = false;
        writer = null;
        locator = null;
        documentStarted = false;
        documentEnded = false;
        dtdStarted = false;
        dtdInternalSubset = false;
        dtdEnded = false;
    }

    /**
     * Construct a {@code HTMLFormatter} using the given {@link OutputStream}, with the default
     * whitespace option.
     *
     * @param out         the {@link OutputStream}
     */
    public HTMLFormatter(OutputStream out) {
        this(out, Whitespace.ALL);
    }

    /**
     * Close the formatter.
     *
     * @throws IOException on any errors closing the output {@link Writer}.
     */
    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
        if (!documentEnded) {
            documentEnded = true;
            throw new IOException("Premature HTMLFormatter close");
        }
    }

    /**
     * Receive notification of the beginning of the DTD.
     *
     * @param   name        the document element name
     * @param   publicId    the public id
     * @param   systemId    the system id
     * @throws  SAXException    on any errors
     * @see     org.xml.sax.ext.LexicalHandler#startDTD(String, String, String)
     */
    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (!documentStarted)
            throw new SAXException("Document not started");
        if (dtdStarted || dtdEnded)
            throw new SAXException("Misplaced DTD");
        dtdStarted = true;
        String data = checkData();
        if (!HTML.isAllWhiteSpace(data))
            throw new SAXException("Misplaced data before DOCTYPE");
        try {
            if (whitespace == Whitespace.ALL)
                write(data);
            write("<!DOCTYPE ");
            write(name);
            if (!isEmpty(publicId)) {
                write(" PUBLIC \"");
                write(publicId);
                write("\" \"");
                write(systemId);
                write('"');
            }
            else if (!isEmpty(systemId)) {
                write(" SYSTEM \"");
                write(systemId);
                write('"');
            }
        }
        catch (IOException ioe) {
            throw new SAXException("Error in HTMLFormatter", ioe);
        }
    }

    /**
     * Receive notification of the end of the DTD.
     *
     * @throws  SAXException    on any errors
     * @see     org.xml.sax.ext.LexicalHandler#endDTD()
     */
    @Override
    public void endDTD() throws SAXException {
        if (!documentStarted)
            throw new SAXException("Document not started");
        if (!dtdStarted || dtdEnded)
            throw new SAXException("Misplaced End DTD");
        dtdEnded = true;
        try {
            if (dtdInternalSubset)
                write(']');
            write('>');
            if (whitespace == Whitespace.INDENT)
                write(eol);
        }
        catch (IOException ioe) {
            throw new SAXException("Error in HTMLFormatter", ioe);
        }
    }

    /**
     * Receive notification of the beginning of the document.
     *
     * @exception   SAXException    on any errors
     * @see         org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        if (documentStarted)
            throw new SAXException("Document already started");
        documentStarted = true;
    }

    /**
     * Receive notification of the end of the document.
     *
     * @exception   SAXException    on any errors
     * @see         org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        if (!documentStarted)
            throw new SAXException("Document not started");
        if (documentEnded)
            throw new SAXException("Document already ended");
        documentEnded = true;
        if (elementPending || elements.size() > 0)
            throw new SAXException("Unexpected end");
        if (!HTML.isAllWhiteSpace(data))
            throw new SAXException("Invalid data at end");
        try {
            write(checkData());
        }
        catch (IOException ioe) {
            throw new SAXException("Error in HTMLFormatter", ioe);
        }
    }

    /**
     * Receive notification of the start of a Namespace mapping.  Not allowed for HTML.
     *
     * @param   prefix  the Namespace prefix being declared
     * @param   uri     the Namespace URI mapped to the prefix
     * @exception       SAXException    always
     * @see     org.xml.sax.ContentHandler#startPrefixMapping(String, String)
     */
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        throw new SAXException("Namespaces not allowed in HTML");
    }

    /**
     * Receive notification of the end of a Namespace mapping.  Not allowed for HTML.
     *
     * @param   prefix  the Namespace prefix
     * @exception       SAXException    always
     * @see     org.xml.sax.ContentHandler#endPrefixMapping(String)
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        throw new SAXException("Namespaces not allowed in HTML");
    }

    /**
     * Set the {@link Locator} object for references from this document.
     *
     * @param locator  the {@link Locator} object
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (!documentStarted)
            throw new SAXException("Document not started");
        if (documentEnded)
            throw new SAXException("Document already ended");
        if (elements.size() > 0 &&
                arrayContains(elementsWithoutChildren,
                        elements.get(elements.size() - 1).toLowerCase()))
            throw new SAXException("Misplaced element");
        try {
            write(checkData());
            String elementName = qName.toLowerCase(); // check name is not qualified?
            if (!("meta".equals(elementName) && isMetaContentType(attributes))) {
                if (whitespace == Whitespace.INDENT)
                    writeSpaces(elements.size() * getIndent());
                write('<');
                write(qName);
                String[] boolAttr = booleanAttrs.get(elementName);
                for (int i = 0, n = attributes.getLength(); i < n; i++) {
                    String attr = attributes.getQName(i); // check name is not qualified?
                    if (boolAttr != null && arrayContains(boolAttr, attr.toLowerCase())) {
                        write(' ');
                        write(attr);
                    }
                    else {
                        write(' ');
                        write(attr);
                        write("=\"");
                        write(HTML.escape(attributes.getValue(i)));
                        write('"');
                    }
                }
                write('>');
                if ("head".equals(elementName)) {
                    if (whitespace == Whitespace.INDENT) {
                        write(eol);
                        writeSpaces((elements.size() + 1) * getIndent());
                    }
                    write("<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
                    write(encoding);
                    write("\">");
                }
                if (arrayContains(elementsWithoutChildren, elementName)) {
                    if (whitespace == Whitespace.INDENT)
                        write(eol);
                }
                else
                    elementPending = true;
            }
            elements.add(qName);
            if ("pre".equals(elementName))
                preCount++;
            if ("style".equals(elementName) || "script".equals(elementName))
                literal = true;
        }
        catch (IOException e) {
            throw new SAXException("Error in HTMLFormatter", e);
        }
    }

    private boolean isMetaContentType(Attributes attributes) {
        for (int i = 0, n = attributes.getLength(); i < n; i++) {
            String attrName = attributes.getLocalName(i); // check name is not qualified?
            if (("http-equiv".equals(attrName) || "name".equals(attrName)) &&
                    "content-type".equals(attributes.getValue(i)))
                return true;
        }
        return false;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String elementName = qName.toLowerCase();
        if (elements.size() == 0 || !elementName.equals(elements.remove(elements.size() - 1)))
            throw new SAXException("Unmatched element end");
        try {
            String data = this.data.toString();
            this.data.setLength(0);
            if (arrayContains(elementsWithoutChildren, elementName)) {
                if (!data.isEmpty())
                    throw new SAXException("Misplaced data");
                return;
            }
            if (arrayContains(elementsWithoutText, elementName) &&
                    !HTML.isAllWhiteSpace(data))
                throw new SAXException("Misplaced data");
            if (literal) {
                write(data);
                write("</");
                write(qName);
                write('>');
                if (whitespace == Whitespace.INDENT)
                    write(eol);
                elementPending = false;
            }
            else if (preCount > 0 || whitespace == Whitespace.ALL) {
                write(HTML.escape(data));
                write("</");
                write(qName);
                write('>');
                if (preCount == 1 && whitespace == Whitespace.INDENT)
                    write(eol);
                elementPending = false;
            }
            else if (whitespace == Whitespace.NONE) {
                if (elementPending) {
                    write(HTML.escape(HTML.trim(data)));
                    write("</");
                    write(qName);
                    write('>');
                    elementPending = false;
                }
                else {
                    if (!HTML.isAllWhiteSpace(data)) {
                        if (HTML.isWhiteSpace(data.charAt(0)))
                            write(' ');
                        write(HTML.escape(HTML.trim(data)));
                    }
                    write("</");
                    write(qName);
                    write('>');
                }
            }
            else { // whitespace == Whitespace.INDENT
                if (elementPending) {
                    write(HTML.escape(HTML.trim(data)));
                    write("</");
                    write(qName);
                    write('>');
                    write(eol);
                    elementPending = false;
                }
                else {
                    if (!HTML.isAllWhiteSpace(data)) {
                        writeSpaces((elements.size() + 1) * indent);
                        write(HTML.escape(HTML.trim(data)));
                        write(eol);
                    }
                    writeSpaces(elements.size() * indent);
                    write("</");
                    write(qName);
                    write('>');
                    write(eol);
                }
            }
            if ("pre".equals(elementName))
                preCount--;
            if ("style".equals(elementName) || "script".equals(elementName))
                literal = false;
        }
        catch (Exception e) {
            throw new SAXException("Error in HTMLFormatter", e);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!documentStarted)
            throw new SAXException("Document not started");
        if (documentEnded)
            throw new SAXException("Document already ended");
        try {
            data.append(ch, start, length);
        }
        catch (Exception e) {
            throw new SAXException("Error in HTMLFormatter", e);
        }
    }

    public void characters(String str) throws SAXException {
        characters(str.toCharArray(), 0, str.length());
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        characters(ch, start, length);
    }

    @Override
    public void comment(char ch[], int start, int length) throws SAXException {
        // TODO check this
        for (int i = start, n = start + length - 1; i < n; i++)
            if (ch[i] == '-' && ch[i + 1] == '-')
                throw new SAXException("Illegal data in comment");
        try {
            write(checkData());
            if (whitespace == Whitespace.INDENT)
                writeSpaces(elements.size() * getIndent());
            write("<!--");
            StringBuilder sb = new StringBuilder(length);
            sb.append(ch, start, length);
            write("-->");
            if (whitespace == Whitespace.INDENT)
                write(eol);
        }
        catch (IOException e) {
            throw new SAXException("Error in HTMLFormatter", e);
        }
    }

    private String checkData() throws SAXException {
        StringBuilder output = new StringBuilder();
        String data = this.data.toString();
        this.data.setLength(0);
        if (elements.isEmpty() ||
                arrayContains(elementsWithoutText,
                        elements.get(elements.size() - 1).toLowerCase())) {
            if (!HTML.isAllWhiteSpace(data))
                throw new SAXException("Misplaced data");
            if (whitespace == Whitespace.NONE)
                data = "";
        }
        if (literal) { // shouldn't happen
            output.append(data);
        }
        else if (preCount > 0 || whitespace == Whitespace.ALL) {
            output.append(HTML.escape(data));
        }
        else if (whitespace == Whitespace.NONE) {
            if (elementPending) {
                if (!HTML.isAllWhiteSpace(data)) {
                    output.append(HTML.escape(HTML.trim(data)));
                    if (HTML.isWhiteSpace(data.charAt(data.length() - 1)))
                        output.append(' ');
                }
            }
            else {
                if (!data.isEmpty()) {
                    if (HTML.isAllWhiteSpace(data))
                        output.append(' ');
                    else {
                        if (HTML.isWhiteSpace(data.charAt(0)))
                            output.append(' ');
                        output.append(HTML.escape(HTML.trim(data)));
                        if (HTML.isWhiteSpace(data.charAt(data.length() - 1)))
                            output.append(' ');
                    }
                }
            }
        }
        else { // whitespace == Whitespace.INDENT
            if (elementPending)
                output.append(eol);
            if (!HTML.isAllWhiteSpace(data)) {
                addSpaces(output, elements.size() * indent);
                output.append(HTML.escape(HTML.trim(data)));
                output.append(eol);
            }
        }
        elementPending = false;
        return output.toString();
    }

    private void write(String str) throws IOException {
        getWriter().write(str);
    }

    private void write(char ch) throws IOException {
        getWriter().write(ch);
    }

    private void writeSpaces(int length) throws IOException {
        while (length-- > 0)
            getWriter().write(' ');
    }

    private synchronized Writer getWriter() throws IOException {
        if (writer == null) {
            if (out == null)
                throw new IllegalStateException("Output Stream not set");
            String encoding = getEncoding();
            writer = new BufferedWriter(encoding == null ? new OutputStreamWriter(out) :
                    new OutputStreamWriter(out, encoding));
        }
        return writer;
    }

    private static void addSpaces(StringBuilder a, int n) {
        for (; n > 0; n--)
            a.append(' ');
    }

    public Whitespace getWhitespace() {
        return whitespace;
    }

    public void setWhitespace(Whitespace whitespace) {
        this.whitespace = whitespace;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    private static <E> boolean arrayContains(E[] array, E item) {
        for (int i = 0, n = array.length; i < n; i++)
            if (array[i].equals(item))
                return true;
        return false;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
