package nl.gridshore.dwes.index.api;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ShaBasedMappingsIdentifierTest {

    @Test
    public void testAsString() throws Exception {
        String result = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b884a516841ba77a5b4648de2cd0dfcb30ea46dbb454fd1711209fb1c0781092374132c66e79e2241b395df8f7c51f007019cb30201c49e884b46b92fa";

        Map<String, String> mappings = new HashMap<>();
        mappings.put("a", "a");
        mappings.put("z", "z");
        mappings.put("g", "g");
        mappings.put("c", "c");
        ShaBasedMappingsIdentifier identifier = new ShaBasedMappingsIdentifier(mappings);

        assertEquals(result, identifier.asString());
    }
}