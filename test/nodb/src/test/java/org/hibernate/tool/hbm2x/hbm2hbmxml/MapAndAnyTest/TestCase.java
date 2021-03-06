/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml",
			"Person.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Exporter hbmexporter = null;
	private File outputDir = null;
	private File resourcesDir = null;
	private Metadata metadata = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml") );
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[4]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testAnyNode() throws DocumentException {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/any");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one any element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(),"someSpecificProperty");
		Assert.assertEquals(node.attribute( "id-type" ).getText(),"long");
		Assert.assertEquals(node.attribute( "meta-type" ).getText(),"string");
		Assert.assertEquals(node.attribute( "cascade" ).getText(), "all");
		Assert.assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("column");
		Assert.assertEquals("Expected to get two column elements", 2, list.size());
		list = node.elements("meta-value");
		Assert.assertEquals("Expected to get three meta-value elements", 3, list.size());
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"I");
		} else if (className.indexOf("StringPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"S");
		} else {
			Assert.assertTrue(className.indexOf("ComplexPropertyValue") > 0);
			Assert.assertEquals(node.attribute( "value" ).getText(),"C");
		}
	}

	@Test
	public void testMetaValueRead() throws Exception{
		PersistentClass pc = metadata.getEntityBinding("org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest.Person");
		Assert.assertNotNull(pc);
		Property prop = pc.getProperty("data");
		Assert.assertNotNull(prop);
		Assert.assertTrue(prop.getValue() instanceof Any);
		Any any = (Any) prop.getValue();
		Assert.assertTrue("Expected to get one meta-value element", any.getMetaValues() != null);
		Assert.assertEquals("Expected to get one meta-value element", 1, any.getMetaValues().size());
	}

	@Test
	public void testMapManyToAny() throws DocumentException {
		File outputXml = new File(outputDir,  "org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/map");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one any element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(),"generalProperties");
		Assert.assertEquals(node.attribute( "table" ).getText(),"T_GEN_PROPS");
		Assert.assertEquals(node.attribute( "lazy" ).getText(),"true");
		Assert.assertEquals(node.attribute( "cascade" ).getText(), "all");
		Assert.assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("key");
		Assert.assertEquals("Expected to get one key element", 1, list.size());
		list = node.elements("map-key");
		Assert.assertEquals("Expected to get one map-key element", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "type" ).getText(),"string");
		list = node.elements("column");
		Assert.assertEquals("Expected to get one column element", 1, list.size());
		node = node.getParent();//map
		list = node.elements("many-to-any");
		Assert.assertEquals("Expected to get one many-to-any element", 1, list.size());
		node = (Element) list.get(0);
		list = node.elements("column");
		Assert.assertEquals("Expected to get two column elements", 2, list.size());
		list = node.elements("meta-value");
		Assert.assertEquals("Expected to get two meta-value elements", 2, list.size());
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"I");
		} else {
			Assert.assertTrue(className.indexOf("StringPropertyValue") > 0);
			Assert.assertEquals(node.attribute( "value" ).getText(),"S");
		}
	}

}
