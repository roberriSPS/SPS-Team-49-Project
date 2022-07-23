package com.google.sps.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;
import org.apache.hc.core5.http.ParseException;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

import java.util.Random;

import java.util.*;
import java.net.*;


@WebServlet("/spotify")
public class SpotifyServlet extends HttpServlet {

   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

        String message = request.getParameter("message");

        Document doc =
            Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        double score = sentiment.getScore();
        languageService.close();

        String queryParameter = typeOfSongs(score);

        String spotifyPlaylistID = "";
        try {
            spotifyPlaylistID = fetchSpotifyApi(queryParameter);
        } catch(Exception e) {
            e.getMessage();
        }
        response.setContentType("text/html;");
        response.getWriter().println(spotifyPlaylistID);
    }

    private String fetchSpotifyApi(String queryParameter) throws Exception {

        int low = 0;
        int high = 10;
        int r = (int) (Math.random() * (high - low)) + low;

        String responseString = "";

        String client_id = "65240d81eab94dad85f467a4de4b2918"; //your client id
        String client_secret = "d9ec0c6e205c4ef3bc5944065bd18444"; //your client secret
        final String q = queryParameter;
        
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setClientId(client_id)
        .setClientSecret(client_secret)
        .build();

        final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();
        final ClientCredentials clientCredentials = clientCredentialsFuture.join();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        
        final SearchPlaylistsRequest searchPlaylistsRequest = spotifyApi.searchPlaylists(q)
//          .market(CountryCode.SE)
          .limit(10)
//          .offset(0)
//          .includeExternal("audio")
        .build();

        try {
            final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = searchPlaylistsRequest.executeAsync();
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = pagingFuture.join();
            responseString = playlistSimplifiedPaging.getItems()[r].getId().toString();
        } 
        catch (CompletionException e) {
        responseString = e.getCause().getMessage();
        } 
        catch (CancellationException e) {
        responseString = "Async operation cancelled.";
        }

        return responseString;
    }

    private String typeOfSongs(double score) {
        boolean clearlyPositive = (score >= 0.25) && (score <= 1);
        boolean clearlyNegative = (score >= -1) && (score <= -0.25);
        boolean isMixed = (score > -0.25) && (score < 0.25);

        if(clearlyPositive) {
            return "happy+songs";
        } else if (clearlyNegative) {
            return "sad+songs";
        } else if(isMixed){               
            return "motivational+songs";
        } 
        //neutral book
        return "thriller+songs";
    }
}

