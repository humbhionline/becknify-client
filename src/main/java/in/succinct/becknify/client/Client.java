package in.succinct.becknify.client;

import com.venky.core.collections.IgnoreCaseMap;
import in.succinct.beckn.Request;
import in.succinct.beckn.Response;
import in.succinct.beckn.Subscriber;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Client {

    Client(){

    }

    String domain;
    String domainRegex="[A-z,_]+";
    public Client domain(String domain){
        this.domain = domain;
        if (!domain.matches(domainRegex)){
            throw new RuntimeException("Invalid Domain");
        }
        return this;
    }

    String networkName;
    public Client network(String name){
        this.networkName = name;
        return this;
    }

    String basic;
    String appId;
    public Client auth(String appId,String secret){
        this.appId =appId;
        this.basic = Base64.getEncoder().encodeToString(String.format("%s:%s",appId,secret).getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public String getAccessPointUrl(String role){
        try {
            return String.format("%s/%s/%s/%s/%s/api/%s",
                    Becknify.getInstance().getUrl(),
                    domain,
                    networkName,
                    appId,
                    role,
                    "bg");
        }catch (NullPointerException ex){
            return null;
        }
    }

    public List<Request> search(Request search) {
        return search(search, 10*1000L);
    }

    public List<Request> search(Request search, long timeoutMillis) {
        try {
            return _bapListApi("search",search,timeoutMillis);
        } catch (Exception e) {
            throw new RuntimeException(e); //Soften the exception.
        }
    }

    public Response on_search(Request request, long timeoutMillis){
        return _bppApi("on_search" ,request,timeoutMillis);
    }
    public Request select(Request request, long timeoutMillis){
        return _bapApi("select",request,timeoutMillis);
    }
    public Response on_select(Request request, long timeoutMillis){
        return _bppApi("on_select",request,timeoutMillis);
    }
    public Request init(Request request, long timeoutMillis){
        return _bapApi("init",request,timeoutMillis);
    }
    public Response on_init(Request request, long timeoutMillis){
        return _bppApi("on_init",request,timeoutMillis);
    }
    public Request confirm(Request request, long timeoutMillis){
        return _bapApi("confirm",request,timeoutMillis);
    }
    public Response on_confirm(Request request, long timeoutMillis){
        return _bppApi("on_confirm",request,timeoutMillis);
    }
    public Request status(Request request, long timeoutMillis){
        return _bapApi("status",request,timeoutMillis);
    }
    public Response on_status(Request request, long timeoutMillis){
        return _bppApi("on_status",request,timeoutMillis);
    }
    public Request track(Request request, long timeoutMillis){
        return _bapApi("track",request,timeoutMillis);
    }
    public Response on_track(Request request, long timeoutMillis){
        return _bppApi("on_track",request,timeoutMillis);
    }

    public Request cancel(Request request, long timeoutMillis){
        return _bapApi("cancel",request,timeoutMillis);
    }
    public Response on_cancel(Request request, long timeoutMillis){
        return _bppApi("on_cancel",request,timeoutMillis);
    }
    public Request update(Request request, long timeoutMillis){
        return _bapApi("update",request,timeoutMillis);
    }
    public Response on_update(Request request, long timeoutMillis){
        return _bppApi("on_update",request,timeoutMillis);
    }


    public Request rating(Request request, long timeoutMillis){
        return _bapApi("rating",request,timeoutMillis);
    }
    public Response on_rating(Request request, long timeoutMillis){
        return _bppApi("on_rating",request,timeoutMillis);
    }

    public Request support(Request request, long timeoutMillis){
        return _bapApi("support",request,timeoutMillis);
    }
    public Response on_support(Request request, long timeoutMillis){
        return _bppApi("on_support",request,timeoutMillis);
    }

    public Request get_cancellation_reasons(Request request, long timeoutMillis){
        return _bapApi("get_cancellation_reasons",request,timeoutMillis);
    }
    public Response cancellation_reasons(Request request, long timeoutMillis){
        return _bppApi("cancellation_reasons",request,timeoutMillis);
    }

    public Request get_return_reasons(Request request, long timeoutMillis){
        return _bapApi("get_return_reasons",request,timeoutMillis);
    }
    public Response return_reasons(Request request, long timeoutMillis){
        return _bppApi("return_reasons",request,timeoutMillis);
    }

    public Request get_rating_categories(Request request, long timeoutMillis){
        return _bapApi("get_rating_categories",request,timeoutMillis);
    }
    public Response rating_categories(Request request, long timeoutMillis){
        return _bppApi("rating_categories",request,timeoutMillis);
    }

    public Request get_feedback_categories(Request request, long timeoutMillis){
        return _bapApi("get_feedback_categories",request,timeoutMillis);
    }
    public Response feedback_categories(Request request, long timeoutMillis){
        return _bppApi("feedback_categories",request,timeoutMillis);
    }

    public Request get_feedback_form(Request request, long timeoutMillis){
        return _bapApi("get_feedback_form",request,timeoutMillis);
    }
    public Response feedback_form(Request request, long timeoutMillis){
        return _bppApi("feedback_form",request,timeoutMillis);
    }

    private Request _bapApi(String action, Request request, long timeoutMillis) {
        List<Request> requests = _bapListApi(action,request,timeoutMillis);
        return requests.isEmpty() ?null : requests.get(0);
    }

    private List<Request> _bapListApi(String action, Request request, long timeoutMillis) {
        try {
            request.getContext().setAction(action);
            InputStream in = invoke(action,Subscriber.SUBSCRIBER_TYPE_BAP,request, timeoutMillis);
            JSONAware jsonAware = (JSONAware) JSONValue.parse(new InputStreamReader(in));
            List<Request> callbacks = new ArrayList<>();
            if (jsonAware != null ) {
                if (jsonAware instanceof JSONArray array){
                    for (Object o : array) {
                        callbacks.add(new Request((JSONObject) o));
                    }
                }else {
                    callbacks.add(new Request((JSONObject) jsonAware));
                }
            }
            return callbacks;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public Response _bppApi(String action, Request request, long timeoutMillis){
        try {
            request.getContext().setAction(action);
            InputStream in = invoke(action,Subscriber.SUBSCRIBER_TYPE_BPP,request, timeoutMillis);
            JSONObject out = (JSONObject) JSONValue.parse(new InputStreamReader(in));
            JSONArray array = (JSONArray) out.get("responses");
            if (array.size() > 0){
                return new Response((JSONObject) array.get(0));
            }else {
                return new Response(new JSONObject());
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Map<String,String> headers(){
        return new IgnoreCaseMap<>(){{
            put("Accept-Encoding","gzip");
            put("Content-Type","application/json");
            put("X-CallBackToBeSynchronized","Y");
            put("Authorization", String.format("Basic %s",basic));
        }};
    }

    Map<String,String> responseHeaders = new IgnoreCaseMap<>();

    public Map<String,String> getResponseHeaders(){
        return responseHeaders;
    }

    int status;
    public InputStream invoke(String action, String myRole, Request request,long timeoutMillis) throws URISyntaxException, IOException,InterruptedException {
        request.getContext().setAction(action);
        Builder curlBuilder = HttpRequest.newBuilder().uri(new URI(String.format("%s/%s",getAccessPointUrl(myRole),request.getContext().getAction())));
        byte[] parameterByteArray = request.getInner().toString().getBytes(StandardCharsets.UTF_8);
        curlBuilder.POST(BodyPublishers.ofByteArray(parameterByteArray));
        curlBuilder.version(Version.HTTP_2);
        headers().forEach((k,v)->{
            curlBuilder.header(k,v);
        });

        curlBuilder.timeout(Duration.ofMillis(timeoutMillis));

        HttpRequest httpRequest  = curlBuilder.build();
        HttpResponse<InputStream> response = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build().send(httpRequest, BodyHandlers.ofInputStream());

        this.status = response.statusCode();

        response.headers().map().forEach((k,v)->{
            responseHeaders.put(k,v.get(0));
        });

        return responseHeaders.containsKey("Content-Encoding") ? new GZIPInputStream(response.body()) : response.body();
    }

    public int getStatus() {
        return status;
    }

    public boolean hasErrors(){
        return (this.status < 200 || this.status >=300);
    }
}
