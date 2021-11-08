package com.skg.apimonkey.service.util;

import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Slf4j
public class RequestValuesUtil {

    //supported media type
    private final static String IN_HEAD_TYPE = "header";
    private final static String IN_PATH_TYPE = "path";
    private final static String IN_QUERY_TYPE = "query";
    private final static String EMAIL = "email";
    private final static String STRING = "string";
    private final static String INTEGER = "integer";
    private final static String NUMBER = "number";
    private final static String BOOLEAN = "boolean";
    private final static String DATE = "date";
    private final static String DATETIME = "date-time";

    private final static List<String> STRING_TYPE_VALUES = Arrays.asList("test", "", "null", "88888", "random test value", "long strin---------------------------g", "string");
    private final static List<String> EMAIL_TYPE_VALUES = Arrays.asList("test@test.com", "valid.12345.email@test.com", "invalid.email@@@id.test", "null", "", "email.test@gmail.test");
    private final static List<String> INTEGER_TYPE_VALUES = Arrays.asList("null", "A-random-string", "-1", "99", "888888888888", "7547", "0", "102");
    private final static List<String> NUMBER_TYPE_VALUES = Arrays.asList("0.235", "3.1415926", "-1.36", "99.4565", "00.9999999", "null", "", "102");

    public static Object getValueByType(Schema schema, int variantNumber) {

        if(Objects.isNull(schema) || StringUtils.isEmpty(schema.getType())) {
            log.warn("Error creating value from scheme type!");
            return "";
        }
        Object result;
        switch (schema.getType()) {
            case INTEGER:
                result = getIntegerVariant(schema, variantNumber);
                break;
            case STRING:
                if (StringUtils.equalsIgnoreCase(schema.getFormat(), EMAIL)) {
                    result = getEmailVariant(schema, variantNumber);

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), DATETIME)) {
                    result = getDateTimeVariant(schema, variantNumber, DATETIME);

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), DATE)) {
                    result = getDateTimeVariant(schema, variantNumber, DATE);

                } else {
                    result = getStringVariant((StringSchema) schema, variantNumber);
                }
                break;
            case NUMBER:
                result = getNumberVariant(schema, variantNumber);
                break;
            case BOOLEAN:
                result = getBooleanVariant(schema, variantNumber);
                break;
            default:
                log.warn("Unrecognized type: {} !", schema.getType());
                result = "";
                break;
        }
        return result;
    }

    public static String getStringVariant(StringSchema schema, int variantNumber) {

        if (variantNumber == 0) {
            if (StringUtils.isNotEmpty(schema.getDefault())) {
                return schema.getDefault();
            }
            if (Objects.nonNull(schema.getExample())) {
                return String.valueOf(schema.getExample());
            }
            if (CollectionUtils.isNotEmpty(schema.getEnum())) {
                return schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size()));
            }
            return STRING_TYPE_VALUES.get(0);
        }

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size()));
        }

        return STRING_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(STRING_TYPE_VALUES.size()));
    }

    public static String getNumberVariant(Schema schema, int variantNumber) {

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return String.valueOf(schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size())));
        }

        if (variantNumber == 0) {
            if (Objects.nonNull(schema.getDefault())) {
                return String.valueOf(schema.getDefault());
            }
            return Objects.isNull(schema.getExample()) ?
                    "1.0" :
                    String.valueOf(schema.getExample());
        }
        return NUMBER_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(NUMBER_TYPE_VALUES.size()));
    }

    public static String getIntegerVariant(Schema schema, int variantNumber) {

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return String.valueOf(schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size())));
        }

        if (variantNumber == 0) {
            if (Objects.nonNull(schema.getDefault())) {
                return String.valueOf(schema.getDefault());
            }
            return Objects.isNull(schema.getExample()) ?
                    "1" :
                    String.valueOf(schema.getExample());
        }
        return INTEGER_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(INTEGER_TYPE_VALUES.size()));
    }

    public static String getEmailVariant(Schema schema, int variantNumber) {

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return String.valueOf(schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size())));
        }

        if (variantNumber == 0) {
            if (Objects.nonNull(schema.getDefault())) {
                return String.valueOf(schema.getDefault());
            }
            return Objects.isNull(schema.getExample()) ?
                    EMAIL_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(EMAIL_TYPE_VALUES.size())) :
                    String.valueOf(schema.getExample());
        }
        return EMAIL_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(EMAIL_TYPE_VALUES.size()));
    }

    public static String getDateTimeVariant(Schema schema, int variantNumber, String dateType) {

        String pattern = StringUtils.equalsIgnoreCase(dateType, DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return String.valueOf(schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size())));
        }
        Date date = new Date();
        date = DateUtils.addYears(date, ThreadLocalRandom.current().nextInt(10));
        date = DateUtils.addMonths(date, ThreadLocalRandom.current().nextInt(12));
        date = DateUtils.addDays(date, ThreadLocalRandom.current().nextInt(28));
        date = DateUtils.addHours(date, ThreadLocalRandom.current().nextInt(24));
        date = DateUtils.addMinutes(date, ThreadLocalRandom.current().nextInt(60));
        date = DateUtils.addSeconds(date, ThreadLocalRandom.current().nextInt(60));

        if (variantNumber == 0) {
            return Objects.isNull(schema.getExample()) ?
                    DateFormatUtils.format(date, pattern) :
                    String.valueOf(schema.getExample());
        }
        return DateFormatUtils.format(date, pattern);
    }

    public static String getBooleanVariant(Schema schema, int variantNumber) {

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return String.valueOf(schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size())));
        }

        if (variantNumber == 0) {
            if (Objects.nonNull(schema.getDefault())) {
                return String.valueOf(schema.getDefault());
            }
            return Objects.isNull(schema.getExample()) ?
                    String.valueOf(ThreadLocalRandom.current().nextBoolean()) :
                    String.valueOf(schema.getExample());
        }
        return String.valueOf(ThreadLocalRandom.current().nextBoolean());
    }

    public static boolean isObject(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) ||
                StringUtils.equalsIgnoreCase(schema.getType(), "object") ||
                MapUtils.isNotEmpty(schema.getProperties()) ||
                StringUtils.isNotEmpty(schema.get$ref());
    }

    public static List<Parameter> getInHeadParameters(List<Parameter> parameters) {
        if (CollectionUtils.isNotEmpty(parameters)) {
            return parameters.stream()
                    .filter(i -> StringUtils.equalsIgnoreCase(i.getIn(), IN_HEAD_TYPE))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<Parameter> getInPathOrQueryParameters(List<Parameter> parameters) {
        if (CollectionUtils.isNotEmpty(parameters)) {
            return parameters.stream()
                    .filter(i -> StringUtils.equalsIgnoreCase(i.getIn(), IN_PATH_TYPE) || StringUtils.equalsIgnoreCase(i.getIn(), IN_QUERY_TYPE))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}