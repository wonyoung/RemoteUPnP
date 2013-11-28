package com.wonyoung.remoteupnp.ui;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.lastchange.Event;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wonyoung.remoteupnp.PlaylistAdapter;
import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.UPnPService;

/**
 * Created by wonyoungjang on 13. 10. 18..
 */
public class PlaylistFragment extends Fragment {
    private UPnPService uPnPService;
    private PlaylistAdapter adapter;

	private void playFrom(int p0)
	{
		if (p0 >= adapter.getCount()) {
			return;
		}
	    Item item = (Item) adapter.getItem(p0);
		String url = item.getFirstResource().getValue();
		playNext(url, p0+ 1);
	}

	private void playNext(String url, final int p0)
	{
		Device device = uPnPService.getRendererDevice();
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
			final ActionCallback playAction = new Play(service) {
				@Override
				public void success(ActionInvocation p1) {
					playFrom(p0);
				}
				
                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };


            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, url, "NO METADATA") {
				@Override
				public void success(ActionInvocation p1) {
					uPnPService.execute(playAction);
				}
				@Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };


            uPnPService.execute(setAVTransportURIAction);
        }
	}
          

    private void play(String url) {
        Device device = uPnPService.getRendererDevice();
        Service service = device.findService(new UDAServiceId("AVTransport"));

        if (service != null) {
            final SubscriptionCallback callback = new SubscriptionCallback(service) {
                
                @Override
                protected void failed(GENASubscription sub, UpnpResponse response,
                        Exception ex, String arg3) {
                    Log.d("remoteUpnp", arg3 + " "+ createDefaultFailureMessage(response, ex));
                }
                
                @Override
                protected void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                    Log.d("remoteUpnp", "Missed events: " + numberOfMissedEvents);
                }
                
                @Override
                protected void eventReceived(GENASubscription sub) {
                    Log.d("remoteUpnp", "Event: " + sub.getCurrentSequence().getValue());
                    Map<String, StateVariableValue> values = sub.getCurrentValues();
                    StateVariableValue value = values.get("LastChange");
                    
                    for (Entry<String, StateVariableValue> entry : values.entrySet()) {
                        Log.d("remoteUpnp", entry.getKey() + " is: " + entry.getValue().toString());
                        
                    }
                }
                
                @Override
                protected void established(GENASubscription sub) {
                    Log.d("remoteUpnp", "Established: " + sub.getSubscriptionId());
                }
                
                @Override
                protected void ended(GENASubscription sub, CancelReason arg1,
                        UpnpResponse arg2) {
                    Log.d("remoteUpnp", "ended: " + sub.getSubscriptionId());
                }
            };
            
			final ActionCallback playAction = new Play(service) {
                @Override
                public void success(ActionInvocation p1) {
                    uPnPService.execute(callback);
                }
                @Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };


            ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, url, "NO METADATA") {
				@Override
				public void success(ActionInvocation p1) {
					uPnPService.execute(playAction);
				}
				@Override
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                }
            };


            uPnPService.execute(setAVTransportURIAction);
        }
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_layout, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        this.uPnPService = activity.getUPnPService();

        adapter = uPnPService.getPlaylistAdapter();
        ListView listView = (ListView) activity.findViewById(R.id.playlistView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					DIDLObject item = (DIDLObject) adapter.getItem(position);

					Res resource = item.getFirstResource();
					if (resource != null) {
						play(resource.getValue());
					}
					Toast.makeText(activity.getApplicationContext(), "" + resource.getDuration(),
					Toast.LENGTH_SHORT).show();
				}
			});
			
		Button playAll = (Button) activity.findViewById(R.id.playAll);
		playAll.setOnClickListener(new View.OnClickListener() {

				public void onClick(View p1)
				{
					playFrom(0);
				}

		});
		
    }

}

