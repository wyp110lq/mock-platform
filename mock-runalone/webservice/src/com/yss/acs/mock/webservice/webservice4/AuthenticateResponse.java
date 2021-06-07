
package com.yss.acs.mock.webservice.webservice4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticateResult" type="{http://tempuri.org/}AuthenticationModel" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "authenticateResult"
})
@XmlRootElement(name = "AuthenticateResponse")
public class AuthenticateResponse {

    @XmlElement(name = "AuthenticateResult")
    protected AuthenticationModel authenticateResult;

    /**
     * 获取authenticateResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationModel }
     *     
     */
    public AuthenticationModel getAuthenticateResult() {
        return authenticateResult;
    }

    /**
     * 设置authenticateResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationModel }
     *     
     */
    public void setAuthenticateResult(AuthenticationModel value) {
        this.authenticateResult = value;
    }

}
