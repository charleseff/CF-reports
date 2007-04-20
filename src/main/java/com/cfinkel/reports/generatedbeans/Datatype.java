//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.31 at 02:45:59 PM EDT 
//


package com.cfinkel.reports.generatedbeans;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for datatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="datatype">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="integer"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="date"/>
 *     &lt;enumeration value="datetime"/>
 *     &lt;enumeration value="time"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum Datatype {

    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("datetime")
    DATETIME("datetime"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("integer")
    INTEGER("integer"),
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("time")
    TIME("time");
    private final String value;

    Datatype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Datatype fromValue(String v) {
        for (Datatype c: Datatype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}