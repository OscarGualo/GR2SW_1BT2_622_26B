package com.javaweb.gr2s2_1bt2_622_26b.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // Establece UTF-8 para interpretar correctamente la petición.
        request.setCharacterEncoding("UTF-8");
        // Establece UTF-8 para enviar correctamente la respuesta.
        response.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }
}
