# Java Client for Becknify Apis.
[Becknify](https://becknify.humbhionline.in) is an api platform to integrate with beckn networks.
## Usage 
###/search:

	Request request = new Request();
		...Set the attributes...
        String myappid = "what ever is your app id";
        String mysecret = "what ever is your secret";

        Client client = Becknify.getInstance().
                build().domain("local_retail").
                network("ondc").
                auth(myappid,mysecret);
                
        List<Request> onSearch = client.search(request); 
        //Has responsees from all bpps in the network.

###/select:

	Request request = new Request();
		... Set Attributes....
        String myappid = "what ever is your app id";
        String mysecret = "what ever is your secret";

        Client client = Becknify.getInstance().
                build().domain("local_retail").
                network("ondc").
                auth(myappid,mysecret);
                
        Request onSelect = client.select(request,2); 
		//Fires  select on the BPP