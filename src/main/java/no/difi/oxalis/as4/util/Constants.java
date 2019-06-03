package no.difi.oxalis.as4.util;

import javax.xml.namespace.QName;

public interface Constants {

    // CEF-test
//    String PARTY_ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:unregistered"; // CEF connectivitytest
//    String SERVICE_TYPE = "connectivity-procid-qns"; // CEF connectivitytest
//    String AGREEMENT_REF = null;

    // PEPPOL
    String PARTY_ID_TYPE = "urn:fdc:peppol.eu:2017:identifiers:ap";
    String AGREEMENT_REF = "urn:fdc:peppol.eu:2017:agreements:tia:ap_provider";
    String SERVICE_TYPE = "cenbii-procid-ubl";

    // Common
    String FROM_ROLE = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    String TO_ROLE = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";

    String EBMS_NAMESPACE = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/";

    QName MESSAGING_QNAME = new QName(EBMS_NAMESPACE, "Messaging", "eb");
    QName USER_MESSAGE_QNAME = new QName(EBMS_NAMESPACE, "UserMessage");
    QName SIGNAL_MESSAGE_QNAME = new QName(EBMS_NAMESPACE, "SignalMessage");

    String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    String DIGEST_ALGORITHM_SHA256 = "sha256";

    String TEST_SERVICE = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/service";
    String TEST_ACTION = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/test";
}
