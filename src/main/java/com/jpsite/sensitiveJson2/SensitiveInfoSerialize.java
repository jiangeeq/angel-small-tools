package com.jpsite.sensitiveJson2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author jiangpeng
 * @Description Json脱敏序列化
 */
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveInfoSerialize extends JsonSerializer<Object> implements ContextualSerializer {
    private SensitiveTypeEnum type;
    private String encrypt;


    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        switch (this.type) {
            case ID_CARD:
                jsonGenerator.writeString(SensitiveInfoUtils.idCard(String.valueOf(value), encrypt));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(SensitiveInfoUtils.mobilePhone(String.valueOf(value), encrypt));
                break;
            case EMAIL:
                jsonGenerator.writeString(SensitiveInfoUtils.email(String.valueOf(value), encrypt));
                break;
            case CARD_NO:
                jsonGenerator.writeString(SensitiveInfoUtils.cardNo(String.valueOf(value), encrypt));
                break;
            case PASSWORD:
                jsonGenerator.writeString(SensitiveInfoUtils.password(String.valueOf(value), encrypt));
                break;
            case NAME:
                jsonGenerator.writeString(SensitiveInfoUtils.realName(String.valueOf(value), encrypt));
                break;
            default:
                jsonGenerator.writeString(String.valueOf(value));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);

    }
}
