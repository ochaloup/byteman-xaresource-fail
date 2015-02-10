package org.jboss.qa.ochaloup.web;


import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.qa.ochaloup.service.Worker;


@WebServlet(name="TestingServlet", urlPatterns={"/"})
public class Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @EJB
    Worker workerBean;
        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        workerBean.doWork();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<h4>Work of bean is done!</h4>");
    }
}
