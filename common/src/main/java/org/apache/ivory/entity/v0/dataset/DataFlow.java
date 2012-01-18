/*******************************************************************************
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
 ******************************************************************************/
package org.apache.ivory.entity.v0.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "_import", "copy", "export" })
@XmlRootElement(name = "DataFlow")
public class DataFlow {

  @XmlElement(name = "Import", required = true)
  protected List<Import> _import;

  @XmlElement(name = "Copy", required = true)
  protected List<Copy> copy;

  @XmlElement(name = "Export", required = true)
  protected List<Export> export;

  public List<Import> getImport() {
    if (_import == null) {
      _import = new ArrayList<Import>();
    }
    return _import;
  }

  public List<Copy> getCopy() {
    if (copy == null) {
      copy = new ArrayList<Copy>();
    }
    return copy;
  }

  public List<Export> getExport() {
    if (export == null) {
      export = new ArrayList<Export>();
    }
    return export;
  }

}
