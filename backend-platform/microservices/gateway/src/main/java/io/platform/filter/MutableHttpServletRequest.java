package io.platform.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, List<String>> customHeaders = new HashMap<>();

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void putHeader(String name, String value) {
        customHeaders.put(name, List.of(value));
    }

    public void removeHeader(String name) {
        customHeaders.remove(name);
    }

    @Override
    public String getHeader(String name) {
        List<String> values = customHeaders.get(name);
        return values != null && !values.isEmpty()
                ? values.getFirst()
                : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = customHeaders.get(name);
        return values != null
                ? Collections.enumeration(values)
                : super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new LinkedHashSet<>();
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        names.addAll(customHeaders.keySet());
        return Collections.enumeration(names);
    }
}
