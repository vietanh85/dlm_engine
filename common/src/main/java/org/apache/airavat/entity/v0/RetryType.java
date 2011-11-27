//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.11.27 at 11:30:16 PM GMT+05:30 
//


package org.apache.airavat.entity.v0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for retryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="retryType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="policy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delay" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delayUnit" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="attempts" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "retryType", propOrder = {
    "value"
})
public class RetryType {

    @XmlValue
    protected String value;
    @XmlAttribute
    protected String policy;
    @XmlAttribute
    protected String delay;
    @XmlAttribute
    protected String delayUnit;
    @XmlAttribute
    protected String attempts;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the policy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * Sets the value of the policy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicy(String value) {
        this.policy = value;
    }

    /**
     * Gets the value of the delay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelay() {
        return delay;
    }

    /**
     * Sets the value of the delay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelay(String value) {
        this.delay = value;
    }

    /**
     * Gets the value of the delayUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelayUnit() {
        return delayUnit;
    }

    /**
     * Sets the value of the delayUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelayUnit(String value) {
        this.delayUnit = value;
    }

    /**
     * Gets the value of the attempts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttempts() {
        return attempts;
    }

    /**
     * Sets the value of the attempts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttempts(String value) {
        this.attempts = value;
    }

}
