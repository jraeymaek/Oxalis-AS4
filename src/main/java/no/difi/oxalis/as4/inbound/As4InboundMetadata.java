package no.difi.oxalis.as4.inbound;

import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.tag.Tag;
import no.difi.oxalis.api.timestamp.Timestamp;
import no.difi.vefa.peppol.common.model.*;

import java.security.cert.X509Certificate;
import java.util.*;

public class As4InboundMetadata implements InboundMetadata {

    private final TransmissionIdentifier transmissionIdentifier;

    private final String conversationId;

    private final Header header;

    private final Date timestamp;

    private final TransportProfile transportProfile;

    private final Digest digest;

    private final Receipt primaryReceipt;

    private final List<Receipt> receipts;

    private final X509Certificate certificate;

    private final Map<String, String> messageProperties;

    public As4InboundMetadata(TransmissionIdentifier transmissionIdentifier, String conversationId, Header header, Timestamp timestamp,
                              TransportProfile transportProfile, Digest digest, X509Certificate certificate,
                              byte[] primaryReceipt, Map<String, String> messageProperties) {
        this.transmissionIdentifier = transmissionIdentifier;
        this.conversationId = conversationId;
        this.header = header;
        this.timestamp = timestamp.getDate();
        this.transportProfile = transportProfile;
        this.digest = digest;
        this.certificate = certificate;
        this.primaryReceipt = Receipt.of("message/disposition-notification", primaryReceipt);

        List<Receipt> receipts = new ArrayList<>();
        receipts.add(this.primaryReceipt);
        if (timestamp.getReceipt().isPresent())
            receipts.add(timestamp.getReceipt().get());
        this.receipts = Collections.unmodifiableList(receipts);
        this.messageProperties = Collections.unmodifiableMap(messageProperties);
    }

    @Override
    public X509Certificate getCertificate() {
        return certificate;
    }

    @Override
    public TransmissionIdentifier getTransmissionIdentifier() {
        return transmissionIdentifier;
    }

    public String getConversationId() {
        return conversationId;
    }

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public Digest getDigest() {
        return digest;
    }

    @Override
    public TransportProtocol getTransportProtocol() {
        return TransportProtocol.AS4;
    }

    @Override
    public TransportProfile getProtocol() {
        return transportProfile;
    }

    @Override
    public List<Receipt> getReceipts() {
        return receipts;
    }

    @Override
    public Receipt primaryReceipt() {
        return primaryReceipt;
    }

    @Override
    public Tag getTag() {
        return Tag.NONE;
    }

    public Map<String, String> getMessageProperties() {
        return messageProperties;
    }
}
