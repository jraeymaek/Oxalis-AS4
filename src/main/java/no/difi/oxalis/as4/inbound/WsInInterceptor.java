package no.difi.oxalis.as4.inbound;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.common.crypto.Crypto;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class WsInInterceptor extends WSS4JInInterceptor {

    private Crypto crypto;
    private String alias;

    WsInInterceptor(Map<String,Object> props, Crypto crypto, String alias) {
        super(props);
        this.crypto = crypto;
        this.alias = alias;
    }

    @Override
    public void handleMessage(SoapMessage msg) throws Fault {
        msg.put(SecurityConstants.ENCRYPT_CRYPTO, crypto);
        msg.put(SecurityConstants.SIGNATURE_CRYPTO, crypto);
        msg.put(SecurityConstants.ENCRYPT_USERNAME, alias);
        super.handleMessage(msg);

        SOAPMessage soapMessage = msg.getContent(SOAPMessage.class);
        if (soapMessage != null) {
            if (soapMessage.countAttachments() > 0) {
                Iterator<AttachmentPart> it = CastUtils.cast(soapMessage.getAttachments());
                while (it.hasNext()) {
                    AttachmentPart part = it.next();
                    Optional<Attachment> first = msg.getAttachments().stream()
                            .filter(a -> a.getId().equals(part.getContentId()))
                            .findFirst();
                    first.ifPresent(a -> part.setDataHandler(a.getDataHandler()));
                }
            }
        }
    }
}
