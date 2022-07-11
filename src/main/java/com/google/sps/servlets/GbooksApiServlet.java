package com.google.sps.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


@WebServlet("/getBooks")
public class GbooksApiServlet extends HttpServlet{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String query = request.getParameter("bookSelected");
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
    
        try {
            System.out.println(response1.getStatusLine());
            String responseString = EntityUtils.toString(response1.getEntity());
            System.out.println(responseString);
            response.setContentType("text/html;");
            json = gson.toJson(responseString);
            response.setContentType("application/json;");
            response.getWriter().println(json);
            response.getWriter().println("<p>You entered: " + responseString + "</p>");
        }finally {
            response1.close();
        }
    }//doPost
}//GbooksApiServlet
