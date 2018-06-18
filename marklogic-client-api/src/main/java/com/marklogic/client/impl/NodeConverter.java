/*
 * Copyright 2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;

public class NodeConverter {
   static private ObjectMapper           mapper;
   static private DocumentBuilderFactory documentBuilderFactory;
   static private XMLInputFactory        xmlInputFactory;

   private NodeConverter() {
      super();
   }

   static private ObjectMapper getMapper() {
      // okay if one thread overwrites another during lazy initialization
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }
   static private DocumentBuilderFactory getDocumentBuilderFactory() {
      // okay if one thread overwrites another during lazy initialization
      if (documentBuilderFactory == null) {
         documentBuilderFactory = DocumentBuilderFactory.newInstance();
      }
      return documentBuilderFactory;
   }
   static private XMLInputFactory getXMLInputFactory() {
      // okay if one thread overwrites another during lazy initialization
      if (xmlInputFactory == null) {
         xmlInputFactory = XMLInputFactory.newFactory();
      }
      return xmlInputFactory;
   }

   static public BinaryWriteHandle BinaryWriter(BinaryWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.BINARY, BinaryWriteHandle.class);
   }
   static public Stream<BinaryWriteHandle> BinaryWriter(Stream<? extends BinaryWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::BinaryWriter);
   }
   static public BinaryReadHandle BinaryReader(BinaryReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.BINARY, BinaryReadHandle.class);
   }
   static public Stream<BinaryReadHandle> BinaryReader(Stream<? extends BinaryReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::BinaryReader);
   }
   static public JSONWriteHandle JSONWriter(JSONWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.JSON, JSONWriteHandle.class);
   }
   static public Stream<JSONWriteHandle> JSONWriter(Stream<? extends JSONWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::JSONWriter);
   }
   static public JSONReadHandle JSONReader(JSONReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.JSON, JSONReadHandle.class);
   }
   static public Stream<JSONReadHandle> JSONReader(Stream<? extends JSONReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::JSONReader);
   }
   static public TextWriteHandle TextWriter(TextWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.TEXT, TextWriteHandle.class);
   }
   static public Stream<TextWriteHandle> TextWriter(Stream<? extends TextWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::TextWriter);
   }
   static public TextReadHandle TextReader(TextReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.TEXT, TextReadHandle.class);
   }
   static public Stream<TextReadHandle> TextReader(Stream<? extends TextReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::TextReader);
   }
   static public XMLWriteHandle XMLWriter(XMLWriteHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.XML, XMLWriteHandle.class);
   }
   static public Stream<XMLWriteHandle> XMLWriter(Stream<? extends XMLWriteHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::XMLWriter);
   }
   static public XMLReadHandle XMLReader(XMLReadHandle handle) {
      return (handle == null) ? null : withFormat(handle, Format.XML, XMLReadHandle.class);
   }
   static public Stream<XMLReadHandle> XMLReader(Stream<? extends XMLReadHandle> handles) {
      return (handles == null) ? null : handles.map(NodeConverter::XMLReader);
   }
   static private <T> T withFormat(T handle, Format format, Class<T> as) {
      if (handle != null) {
         if (!(handle instanceof BaseHandle)) {
            throw new IllegalArgumentException(
                  "cannot set format on handle of "+handle.getClass().getName()
            );
         }
         ((BaseHandle) handle).setFormat(format);
      }
      return handle;
   }

   static public BytesHandle BytesToHandle(byte[] value) {
      return (value == null) ? null : new BytesHandle(value);
   }
   static public Stream<BytesHandle> BytesToHandle(Stream<? extends byte[]> values) {
      return (values == null) ? null : values.map(NodeConverter::BytesToHandle);
   }
   static public DOMHandle DocumentToHandle(Document value) {
      return (value == null) ? null : new DOMHandle(value);
   }
   static public Stream<DOMHandle> DocumentToHandle(Stream<? extends Document> values) {
      return (values == null) ? null : values.map(NodeConverter::DocumentToHandle);
   }
   static public FileHandle FileToHandle(File value) {
      return (value == null) ? null : new FileHandle(value);
   }
   static public Stream<FileHandle> FileToHandle(Stream<? extends File> values) {
      return (values == null) ? null : values.map(NodeConverter::FileToHandle);
   }
   static public InputStreamHandle InputStreamToHandle(InputStream value) {
      return (value == null) ? null : new InputStreamHandle(value);
   }
   static public Stream<InputStreamHandle> InputStreamToHandle(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToHandle);
   }
   static public InputSourceHandle InputSourceToHandle(InputSource value) {
      return (value == null) ? null : new InputSourceHandle(value);
   }
   static public Stream<InputSourceHandle> InputSourceToHandle(Stream<? extends InputSource> values) {
      return (values == null) ? null : values.map(NodeConverter::InputSourceToHandle);
   }
   static public JacksonHandle JsonNodeToHandle(JsonNode value) {
      return (value == null) ? null : new JacksonHandle(value);
   }
   static public Stream<JacksonHandle> JsonNodeToHandle(Stream<? extends JsonNode> values) {
      return (values == null) ? null : values.map(NodeConverter::JsonNodeToHandle);
   }
   static public JacksonParserHandle JsonParserToHandle(JsonParser value) {
      if (value == null) {
         return null;
      }
      JacksonParserHandle handle = new JacksonParserHandle();
      handle.set(value);
      return handle;
   }
   static public Stream<JacksonParserHandle> JsonParserToHandle(Stream<? extends JsonParser> values) {
      return (values == null) ? null : values.map(NodeConverter::JsonParserToHandle);
   }
   static public ArrayNode ReaderToArrayNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readValue(value, ArrayNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<ArrayNode> ReaderToArrayNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToArrayNode);
   }
   static public JsonNode ReaderToJsonNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readTree(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<JsonNode> ReaderToJsonNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToJsonNode);
   }
   static public ObjectNode ReaderToObjectNode(Reader value) {
      try {
         return (value == null) ? null : getMapper().readValue(value, ObjectNode.class);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<ObjectNode> ReaderToObjectNode(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToObjectNode);
   }
   static public JsonParser ReaderToJsonParser(Reader value) {
      try {
         return (value == null) ? null : getMapper().getFactory().createParser(value);
      } catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<JsonParser> ReaderToJsonParser(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToJsonParser);
   }

   static public Document InputStreamToDocument(InputStream inputStream) {
      try {
         return (inputStream == null) ? null : getDocumentBuilderFactory().newDocumentBuilder().parse(inputStream);
      } catch(SAXException e) {
         throw new RuntimeException(e);
      } catch(IOException e) {
         throw new RuntimeException(e);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<Document> InputStreamToDocument(Stream<? extends InputStream> values) {
      return (values == null) ? null : values.map(NodeConverter::InputStreamToDocument);
   }
   static public InputSource ReaderToInputSource(Reader reader) {
      return (reader == null) ? null : new InputSource(reader);
   }
   static public Stream<InputSource> ReaderToInputSource(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToInputSource);
   }
   static public Source ReaderToSource(Reader reader) {
      return (reader == null) ? null : new StreamSource(reader);
   }
   static public Stream<Source> ReaderToSource(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToSource);
   }
   static public XMLEventReader ReaderToXMLEventReader(Reader reader) {
      try {
         return (reader == null) ? null : getXMLInputFactory().createXMLEventReader(reader);
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<XMLEventReader> ReaderToXMLEventReader(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToXMLEventReader);
   }
   static public XMLStreamReader ReaderToXMLStreamReader(Reader reader) {
      try {
         return (reader == null) ? null : getXMLInputFactory().createXMLStreamReader(reader);
      } catch(XMLStreamException e) {
         throw new RuntimeException(e);
      } catch(FactoryConfigurationError e) {
         throw new RuntimeException(e);
      }
   }
   static public Stream<XMLStreamReader> ReaderToXMLStreamReader(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToXMLStreamReader);
   }

   static public OutputStreamHandle OutputStreamSenderToHandle(OutputStreamSender value) {
      return (value == null) ? null : new OutputStreamHandle(value);
   }
   static public Stream<OutputStreamHandle> OutputStreamSenderToHandle(Stream<? extends OutputStreamSender> values) {
      return (values == null) ? null : values.map(NodeConverter::OutputStreamSenderToHandle);
   }
   static public ReaderHandle ReaderToHandle(Reader value) {
      return (value == null) ? null : new ReaderHandle(value);
   }
   static public Stream<ReaderHandle> ReaderToHandle(Stream<? extends Reader> values) {
      return (values == null) ? null : values.map(NodeConverter::ReaderToHandle);
   }
   static public StringHandle StringToHandle(String value) {
      return (value == null) ? null : new StringHandle(value);
   }
   static public Stream<StringHandle> StringToHandle(Stream<? extends String> values) {
      return (values == null) ? null : values.map(NodeConverter::StringToHandle);
   }
   static public SourceHandle SourceToHandle(Source value) {
      return (value == null) ? null : new SourceHandle(value);
   }
   static public Stream<SourceHandle> SourceToHandle(Stream<? extends Source> values) {
      return (values == null) ? null : values.map(NodeConverter::SourceToHandle);
   }
   static public XMLEventReaderHandle XMLEventReaderToHandle(XMLEventReader value) {
      return (value == null) ? null : new XMLEventReaderHandle(value);
   }
   static public Stream<XMLEventReaderHandle> XMLEventReaderToHandle(Stream<? extends XMLEventReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLEventReaderToHandle);
   }
   static public XMLStreamReaderHandle XMLStreamReaderToHandle(XMLStreamReader value) {
      return (value == null) ? null : new XMLStreamReaderHandle(value);
   }
   static public Stream<XMLStreamReaderHandle> XMLStreamReaderToHandle(Stream<? extends XMLStreamReader> values) {
      return (values == null) ? null : values.map(NodeConverter::XMLStreamReaderToHandle);
   }

}