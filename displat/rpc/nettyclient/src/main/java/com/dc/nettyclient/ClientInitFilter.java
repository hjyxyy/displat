package com.dc.nettyclient;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.beans.factory.annotation.Autowired;

import io.netty.channel.ChannelFuture;

/**
 * 通过过滤器初始化client
 * @author gavin
 *
 */
@WebFilter(filterName = "clientInit", urlPatterns = "/*")
public class ClientInitFilter {
	@Autowired
	private Client client;
	
	private ChannelFuture f;
	
	public void destroy() {
		if(f!=null){
			try {
				f.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
		try {
			f=client.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
