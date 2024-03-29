/**
 * 
 */
package com.tenline.pinecone.platform.sdk;

import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.EventParser;
import nl.justobjects.pushlet.core.Protocol;
import nl.justobjects.pushlet.util.PushletException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.util.Map;

import sun.misc.BASE64Encoder;

/**
 * 
 * @author Bill
 *
 */
@SuppressWarnings("restriction")
public class ChannelClient implements Protocol {
	
	/**
	 * Pushlet URL.
	 */
	private String pushletURL;

	/**
	 * Debug flag for verbose output.
	 */
	private boolean debug;

	/**
	 * Id gotten on join ack
	 */
	private String id;

	/**
	 * Internal listener for data events pushed by server.
	 */
	protected DataEventListener dataEventListener;
	
	/**
	 * Base64 Encoder
	 */
	private String username; public void setUsername(String username) {this.username = username;}
	private String password; public void setPassword(String password) {this.password = password;}
	private BASE64Encoder encoder = new BASE64Encoder();

	/**
	 * Constructor with full pushlet URL.
	 */
	public ChannelClient(String baseURL) {
		pushletURL = baseURL + "/channel";
	}

	/**
	 * Constructor with host and port using default URI.
	 */
	public ChannelClient(String aHost, int aPort) {
		this("http://" + aHost + ":" + aPort + DEFAULT_SERVLET_URI);
	}

	/**
	 * Set proxy options and optional proxy authentication.
	 * Usage:
	 * PushletClient pushletClient = new PushletClient("http:://www.domain.com/pushlet");
	 * pushletClient.setProxyOptions("proxy.bla.com", "8080", ....);
	 */
	public void setProxyOptions(String aProxyHost,
	                            String aProxyPort, String theNonProxyHosts,
	                            String aUserName, String aPassword, String anNTLMDomain) {

		// Enable proxying
		System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyHost", aProxyHost);
		System.setProperty("http.proxyPort", aProxyPort);

		// Set optional non-proxy hosts
		if (theNonProxyHosts != null) {
			System.setProperty("http.nonProxyHosts", theNonProxyHosts);
		}

		// If user name specified configure proxy authentication
		if (aUserName != null) {
			System.setProperty("http.proxyUser", aUserName);
			System.setProperty("http.proxyPassword", aPassword);

			// See inner class below
			Authenticator.setDefault(new HTTPAuthenticateProxy(aUserName, aPassword));

			// Optional NT domain
			if (anNTLMDomain != null) {
				System.setProperty("http.auth.ntlm.domain", anNTLMDomain);
			}
		}
	}

	/**
	 * Join server, starts session.
	 */
	public void join() throws PushletException {
		Event event = new Event(E_JOIN);
		event.setField(P_FORMAT, FORMAT_XML);
		Event response = doControl(event);
		throwOnNack(response);

		// Join Ack received
		id = response.getField(P_ID);
	}

	/**
	 * Leave server, stops session.
	 */
	public void leave() throws PushletException {
		stopListen();
		throwOnInvalidSession();
		Event event = new Event(E_LEAVE);
		event.setField(P_ID, id);
		Event response = doControl(event);

		throwOnNack(response);
		id = null;
	}

	/**
	 * Open data channel.
	 */
	public void listen(ChannelClientListener aListener) throws PushletException {
		listen(aListener, MODE_STREAM);
	}

	/**
	 * Open data channel in stream or push mode.
	 */
	public void listen(ChannelClientListener aListener, String aMode) throws PushletException {
		listen(aListener, aMode, null);
	}

	/**
	 * Open data channel in stream or push mode with a subject.
	 */
	public void listen(ChannelClientListener aListener, String aMode, String aSubject) throws PushletException {
		throwOnInvalidSession();
		stopListen();

		String listenURL = pushletURL
				+ "?" + P_EVENT + "=" + E_LISTEN
				+ "&" + P_ID + "=" + id
				+ "&" + P_MODE + "=" + aMode;
		if (aSubject != null) {
			listenURL = listenURL + "&" + P_SUBJECT + "=" + aSubject;
		}

		// Start listener thread (sync call).
		startDataEventListener(aListener, listenURL);
	}

	/**
	 * Immediate listener: joins/subscribes and listens in one action.
	 */
	public void joinListen(ChannelClientListener aListener, String aMode, String aSubject) throws PushletException {
		stopListen();

		String listenURL = pushletURL
				+ "?" + P_EVENT + "=" + E_JOIN_LISTEN
				+ "&" + P_FORMAT + "=" + FORMAT_XML
				+ "&" + P_MODE + "=" + aMode
				+ "&" + P_SUBJECT + "=" + aSubject;

		// Start listener thread (sync call).
		startDataEventListener(aListener, listenURL);
	}

	/**
	 * Publish an event through server.
	 */
	@SuppressWarnings("rawtypes")
	public void publish(String aSubject, Map theAttributes) throws PushletException {
		throwOnInvalidSession();
		Event event = new Event(E_PUBLISH, theAttributes);
		event.setField(P_SUBJECT, aSubject);
		event.setField(P_ID, id);
		Event response = doControl(event);
		throwOnNack(response);
	}

	/**
	 * Subscribes, returning subscription id.
	 */
	public String subscribe(String aSubject, String aLabel) throws PushletException {
		throwOnInvalidSession();
		Event event = new Event(E_SUBSCRIBE);
		event.setField(P_ID, id);
		event.setField(P_SUBJECT, aSubject);

		// Optional label, is returned in data events
		if (aLabel != null) {
			event.setField(P_SUBSCRIPTION_LABEL, aLabel);
		}

		// Send request
		Event response = doControl(event);
		throwOnNack(response);

		return response.getField(P_SUBSCRIPTION_ID);
	}

	/**
	 * Subscribes, returning subscription id.
	 */
	public String subscribe(String aSubject) throws PushletException {
		return subscribe(aSubject, null);
	}

	/**
	 * Unsubscribes with subscription id.
	 */
	public void unsubscribe(String aSubscriptionId) throws PushletException {
		throwOnInvalidSession();
		Event event = new Event(E_UNSUBSCRIBE);
		event.setField(P_ID, id);

		// Optional subscription id
		if (aSubscriptionId != null) {
			event.setField(P_SUBSCRIPTION_ID, aSubscriptionId);
		}

		Event response = doControl(event);
		throwOnNack(response);
	}

	/**
	 * Unsubscribes from all subjects.
	 */
	public void unsubscribe() throws PushletException {
		unsubscribe(null);
	}

	/**
	 * Stop the listener.
	 */
	public void stopListen() throws PushletException {
		if (dataEventListener != null) {
			unsubscribe();
			dataEventListener.stop();
			dataEventListener = null;
		}
	}

	public void setDebug(boolean b) {
		debug = b;
	}

	/**
	 * Starts default DataEventListener and waits for its thread to start.
	 */
	protected void startDataEventListener(ChannelClientListener aListener, String aListenURL) {
		// Suggestion by Jeff Nowakowski 29.oct.2006
		dataEventListener = new DataEventListener(aListener, aListenURL);

		synchronized (dataEventListener) {
			dataEventListener.start();
			try {
				// Wait for data event listener (thread) to start
				dataEventListener.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	protected void throwOnNack(Event anEvent) throws PushletException {
		if (anEvent.getEventType().equals(E_NACK)) {
			throw new PushletException("Negative response: reason=" + anEvent.getField(P_REASON));
		}
	}

	protected void throwOnInvalidSession() throws PushletException {
		if (id == null) {
			throw new PushletException("Invalid pushlet session");
		}
	}

	protected Reader openURL(String aURL) throws PushletException {
		// Open URL connection with server
		try {
			p("Connecting to " + aURL);
			URL url = new URL(aURL);
			URLConnection urlConnection = url.openConnection();

			// Disable any kind of caching.
			urlConnection.setUseCaches(false);
			urlConnection.setDefaultUseCaches(false);
			
			// HTTP Basic Authorization
			urlConnection.setRequestProperty("Authorization", "Basic " + encoder.encode((username + ":" + password).getBytes()));

			// TODO: later version may use POST
			// Enable HTTP POST
			// urlConnection.setDoOutput(true);

			// Do the POST with Event in XML in body
			// OutputStream os = urlConnection.getOutputStream();
			// os.write(anEvent.toXML().getBytes());
			// os.flush();
			// os.close();

			// Get the stream from the server.
			// reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			// Note: somehow the client does not work with some JVMs when using
			// BufferedInputStream... So do unbuffered input.
			// p("Opening urlConnection inputstream");
			return new InputStreamReader(urlConnection.getInputStream());

		} catch (Throwable t) {
			warn("openURL() could not open " + aURL, t);
			throw new PushletException(" could not open " + aURL, t);
		}
	}


	/**
	 * Send control events to server and return response.
	 */
	protected Event doControl(Event aControlEvent) throws PushletException {
		String controlURL = pushletURL + "?" + aControlEvent.toQueryString();

		p("doControl to " + controlURL);

		// Open URL connection with server
		Reader reader = openURL(controlURL);

		// Get Pushlet event from stream
		Event event = null;
		try {
			p("Getting event...");
			// Get next event from server
			event = EventParser.parse(reader);
			p("Event received " + event);
			return event;
		} catch (Throwable t) {
			// Stop and report error.
			warn("doControl() exception", t);
			throw new PushletException(" error parsing response from" + controlURL, t);
		}
	}

	/**
	 * Util: print.
	 */
	protected void p(String s) {
		if (debug) {
			System.out.println("[PushletClient] " + s);
		}
	}

	/**
	 * Util: warn.
	 */
	protected void warn(String s) {
		warn(s, null);
	}

	/**
	 * Util: warn with exception.
	 */
	protected void warn(String s, Throwable t) {
		System.err.println("[PushletClient] - WARN - " + s + " ex=" + t);

		if (t != null) {
			t.printStackTrace();
		}
	}

	/**
	 * Internal (default) listener for the Pushlet data channel.
	 */
	protected class DataEventListener implements Runnable {
		/**
		 * Client's listener that gets called back on events.
		 */
		private ChannelClientListener listener;

		/**
		 * Receiver receiveThread.
		 */
		private Thread receiveThread = null;
		private Reader reader;
		private String refreshURL;
		private String listenURL;

		public DataEventListener(ChannelClientListener aListener, String aListenURL) {
			listener = aListener;
			listenURL = aListenURL;
		}

		public void start() {
			// All ok: start a receiver receiveThread
			receiveThread = new Thread(this);
			receiveThread.start();

		}

		/**
		 * Stop listening; may restart later with start().
		 */
		public void stop() {
			p("In stop()");
			bailout();
		}

		/**
		 * Receive event objects from server and callback listener.
		 */
		public void run() {
			p("Start run()");
			try {
				while (receiveThread != null && receiveThread.isAlive()) {
					// Connect to server
					reader = openURL(listenURL);

					synchronized (this) {
						// Inform the calling thread we're ready to receive events.
						// Suggestion by Jeff Nowakowski 29.oct.2006
						this.notify();
					}

					// Get events while we're alive.
					while (receiveThread != null && receiveThread.isAlive()) {
						Event event = null;
						try {
							// p("Getting event...");
							// Get next event from server
							event = EventParser.parse(reader);
							p("Event received " + event);
						} catch (Throwable t) {

							// Stop and report error.
							// warn("Stop run() on exception", t);
							if (listener != null) {
								listener.onError("exception during receive: " + t);
							}

							break;
						}

						// Handle event by calling listener
						if (event != null && listener != null) {
							// p("received: " + event.toXML());
							String eventType = event.getEventType();
							if (eventType.equals(E_HEARTBEAT)) {
								listener.onHeartbeat(event);
							} else if (eventType.equals(E_DATA)) {
								listener.onData(event);
							} else if (eventType.equals(E_JOIN_LISTEN_ACK)) {
								id = event.getField(P_ID);
							} else if (eventType.equals(E_LISTEN_ACK)) {
								p("Listen ack ok");
							} else if (eventType.equals(E_REFRESH_ACK)) {
								// ignore
							} else if (eventType.equals(E_ABORT)) {
								listener.onAbort(event);
								listener = null;
								break;
							} else if (eventType.equals(E_REFRESH)) {
								refresh(event);
							} else {
								handleUnknownEventType(eventType, event, listener);
							}
						}
					}
				}
			} catch (Throwable t) {
				warn("Exception in run() ", t);
				// bailout();
			}
		}

		protected void disconnect() {
			p("start disconnect()");
			if (reader != null) {
				try {
					// this blocks, find another way
					// reader.close();
					p("Closed reader ok");
				} catch (Exception ignore) {
				} finally {
					reader = null;
				}
			}
			p("end disconnect()");
		}

		/**
		 * Stop receiver receiveThread.
		 */
		@SuppressWarnings("deprecation")
		public void stopThread() {
			p("In stopThread()");

			// Keep a reference such that we can kill it from here.
			Thread targetThread = receiveThread;

			receiveThread = null;

			// This should stop the main loop for this receiveThread.
			// Killing a receiveThread on a blcing read is tricky.
			// See also http://gee.cs.oswego.edu/dl/cpj/cancel.html
			if ((targetThread != null) && targetThread.isAlive()) {

				targetThread.interrupt();

				try {

					// Wait for it to die
					targetThread.join(500);
				} catch (InterruptedException ignore) {
				}

				// If current receiveThread refuses to die,
				// take more rigorous methods.
				if (targetThread.isAlive()) {

					// Not preferred but may be needed
					// to stop during a blocking read.
					targetThread.stop();

					// Wait for it to die
					try {
						targetThread.join(500);
					} catch (Throwable ignore) {
					}
				}

				p("Stopped receiveThread alive=" + targetThread.isAlive());

			}
		}

		/**
		 * Stop listening on stream from server.
		 */
		public void bailout() {
			p("In bailout()");
			stopThread();
			disconnect();
		}

		/**
		 * Handle refresh, by pausing.
		 */
		protected void refresh(Event aRefreshEvent) throws PushletException {
			try {
				// Wait for specified time.
				Thread.sleep(Long.parseLong(aRefreshEvent.getField(P_WAIT)));
			} catch (Throwable t) {
				warn("abort while refresing");
				refreshURL = null;
				return;
			}

			// If stopped during sleep, don't proceed
			if (receiveThread == null) {
				return;
			}

			// Create url to refresh
			refreshURL = pushletURL
					+ "?" + P_ID + "=" + id
					+ "&" + P_EVENT + "=" + E_REFRESH
					;

			if (reader != null) {
				try {
					reader.close();

				} catch (IOException ignore) {

				}
				reader = null;
			}

			reader = openURL(refreshURL);
		}

		/**
		 * Handle unknown Event (default behaviour).
		 */
		protected void handleUnknownEventType(String eventType, Event event, ChannelClientListener listener) {
			warn("unsupported event type received: " + eventType);
		}
	}

	/**
	 * Authenticator
	 */
	private static class HTTPAuthenticateProxy extends Authenticator {

		/**
		 * Contributed by Dele Olajide
		 * See http://groups.yahoo.com/group/pushlet/message/634
		 */

		private String thePassword = "";
		private String theUser = "";

		public HTTPAuthenticateProxy(String username, String password) {

			thePassword = password;
			theUser = username;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(theUser, thePassword.toCharArray());
		}

	}

}
