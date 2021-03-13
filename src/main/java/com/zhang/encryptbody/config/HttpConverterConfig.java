//package com.zhang.encryptbody.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpOutputMessage;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.HttpMessageNotWritableException;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.nio.charset.Charset;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * <p>响应体数据处理，防止数据类型为String时再进行JSON数据转换，那么产生最终的结果可能被双引号包含...</p>
// *
// */
//@Configuration
//public class HttpConverterConfig implements WebMvcConfigurer {
//
//    /**
//     * 重写 MappingJackson2HttpMessageConverter, 不对String在做Json转换
//     * @return
//     */
//    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//        return new MappingJackson2HttpMessageConverter() {
//            @Override
//            protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
//                if (object instanceof String) {
//                    Charset charset = this.getDefaultCharset();
//                    StreamUtils.copy((String) object, charset, outputMessage.getBody());
//                } else {
//                    super.writeInternal(object, type, outputMessage);
//                }
//            }
//        };
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter converter = mappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(new LinkedList<MediaType>() {{
//            add(MediaType.TEXT_HTML);
//            add(MediaType.APPLICATION_JSON_UTF8);
//        }});
//        converters.add(new StringHttpMessageConverter());
//        converters.add(converter);
//    }
//}
