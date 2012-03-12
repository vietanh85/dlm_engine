//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.07 at 07:17:44 PM GMT+05:30 
//


package org.apache.ivory.entity.v0.feed;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.ivory.entity.v0.Entity;
import org.apache.ivory.entity.v0.feed.adapter.PropertiesMapAdapter;
import org.apache.ivory.entity.v0.feed.adapter.LocationsMapAdapter;


/**
 * <p>Java class for feed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="feed">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="partitions" type="{uri:ivory:feed:0.1}partitions"/>
 *         &lt;element name="groups" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="frequency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="periodicity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="late-arrival" type="{uri:ivory:feed:0.1}late-arrival"/>
 *         &lt;element name="clusters" type="{uri:ivory:feed:0.1}clusters"/>
 *         &lt;element name="locations" type="{uri:ivory:feed:0.1}locations"/>
 *         &lt;element name="ACL" type="{uri:ivory:feed:0.1}ACL"/>
 *         &lt;element name="schema" type="{uri:ivory:feed:0.1}schema"/>
 *         &lt;element name="properties" type="{uri:ivory:feed:0.1}properties"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{uri:ivory:feed:0.1}IDENTIFIER" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feed", propOrder = {
    "partitions",
    "groups",
    "frequency",
    "periodicity",
    "lateArrival",
    "clusters",
    "locations",
    "acl",
    "schema",
    "properties"
})
@XmlRootElement(name="feed")
public class Feed extends Entity{

    @XmlElement(required = true)
    protected Partitions partitions;
    @XmlElement(required = true)
    protected String groups;
    @XmlElement(required = true)
    protected String frequency;
    @XmlElement(required = true)
    protected int periodicity;
    @XmlElement(name = "late-arrival", required = true)
    protected LateArrival lateArrival;
    @XmlElement(required = true)
    protected Clusters clusters;
	@XmlJavaTypeAdapter(LocationsMapAdapter.class)
    @XmlElement(required = true)
    protected Map<LocationType,Location> locations;
    @XmlElement(name = "ACL", required = true)
    protected ACL acl;
    @XmlElement(required = true)
    protected Schema schema;
	@XmlJavaTypeAdapter(PropertiesMapAdapter.class)
    @XmlElement(required = true)
    protected Map<String,Property> properties;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String description;

    public Cluster getCluster(String clusterName) {
        if(getClusters() != null) {
            for(Cluster cluster:getClusters().getCluster())
                if(cluster.getName().equals(clusterName))
                    return cluster;
        }
        return null;
    }
    
    /**
     * Gets the value of the partitions property.
     * 
     * @return
     *     possible object is
     *     {@link Partitions }
     *     
     */
    public Partitions getPartitions() {
        return partitions;
    }

    /**
     * Sets the value of the partitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Partitions }
     *     
     */
    public void setPartitions(Partitions value) {
        this.partitions = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroups(String value) {
        this.groups = value;
    }

    /**
     * Gets the value of the frequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the value of the frequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequency(String value) {
        this.frequency = value;
    }

    /**
     * Gets the value of the periodicity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public int getPeriodicity() {
        return periodicity;
    }

    /**
     * Sets the value of the periodicity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriodicity(int value) {
        this.periodicity = value;
    }

    /**
     * Gets the value of the lateArrival property.
     * 
     * @return
     *     possible object is
     *     {@link LateArrival }
     *     
     */
    public LateArrival getLateArrival() {
        return lateArrival;
    }

    /**
     * Sets the value of the lateArrival property.
     * 
     * @param value
     *     allowed object is
     *     {@link LateArrival }
     *     
     */
    public void setLateArrival(LateArrival value) {
        this.lateArrival = value;
    }

    /**
     * Gets the value of the clusters property.
     * 
     * @return
     *     possible object is
     *     {@link Clusters }
     *     
     */
    public Clusters getClusters() {
        return clusters;
    }

    /**
     * Sets the value of the clusters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Clusters }
     *     
     */
    public void setClusters(Clusters value) {
        this.clusters = value;
    }

    /**
     * Gets the value of the locations property.
     * 
     * @return
     *     possible object is
     *     {@link Locations }
     *     
     */
    public Map<LocationType,Location> getLocations() {
        return locations;
    }

    /**
     * Sets the value of the locations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Locations }
     *     
     */
    public void setLocations(Map<LocationType,Location> value) {
        this.locations = value;
    }

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link ACL }
     *     
     */
    public ACL getACL() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACL }
     *     
     */
    public void setACL(ACL value) {
        this.acl = value;
    }

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link Schema }
     *     
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schema }
     *     
     */
    public void setSchema(Schema value) {
        this.schema = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Map<String,Property> getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Map<String,Property> value) {
        this.properties = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public String[] getImmutableProperties() {
        String[] props = {"name"};
        return props;
    }
}
