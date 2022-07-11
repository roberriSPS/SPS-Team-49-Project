package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


@WebServlet("/sentiment")
public class SentimentServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = request.getParameter("message");

        Document doc =
            Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        double score = sentiment.getScore();
        languageService.close();

        String queryParameter = "";

        boolean clearlyPositive = (score >= 0.25) && (score <= 1);
        boolean clearlyNegative = (score >= -1) && (score <= -0.25);

        //ignoring magnitude, neutral emotions, and mixed emotions for now
        if(clearlyPositive) {
            queryParameter = "positive+books";
        } else if (clearlyNegative) {
            queryParameter = "self+help+books";
        } else {
            //handle neutral emotions later
            //handle mixed emotions later                
            queryParameter = "funny";
        }

        String json = "";
        try {
            json = fetchBooksApi(queryParameter);
        } catch(Exception e) {
            e.getMessage();
        }

        //respond with JSON of book based on sentiment score
        response.setContentType("application/json;");
        response.getWriter().println(json);

    }//doPost

    /**
     * Makes a GET Request to the Google books API based on a mood parameter and returns the response as a String.
     * 
     * @param queryParameter the String parameter to the Google Books API query. This will determine the search details.
     *                        For example, a queryParameter of "positive" would search the google books api for positive books.
     * @return String containing the json response from the Google Books API.
     */
    private String fetchBooksApi(String queryParameter) throws Exception {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + queryParameter;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        String responseString = "";
        try {
            responseString = EntityUtils.toString(response.getEntity());
        }finally {
            response.close();
        }
        return responseString;
    }//fetchBookApi
  
}//SentimentServlet
