package nl.gridshore.dwes.index.api;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Mappings identifier that calculates the identifier based on the concatenation of all sha hashes of the different
 * types.
 */
public class ShaBasedMappingsIdentifier {
    private Map<String, String> mappings;

    public ShaBasedMappingsIdentifier(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public String asString() {
        Map<String, String> sorted = new TreeMap<>(mappings);
        StringBuilder shaMappingsBuilder = new StringBuilder();
        sorted.forEach((type, mapping) -> shaMappingsBuilder.append(DigestUtils.sha1Hex(mapping)));
        return shaMappingsBuilder.toString();
    }
}
