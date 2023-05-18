package com.javarush.jira.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class MesageConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        // SessionLocaleResolver slr = new SessionLocaleResolver();
        // slr.setDefaultLocale(Locale.ENGLISH);
        // slr.setLocaleAttributeName("session.current.locale");
        // slr.setTimeZoneAttributeName("session.current.timezone");

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setDefaultTimeZone(TimeZone.getTimeZone("UTC"));

        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor
                = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

  //  @Bean("messageSource")
   // public MessageSource messageSource() {
        ///ResourceBundleMessageSource messageSource =
             //   new ResourceBundleMessageSource();
     //   messageSource.setBasenames("classpath:messages");
      //  messageSource.setDefaultEncoding("UTF-8");
     //   return messageSource;
    //}
}
