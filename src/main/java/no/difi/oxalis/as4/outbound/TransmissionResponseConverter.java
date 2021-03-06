package no.difi.oxalis.as4.outbound;

import com.google.inject.Inject;
import no.difi.oxalis.api.lang.TimestampException;
import no.difi.oxalis.api.model.Direction;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.timestamp.Timestamp;
import no.difi.oxalis.api.timestamp.TimestampProvider;
import no.difi.oxalis.as4.lang.OxalisAs4TransmissionException;
import no.difi.oxalis.as4.util.AS4ErrorCode;
import no.difi.oxalis.as4.util.Marshalling;
import no.difi.oxalis.commons.bouncycastle.BCHelper;
import no.difi.vefa.peppol.common.code.DigestMethod;
import no.difi.vefa.peppol.common.model.Digest;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static no.difi.oxalis.as4.util.Constants.DIGEST_ALGORITHM_SHA256;

public class TransmissionResponseConverter {

    private final JAXBContext jaxbContext = Marshalling.getInstance();
    private final TimestampProvider timestampProvider;

    @Inject
    public TransmissionResponseConverter(TimestampProvider timestampProvider) {
        this.timestampProvider = timestampProvider;
    }

    public TransmissionResponse convert(TransmissionRequest request, SOAPMessage response) throws OxalisAs4TransmissionException {
        SignalMessage signalMessage = getSignalMessage(response);

        String refToMessageId = signalMessage.getMessageInfo().getRefToMessageId();
        TransmissionIdentifier ti = TransmissionIdentifier.of(refToMessageId);

        if (!signalMessage.getError().isEmpty()) {
            Error error = signalMessage.getError().get(0);

            throw new OxalisAs4TransmissionException(
                    error.getErrorDetail(),
                    AS4ErrorCode.nameOf(error.getErrorCode()),
                    AS4ErrorCode.Severity.nameOf(error.getSeverity()));
        }

        Timestamp ts = getTimestamp();
        Digest digest = getDigest();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            response.writeTo(bos);
        } catch (SOAPException | IOException e) {
            throw new OxalisAs4TransmissionException("Could not write response", e);
        }

        return new As4TransmissionResponse(
                ti,
                request,
                digest,
                bos.toByteArray(),
                ts,
                ts.getDate()
        );
    }

    private Digest getDigest() throws OxalisAs4TransmissionException {
        try {
            MessageDigest md = BCHelper.getMessageDigest(DIGEST_ALGORITHM_SHA256);
            return Digest.of(DigestMethod.SHA256, md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new OxalisAs4TransmissionException("Could not create message digest", e);
        }
    }

    private Timestamp getTimestamp() throws OxalisAs4TransmissionException {
        try {
            return timestampProvider.generate(null, Direction.OUT);
        } catch (TimestampException e) {
            throw new OxalisAs4TransmissionException("Could not create timestamp", e);
        }
    }

    private SignalMessage getSignalMessage(SOAPMessage soapMessage) throws OxalisAs4TransmissionException {
        Node signalNode = getSignalNode(soapMessage);

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(signalNode, SignalMessage.class).getValue();
        } catch (JAXBException e) {
            throw new OxalisAs4TransmissionException("Could not create unmarshaller", e);
        }
    }

    private Node getSignalNode(SOAPMessage soapMessage) throws OxalisAs4TransmissionException {
        try {
            NodeList signalNodeList = soapMessage.getSOAPHeader().getElementsByTagNameNS("*", "SignalMessage");
            if (signalNodeList.getLength() != 1) {
                throw new OxalisAs4TransmissionException("SOAP header contains zero or multiple SignalMessage elements, should only contain one");
            }
            return signalNodeList.item(0);
        } catch (SOAPException e) {
            throw new OxalisAs4TransmissionException("Could not access response body", e);
        }
    }
}
