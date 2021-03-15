package bear.dora.mask.utils;

import bear.dora.mask.annotations.Mask;
import bear.dora.mask.annotations.PII;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskUtils {

    @SuppressWarnings("unchecked")
    public static Object getValueAfterMask(Object object) {
        if(Objects.nonNull(object)) {
            if(object instanceof List) {
                List<Object> objects = (List<Object>) object;
                boolean isMask = objects.stream()
                        .anyMatch(pre -> isAnnotationMask(pre.getClass()));
                if(isMask) {
                    List<Object> clones = deepCloneList(objects);
                    clones.forEach(MaskUtils::maskHaveMaskAnnotation);
                    return clones;
                }
            } else if(MaskUtils.isAnnotationMask(object.getClass())
                    && object instanceof Serializable) {
                Object objClone =  SerializationUtils.clone((Serializable)object);
                return MaskUtils.maskHaveMaskAnnotation(objClone);
            }
        }
        return object;
    }

    private static List<Object> deepCloneList(List<Object> objects) {
        List<Object> clones = new ArrayList<>();
        for (Object obj : objects) {
            if(MaskUtils.isAnnotationMask(obj.getClass())
                    && obj instanceof Serializable) {
                Object objClone = SerializationUtils.clone((Serializable)obj);
                clones.add(objClone);
            }
        }
        return clones;
    }

    private static boolean isAnnotationMask(Class<?> tclass){
        return Objects.nonNull(tclass) && tclass.isAnnotationPresent(Mask.class);
    }

    private static Object maskHaveMaskAnnotation(Object object) {
        Class<?> typeClass = object.getClass();
        if(isAnnotationMask(typeClass)) {
            setPCIMask(object, typeClass);
            Class<?> superClass = typeClass.getSuperclass();
            if (isAnnotationMask(superClass)) {
                setPCIMask(object, superClass);
            }
        }
        return object;
    }

    private static void setPCIMask(Object object, Class<?> tclass) {
        Stream.of(tclass.getDeclaredFields())
                .forEach(field -> {
                    field.setAccessible(true);
                        try {
                            Object value = field.get(object);
                            if (field.isAnnotationPresent(PII.class)) {
                                if (value instanceof String) {
                                    PII pii = field.getAnnotation(PII.class);
                                    String mask = pii.pattern();
                                    if(pii.keepLastDigits() != 0){
                                        mask = keepLastDigits(String.valueOf(value),pii.special(),pii.keepLastDigits());
                                    }
                                    field.set(object, mask);
                                } else if (value instanceof List) {
                                    field.set(object, null);
                                }
                            } else if (value instanceof Serializable && isAnnotationMask(value.getClass())) {
                              maskHaveMaskAnnotation(value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                });
    }

    private static String keepLastDigits(String input, String specialCharacter, int keepLastDigits) {
        if(Objects.nonNull(input)) {
            int length = input.length();
            return StringUtils.overlay(input,
                    StringUtils.repeat(specialCharacter, length - keepLastDigits), 0, length - keepLastDigits);
        }
        return StringUtils.EMPTY;
    }
}