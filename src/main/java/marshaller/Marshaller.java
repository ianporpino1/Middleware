package marshaller;

public class Marshaller {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

//    public <T> String serialize(T obj){
//        JSONObject jsonObject = new JSONObject(obj);
//        return jsonObject.toString();
//    }
//    public <T> T deserialize(String json, Class<T> clazz){
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            T obj = clazz.getDeclaredConstructor().newInstance();
//            for (Field field : clazz.getDeclaredFields()) {
//                field.setAccessible(true);
//                if (jsonObject.has(field.getName())) {
//                    field.set(obj, jsonObject.get(field.getName()));
//                }
//            }
//            return obj;
//        } catch (Exception e) {
//            throw new RuntimeException("Erro durante a desserialização", e);
//        }
//    }

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public byte[] serialize(Object data) throws JsonProcessingException {
//        return objectMapper.writeValueAsBytes(data);
//    }
//
//    @Override
//    public <T> T deserialize(String data, Class<T> clazz) throws JsonProcessingException {
//        return objectMapper.readValue( data, clazz);
//    }
}