package nl.gridshore.dwes.index.api;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Settings identifier that uses Sha1 to create a unique identifier for the provided settings.
 */
public class ShaBasedSettingsIdentifier {
    private String settings;

    public ShaBasedSettingsIdentifier(String settings) {
        this.settings = settings;
    }

    public String asString() {
        return DigestUtils.sha1Hex(settings);
    }
}
