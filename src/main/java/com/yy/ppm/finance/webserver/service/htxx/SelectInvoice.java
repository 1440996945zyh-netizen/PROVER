package com.yy.ppm.finance.webserver.service.htxx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for selectInvoice complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="selectInvoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "selectInvoice", propOrder = { "number" })
public class SelectInvoice {

	protected String number;

	/**
	 * Gets the value of the number property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the value of the number property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setNumber(String value) {
		this.number = value;
	}

}
