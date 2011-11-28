/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.airavat.entity.parser;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.airavat.Util;
import org.apache.airavat.entity.v0.EntityType;
import org.apache.airavat.entity.v0.ProcessType;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *Concrete Parser which has  XML parsing and validation logic
 *for Process XML.
 *
 */
public class ProcessEntityParser extends EntityParser<ProcessType>{

	private static Logger LOG = Logger.getLogger(ProcessEntityParser.class);

	private static final String SCHEMA_FILE = "src/main/resources/process.xsd";

	private static final  Class<ProcessType> ProcessDefinitionClazz = org.apache.airavat.entity.v0.ProcessType.class;

	protected ProcessEntityParser(EntityType entityType) {
		super(entityType);		
	}

	@Override
	protected ProcessType doParse(String xmlString) throws SAXException, JAXBException {

		ProcessType processDefinitionElement = null;

		Unmarshaller unmarshaller = SingletonUnmarshaller.getInstance();
		//Validate against schema
		SchemaFactory schemaFactory=SchemaFactory.newInstance
				("http://www.w3.org/2001/XMLSchema");
		Schema schema = null;
		schema = schemaFactory.newSchema(new File(SCHEMA_FILE));
		unmarshaller.setSchema(schema);
		InputStream xmlStream = Util.getStreamFromString(xmlString);
		processDefinitionElement =  (ProcessType) unmarshaller.unmarshal(xmlStream);
		//System.out.println(processDefinitionElement.getClass());

		return processDefinitionElement;
	}

	@Override
	protected void applyValidations(ProcessType entity) {

	}

	public static class SingletonUnmarshaller {

		private static Unmarshaller instance = null;

		private SingletonUnmarshaller() {
		}

		synchronized public static Unmarshaller getInstance() throws JAXBException {
			if (instance == null) {
				try {
					JAXBContext jaxbContext = JAXBContext
							.newInstance(ProcessDefinitionClazz);
					instance = jaxbContext.createUnmarshaller();
				} catch (JAXBException e) {
					LOG.fatal("Unable to get JAXBContext",e);
					throw new JAXBException(e);
				}
			}
			return instance;
		}
	}

}
