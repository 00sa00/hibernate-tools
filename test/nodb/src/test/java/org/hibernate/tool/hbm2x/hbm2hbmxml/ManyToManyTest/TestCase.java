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

package org.hibernate.tool.hbm2x.hbm2hbmxml.ManyToManyTest;

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
import org.hibernate.cfg.AvailableSettings;
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

public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"UserGroup.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private HbmExporter hbmexporter = null;
	private File outputDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		Assert.assertFalse(new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml")
			.exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml") );
	}

	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				2,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[2]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testManyToMany() throws DocumentException {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/many-to-many");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one many-to-many element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "entity-name" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.ManyToManyTest.Group");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one set element", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "table" ).getText(),"UserGroup");
	}

	@Test
	public void testCompositeId() throws DocumentException {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml");
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one class element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute("table").getText(), "`Group`");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one composite-id element", 1, list.size());
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id/key-property");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get two key-property elements", 2, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute("name").getText(), "name");
		node = (Element) list.get(1);
		Assert.assertEquals(node.attribute("name").getText(), "org");
	}

	@Test
	public void testSetAttributes() {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
			List<?> list = xpath.selectNodes(document);
			Assert.assertEquals("Expected to get one set element", 1, list.size());
			Element node = (Element) list.get(0);
			Assert.assertEquals(node.attribute("table").getText(), "UserGroup");
			Assert.assertEquals(node.attribute("name").getText(), "users");
			Assert.assertEquals(node.attribute("inverse").getText(), "true");
			Assert.assertEquals(node.attribute("lazy").getText(), "extra");
		} catch (DocumentException e) {
			Assert.fail("Can't parse file " + outputXml.getAbsolutePath());
		}
	}

}
