package com.google.sps.servlets;

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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //get sentiment
        String message = request.getParameter("message");

        Document doc =
            Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        double score = sentiment.getScore();
        double magnitude = sentiment.getMagnitude();
        languageService.close();

        //determine query parameter for the books api call
        String queryParameter = determineQueryParameter(score, magnitude);

        //fetch books api based on query parameter
        String json = "";
        try {
            json = fetchBooksApi(queryParameter);
        } catch(Exception e) {
            e.getMessage();
        }

        //respond with JSON of books based on sentiment score
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }//doGet

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


    /**
     * Determines an appropriate query parameter for books api based on the sentiment score.
     * @param score sentiment score.
     * @return String containing the query parameter.
     */
    private String determineQueryParameter(double score, double magnitude) {
        boolean clearlyPositive = (score >= 0.25) && (score <= 1);
        boolean clearlyNegative = (score >= -1) && (score <= -0.25);
        boolean isMixed = ((score > -0.25) && (score < 0.25)) && (score != 0);

        if(clearlyPositive) {
            return "positive+books";
        } else if (clearlyNegative) {
            return "self+help+books";
        } else if(isMixed){               
            return "motivational+books";
        } 

        //neutral book
        return "thriller";
    }
  
}//SentimentServlet
