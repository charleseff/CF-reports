//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.31 at 02:45:59 PM EDT 
//


package com.cfinkel.reports.generatedbeans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.cfinkel.reports.generatedbeans.InputElement;
import com.cfinkel.reports.generatedbeans.WhenElement;


/**
 * <p>Java class for dependent-input element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="dependent-input">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;extension base="{http://www.cfinkel.com/report}abstract-input">
 *         &lt;sequence>
 *           &lt;element ref="{http://www.cfinkel.com/report}when" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *       &lt;/extension>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "", propOrder = {
    "when"
})
@XmlRootElement(name = "dependent-input")
public class DependentInputElement
    extends InputElement
{

    public void setWhen(List<WhenElement> when) {
        this.when = when;
    }

    @XmlElement(namespace = "http://www.cfinkel.com/report")
    protected List<WhenElement> when;

    /**
     * Gets the value of the when property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the when property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WhenElement }
     * 
     * 
     */
    public List<WhenElement> getWhen() {
        if (when == null) {
            when = new ArrayList<WhenElement>();
        }
        return this.when;
    }

}