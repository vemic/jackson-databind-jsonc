package jp.vemi.jsoncmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JsoncMapperTest {
    @Test
    public void testReadValue() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" }";
        MyClass result = mapper.readValue(jsonc, MyClass.class);
        assertEquals("value", result.getKey());
    }

    @Test
    public void testReadValueWithMultipleComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\", /* another comment */ \"key2\": \"value2\" }";
        MyClassWithTwoKeys result = mapper.readValue(jsonc, MyClassWithTwoKeys.class);
        assertEquals("value", result.getKey());
        assertEquals("value2", result.getKey2());
    }

    @Test
    public void testReadValueWithNestedComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": { /* nested comment */ \"nestedKey\": \"nestedValue\" } }";
        MyClassWithNestedKey result = mapper.readValue(jsonc, MyClassWithNestedKey.class);
        assertEquals("nestedValue", result.getKey().getNestedKey());
    }

    @Test
    public void testReadValueWithEmptyJson() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{}";
        MyClass result = mapper.readValue(jsonc, MyClass.class);
        assertNull(result.getKey());
    }

    @Test
    public void testReadValueWithInvalidJson() {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" ";
        assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(jsonc, MyClass.class);
        });
    }

    @Test
    public void testNullInputValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((String) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue("{}", (Class<MyClass>) null);
        });
    }
    
    @Test
    public void testReadValueWithTypeReference() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "[ /* comment */ \"value1\", \"value2\" ]";
        TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
        List<String> result = mapper.readValue(jsonc, typeRef);
        assertEquals(2, result.size());
        assertEquals("value1", result.get(0));
        assertEquals("value2", result.get(1));
    }
    
    @Test
    public void testReadValueWithTypeReferenceNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((String) null, new TypeReference<List<String>>() {});
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue("[]", (TypeReference<List<String>>) null);
        });
    }
    
    @Test
    public void testReadValueWithJavaType() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"key\": \"value\" }";
        JavaType javaType = mapper.getTypeFactory().constructType(MyClass.class);
        MyClass result = mapper.readValue(jsonc, javaType);
        assertEquals("value", result.getKey());
    }
    
    @Test
    public void testReadValueWithJavaTypeNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((String) null, mapper.getTypeFactory().constructType(MyClass.class));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue("{}", (JavaType) null);
        });
    }
    
    @Test
    public void testReadValueFromFile(@TempDir Path tempDir) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        File testFile = tempDir.resolve("test.jsonc").toFile();
        
        // Write JSONC content to file
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(jsoncContent);
        }
        
        // Test reading from file
        MyClass result = mapper.readValue(testFile, MyClass.class);
        assertEquals("value", result.getKey());
    }
    
    @Test
    public void testReadValueFromFileWithTypeReference(@TempDir Path tempDir) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        File testFile = tempDir.resolve("test_list.jsonc").toFile();
        
        // Write JSONC content to file
        String jsoncContent = "[ /* comment */ \"value1\", \"value2\" ]";
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(jsoncContent);
        }
        
        // Test reading from file
        TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
        List<String> result = mapper.readValue(testFile, typeRef);
        assertEquals(2, result.size());
        assertEquals("value1", result.get(0));
        assertEquals("value2", result.get(1));
    }
    
    @Test
    public void testReadValueFromFileWithJavaType(@TempDir Path tempDir) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        File testFile = tempDir.resolve("test_javatype.jsonc").toFile();
        
        // Write JSONC content to file
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(jsoncContent);
        }
        
        // Test reading from file
        JavaType javaType = mapper.getTypeFactory().constructType(MyClass.class);
        MyClass result = mapper.readValue(testFile, javaType);
        assertEquals("value", result.getKey());
    }
    
    @Test
    public void testReadValueFromFileNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((File) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            File tempFile = new File("temp.json");
            mapper.readValue(tempFile, (Class<MyClass>) null);
        });
    }
    
    @Test
    public void testReadValueFromReader() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (StringReader reader = new StringReader(jsoncContent)) {
            MyClass result = mapper.readValue(reader, MyClass.class);
            assertEquals("value", result.getKey());
        }
    }
    
    @Test
    public void testReadValueFromReaderWithTypeReference() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "[ /* comment */ \"value1\", \"value2\" ]";
        
        try (StringReader reader = new StringReader(jsoncContent)) {
            TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
            List<String> result = mapper.readValue(reader, typeRef);
            assertEquals(2, result.size());
            assertEquals("value1", result.get(0));
            assertEquals("value2", result.get(1));
        }
    }
    
    @Test
    public void testReadValueFromReaderWithJavaType() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (StringReader reader = new StringReader(jsoncContent)) {
            JavaType javaType = mapper.getTypeFactory().constructType(MyClass.class);
            MyClass result = mapper.readValue(reader, javaType);
            assertEquals("value", result.getKey());
        }
    }
    
    @Test
    public void testReadValueFromReaderNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((Reader) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            StringReader reader = new StringReader("{}");
            mapper.readValue(reader, (Class<MyClass>) null);
        });
    }
    
    @Test
    public void testReadValueFromInputStream() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsoncContent.getBytes())) {
            MyClass result = mapper.readValue(inputStream, MyClass.class);
            assertEquals("value", result.getKey());
        }
    }
    
    @Test
    public void testReadValueFromInputStreamWithTypeReference() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "[ /* comment */ \"value1\", \"value2\" ]";
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsoncContent.getBytes())) {
            TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
            List<String> result = mapper.readValue(inputStream, typeRef);
            assertEquals(2, result.size());
            assertEquals("value1", result.get(0));
            assertEquals("value2", result.get(1));
        }
    }
    
    @Test
    public void testReadValueFromInputStreamWithJavaType() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsoncContent.getBytes())) {
            JavaType javaType = mapper.getTypeFactory().constructType(MyClass.class);
            MyClass result = mapper.readValue(inputStream, javaType);
            assertEquals("value", result.getKey());
        }
    }
    
    @Test
    public void testReadValueFromInputStreamNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((InputStream) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            ByteArrayInputStream inputStream = new ByteArrayInputStream("{}".getBytes());
            mapper.readValue(inputStream, (Class<MyClass>) null);
        });
    }
    
    @Test
    public void testReadValueFromByteArray() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        byte[] bytes = jsoncContent.getBytes();
        
        MyClass result = mapper.readValue(bytes, MyClass.class);
        assertEquals("value", result.getKey());
    }
    
    @Test
    public void testReadValueFromByteArrayWithTypeReference() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "[ /* comment */ \"value1\", \"value2\" ]";
        byte[] bytes = jsoncContent.getBytes();
        
        TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
        List<String> result = mapper.readValue(bytes, typeRef);
        assertEquals(2, result.size());
        assertEquals("value1", result.get(0));
        assertEquals("value2", result.get(1));
    }
    
    @Test
    public void testReadValueFromByteArrayWithJavaType() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        byte[] bytes = jsoncContent.getBytes();
        
        JavaType javaType = mapper.getTypeFactory().constructType(MyClass.class);
        MyClass result = mapper.readValue(bytes, javaType);
        assertEquals("value", result.getKey());
    }
    
    @Test
    public void testReadValueFromByteArrayNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readValue((byte[]) null, MyClass.class);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            byte[] bytes = "{}".getBytes();
            mapper.readValue(bytes, (Class<MyClass>) null);
        });
    }
    
    @Test
    public void testReadTreeFromString() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        JsonNode result = mapper.readTree(jsoncContent);
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals("value", result.get("key").asText());
    }
    
    @Test
    public void testReadTreeFromStringNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readTree((String) null);
        });
    }
    
    @Test
    public void testReadTreeFromFile(@TempDir Path tempDir) throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        File testFile = tempDir.resolve("test_tree.jsonc").toFile();
        
        // Write JSONC content to file
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(jsoncContent);
        }
        
        // Test reading from file
        JsonNode result = mapper.readTree(testFile);
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals("value", result.get("key").asText());
    }
    
    @Test
    public void testReadTreeFromFileNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readTree((File) null);
        });
    }
    
    @Test
    public void testReadTreeFromReader() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (StringReader reader = new StringReader(jsoncContent)) {
            JsonNode result = mapper.readTree(reader);
            assertNotNull(result);
            assertTrue(result.isObject());
            assertEquals("value", result.get("key").asText());
        }
    }
    
    @Test
    public void testReadTreeFromReaderNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readTree((Reader) null);
        });
    }
    
    @Test
    public void testReadTreeFromInputStream() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsoncContent.getBytes())) {
            JsonNode result = mapper.readTree(inputStream);
            assertNotNull(result);
            assertTrue(result.isObject());
            assertEquals("value", result.get("key").asText());
        }
    }
    
    @Test
    public void testReadTreeFromInputStreamNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readTree((InputStream) null);
        });
    }
    
    @Test
    public void testReadTreeFromByteArray() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsoncContent = "{ /* comment */ \"key\": \"value\" }";
        byte[] bytes = jsoncContent.getBytes();
        
        JsonNode result = mapper.readTree(bytes);
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals("value", result.get("key").asText());
    }
    
    @Test
    public void testReadTreeFromByteArrayNullValidation() {
        JsoncMapper mapper = new JsoncMapper();
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.readTree((byte[]) null);
        });
    }

    // Additional Integration Tests
    @Test
    public void testReadValueWithCommentsInArray() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ \"items\": [ /* comment */ \"item1\", /* another */ \"item2\" ] }";
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertTrue(result.has("items"));
        assertTrue(result.get("items").isArray());
        assertEquals(2, result.get("items").size());
        assertEquals("item1", result.get("items").get(0).asText());
        assertEquals("item2", result.get("items").get(1).asText());
    }

    @Test
    public void testReadValueWithComplexNesting() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = """
            {
                /* root comment */
                "data": {
                    /* nested comment */
                    "users": [
                        /* array comment */
                        {
                            /* object comment */
                            "name": "John",
                            "age": 30
                        }
                    ]
                }
            }
            """;
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertEquals("John", result.get("data").get("users").get(0).get("name").asText());
        assertEquals(30, result.get("data").get("users").get(0).get("age").asInt());
    }

    @Test
    public void testReadValueWithUnicodeComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* 日本語コメント */ \"名前\": \"値\", /* 🎉 emoji comment */ \"emoji\": \"🌟\" }";
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertEquals("値", result.get("名前").asText());
        assertEquals("🌟", result.get("emoji").asText());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    public void testReadValuePerformanceWithManyComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        StringBuilder jsonc = new StringBuilder("{ ");
        
        for (int i = 0; i < 100; i++) {
            jsonc.append("/* comment ").append(i).append(" */ ");
            jsonc.append("\"key").append(i).append("\": \"value").append(i).append("\"");
            if (i < 99) {
                jsonc.append(", ");
            }
        }
        jsonc.append(" }");
        
        JsonNode result = mapper.readTree(jsonc.toString());
        assertNotNull(result);
        assertEquals(100, result.size());
        assertEquals("value50", result.get("key50").asText());
    }

    @Test
    public void testReadValueWithCommentsAndStringEscapes() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* comment */ \"escaped\": \"He said \\\"/* not removed */\\\" today\" }";
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertEquals("He said \"/* not removed */\" today", result.get("escaped").asText());
    }

    @Test
    public void testReadValueEmptyComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /**/ \"key\": \"value\" /**/ }";
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertEquals("value", result.get("key").asText());
    }

    @Test
    public void testReadValueConsecutiveComments() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        String jsonc = "{ /* first *//* second *//* third */ \"key\": \"value\" }";
        
        JsonNode result = mapper.readTree(jsonc);
        assertNotNull(result);
        assertEquals("value", result.get("key").asText());
    }

    static class MyClass {
        private String key;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }

    static class MyClassWithTwoKeys {
        private String key;
        private String key2;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getKey2() { return key2; }
        public void setKey2(String key2) { this.key2 = key2; }
    }

    static class MyClassWithNestedKey {
        private NestedKey key;
        public NestedKey getKey() { return key; }
        public void setKey(NestedKey key) { this.key = key; }
    }

    static class NestedKey {
        private String nestedKey;
        public String getNestedKey() { return nestedKey; }
        public void setNestedKey(String nestedKey) { this.nestedKey = nestedKey; }
    }
}