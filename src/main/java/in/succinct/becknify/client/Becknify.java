package in.succinct.becknify.client;

import in.succinct.beckn.Request;

import java.util.List;

public class Becknify {
    private static volatile Becknify sSoleInstance;

    //private constructor.
    private Becknify() {
        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Becknify getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (Becknify.class) {
                if (sSoleInstance == null) sSoleInstance = new Becknify();
            }
        }

        return sSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected Becknify readResolve() {
        return getInstance();
    }

    public Client build(){
        return new Client();
    }
    /*
    public void sampleCall(){
        Request request = new Request();
        String myappid = "what ever is your app id";
        String mysecret = "what ever is your secret";

        Client client = Becknify.getInstance().
                build().domain("local_retail").
                network("ondc").
                auth(myappid,mysecret);
        List<Request> onSearch = client.search(request);
        ..

    }

     */

    String getUrl(){
        return "https://becknify.humbhionline.in";
    }
}
