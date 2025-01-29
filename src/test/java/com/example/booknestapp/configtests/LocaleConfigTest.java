package com.example.booknestapp.configtests;

import com.example.booknestapp.localization.LocaleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LocaleConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        // Initialize Spring context with LocaleConfig
        context = new AnnotationConfigApplicationContext(LocaleConfig.class);
    }

    @Test
    void testLocaleResolver() {
        LocaleResolver localeResolver = context.getBean(LocaleResolver.class);
        assertNotNull(localeResolver, "LocaleResolver bean should not be null");
        assertTrue(localeResolver instanceof CookieLocaleResolver, "LocaleResolver should be of type CookieLocaleResolver");

        CookieLocaleResolver cookieLocaleResolver = (CookieLocaleResolver) localeResolver;

        // Simulate resolving locale without cookies
        var mockRequest = new org.springframework.mock.web.MockHttpServletRequest();
        Locale resolvedLocale = cookieLocaleResolver.resolveLocale(mockRequest);
        assertNotNull(resolvedLocale, "Resolved locale should not be null");
        assertEquals(Locale.ENGLISH, resolvedLocale, "Resolved locale should be English");
    }


    @Test
    void testMessageSource() {
        var messageSource = context.getBean("messageSource");
        assertNotNull(messageSource, "MessageSource bean should not be null");
        assertTrue(messageSource instanceof org.springframework.context.support.ReloadableResourceBundleMessageSource,
                "MessageSource should be of type ReloadableResourceBundleMessageSource");
    }

    @Test
    void testLocaleChangeInterceptor() {
        LocaleChangeInterceptor interceptor = context.getBean(LocaleChangeInterceptor.class);
        assertNotNull(interceptor, "LocaleChangeInterceptor bean should not be null");
        assertEquals("lang", interceptor.getParamName(), "LocaleChangeInterceptor parameter name should be 'lang'");
    }
}
